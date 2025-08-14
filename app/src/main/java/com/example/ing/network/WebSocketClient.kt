package com.example.ing.network

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.TimeUnit
import java.net.InetAddress
import kotlinx.coroutines.*

class AutomotiveWebSocketClient(
    private val serverUrl: String,
    private val onMessageReceived: (String) -> Unit,
    private val onConnectionEstablished: () -> Unit,
    private val onConnectionClosed: () -> Unit,
    private val onError: (String) -> Unit
) : WebSocketClient(createURI(serverUrl)) {

    private val TAG = "AutomotiveWebSocketClient"
    private var isConnecting = false
    private var heartbeatJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private fun createURI(serverUrl: String): URI {
            return try {
                // Extraer IP y puerto de la URL
                val url = serverUrl.replace("ws://", "")
                val parts = url.split(":")
                val ip = parts[0]
                val port = parts[1].toInt()
                
                // Crear URI con IP directa para evitar resolución DNS
                URI("ws", null, ip, port, "/", null, null)
            } catch (e: Exception) {
                Log.e("AutomotiveWebSocketClient", "Error creando URI: ${e.message}")
                // Fallback a URI normal si hay error
                URI(serverUrl)
            }
        }
    }

    init {
        // Configuración optimizada del WebSocket
        this.setConnectionLostTimeout(30)
        this.setTcpNoDelay(true)
        this.setReuseAddr(true)
        
        Log.d(TAG, "🔧 WebSocketClient inicializado para: $serverUrl")
        Log.d(TAG, "🔧 URI: ${uri}")
        Log.d(TAG, "🔧 Host: ${uri.host}")
        Log.d(TAG, "🔧 Port: ${uri.port}")
        Log.d(TAG, "🔧 Path: ${uri.path}")
        
        // Verificar si la IP se está resolviendo correctamente
        try {
            val hostAddress = InetAddress.getByName(uri.host)
            Log.d(TAG, "🔍 Resolución DNS - IP original: ${uri.host}")
            Log.d(TAG, "🔍 Resolución DNS - IP resuelta: ${hostAddress.hostAddress}")
            Log.d(TAG, "🔍 Resolución DNS - HostName: ${hostAddress.hostName}")
            
            if (hostAddress.hostAddress != uri.host) {
                Log.w(TAG, "⚠️ ADVERTENCIA: La IP se está resolviendo a una dirección diferente!")
                Log.w(TAG, "⚠️ IP original: ${uri.host}")
                Log.w(TAG, "⚠️ IP resuelta: ${hostAddress.hostAddress}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error resolviendo DNS: ${e.message}")
        }
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d(TAG, "✅ CONEXIÓN EXITOSA al servidor Automotive: $serverUrl")
        Log.d(TAG, "✅ Handshake: ${handshakedata?.httpStatus} - ${handshakedata?.httpStatusMessage}")
        Log.d(TAG, "✅ Protocolo: ${handshakedata?.httpStatusMessage}")
        
        isConnecting = false
        
        // Iniciar heartbeat
        startHeartbeat()
        
        onConnectionEstablished()
    }

    override fun onMessage(message: String?) {
        message?.let {
            Log.d(TAG, "📨 Mensaje recibido: $it")
            
            // Si es un heartbeat, no procesarlo como mensaje normal
            if (it.startsWith("PING") || it.startsWith("HEARTBEAT")) {
                Log.d(TAG, "💓 Heartbeat recibido, enviando PONG")
                send("PONG")
                return
            }
            
            onMessageReceived(it)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "❌ DESCONEXIÓN del servidor: $reason")
        Log.d(TAG, "❌ Código: $code")
        Log.d(TAG, "❌ Remoto: $remote")
        Log.d(TAG, "❌ URL: $serverUrl")
        
        isConnecting = false
        stopHeartbeat()
        onConnectionClosed()
    }

    override fun onError(ex: Exception?) {
        val errorMessage = ex?.message ?: "Error desconocido"
        Log.e(TAG, "💥 ERROR CRÍTICO en WebSocket: $errorMessage")
        Log.e(TAG, "💥 URL: $serverUrl")
        Log.e(TAG, "💥 URI: ${uri}")
        Log.e(TAG, "Stack trace completo:", ex)
        
        isConnecting = false
        stopHeartbeat()
        onError(errorMessage)
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = coroutineScope.launch {
            while (isOpen) {
                try {
                    delay(30000) // Heartbeat cada 30 segundos
                    if (isOpen) {
                        Log.d(TAG, "💓 Enviando heartbeat...")
                        send("PING")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "💥 Error enviando heartbeat: ${e.message}")
                    break
                }
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun sendMessage(message: String) {
        if (isOpen) {
            try {
            send(message)
                Log.d(TAG, "📤 Mensaje enviado exitosamente: $message")
            } catch (e: Exception) {
                Log.e(TAG, "💥 Error enviando mensaje: ${e.message}")
                Log.e(TAG, "Stack trace:", e)
                onError("Error enviando mensaje: ${e.message}")
            }
        } else {
            Log.w(TAG, "⚠️ No se puede enviar mensaje: conexión cerrada")
            Log.w(TAG, "⚠️ Estado de conexión: isOpen=$isOpen, isConnecting=$isConnecting")
            onError("No se puede enviar mensaje: conexión cerrada")
        }
    }

    fun isConnected(): Boolean = isOpen

    fun connectWithTimeout(timeoutSeconds: Int = 10) {
        try {
            Log.d(TAG, "🔌 INICIANDO CONEXIÓN a: $serverUrl")
            Log.d(TAG, "🔌 URI completa: ${uri}")
            Log.d(TAG, "🔌 Host: ${uri.host}")
            Log.d(TAG, "🔌 Puerto: ${uri.port}")
            Log.d(TAG, "🔌 Timeout: $timeoutSeconds segundos")
            
            // Verificar que el cliente no esté ya conectado o conectándose
            if (isOpen) {
                Log.w(TAG, "⚠️ Cliente ya está conectado")
                Log.w(TAG, "⚠️ Estado: isOpen=$isOpen")
                return
            }
            
            if (isConnecting) {
                Log.w(TAG, "⚠️ Cliente ya está intentando conectar")
                Log.w(TAG, "⚠️ Estado: isConnecting=$isConnecting")
                return
            }
            
            isConnecting = true
            Log.d(TAG, "⏳ Iniciando conexión con timeout de $timeoutSeconds segundos...")
            Log.d(TAG, "⏳ Estado antes de conectar: isOpen=$isOpen, isConnecting=$isConnecting")
            
            // Usar connectBlocking directamente en lugar de connect() + connectBlocking()
            val success = connectBlocking(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            
            Log.d(TAG, "📊 Resultado de conexión: success=$success")
            Log.d(TAG, "📊 Estado después de conectar: isOpen=$isOpen, isConnecting=$isConnecting")
            
            if (success) {
                Log.d(TAG, "✅ CONEXIÓN ESTABLECIDA EXITOSAMENTE")
                Log.d(TAG, "✅ URL: $serverUrl")
                Log.d(TAG, "✅ Estado final: isOpen=$isOpen")
            } else {
                Log.e(TAG, "⏰ TIMEOUT al conectar después de $timeoutSeconds segundos")
                Log.e(TAG, "⏰ URL: $serverUrl")
                Log.e(TAG, "⏰ Estado final: isOpen=$isOpen")
                onError("Timeout al conectar después de $timeoutSeconds segundos")
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 ERROR CRÍTICO en connectWithTimeout: ${e.message}")
            Log.e(TAG, "💥 URL: $serverUrl")
            Log.e(TAG, "💥 URI: ${uri}")
            Log.e(TAG, "Stack trace completo:", e)
            isConnecting = false
            onError("Error conectando: ${e.message}")
        }
    }

    fun disconnect() {
        Log.d(TAG, "🔌 Desconectando manualmente...")
        stopHeartbeat()
        close()
    }

    fun getConnectionStatus(): String {
        val status = when {
            isOpen -> "Conectado"
            isConnecting -> "Conectando..."
            else -> "Desconectado"
        }
        Log.d(TAG, "📊 Estado de conexión: $status (isOpen=$isOpen, isConnecting=$isConnecting)")
        return status
    }

    fun cleanup() {
        coroutineScope.cancel()
        stopHeartbeat()
    }
} 