package com.example.ing.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.NetworkInterface

class ConnectionViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ConnectionViewModel"
    private var webSocketClient: AutomotiveWebSocketClient? = null

    // Estados del UI
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _statusMessage = MutableStateFlow("No conectado")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _receivedMessages = MutableStateFlow<List<String>>(emptyList())
    val receivedMessages: StateFlow<List<String>> = _receivedMessages.asStateFlow()

    private val _messageToSend = MutableStateFlow("")
    val messageToSend: StateFlow<String> = _messageToSend.asStateFlow()

    fun updateServerUrl(url: String) {
        Log.d(TAG, "URL actualizada: $url")
        _serverUrl.value = url
    }

    fun updateMessageToSend(message: String) {
        _messageToSend.value = message
    }

    fun connectAutomatically() {
        Log.d(TAG, "🚀 Iniciando conexión automática...")

        if (_isConnecting.value) {
            Log.w(TAG, "⚠️ Ya intentando conectar...")
            _statusMessage.value = "Ya intentando conectar..."
            return
        }

        viewModelScope.launch {
            try {
                _isConnecting.value = true
                _statusMessage.value = "Buscando servidor automáticamente..."

                // Cerrar conexión existente si hay una
                Log.d(TAG, "🔌 Cerrando conexión existente...")
                disconnectFromAutomotive()

                // Obtener IPs de la red local
                val localIps = getLocalNetworkIPs()
                Log.d(TAG, "🌐 IPs de red local encontradas: $localIps")

                // Intentar conectar a cada IP
                for (ip in localIps) {
                    val url = "ws://$ip:3000"
                    Log.d(TAG, "🔍 Probando conexión a: $url")
                    _statusMessage.value = "Probando: $url"

                    if (tryConnectToUrl(url)) {
                        Log.d(TAG, "✅ Conexión exitosa a: $url")
                        _serverUrl.value = url
                        _statusMessage.value = "Conectado automáticamente a: $url"
                        return@launch
                    } else {
                        Log.w(TAG, "❌ Falló conexión a: $url")
                    }
                }

                // Si no se conectó a ninguna IP, mostrar error
                Log.e(TAG, "💥 No se pudo conectar a ningún servidor")
                _statusMessage.value = "No se encontró ningún servidor WebSocket en la red"
                _isConnecting.value = false

            } catch (e: Exception) {
                Log.e(TAG, "💥 Error en conexión automática: ${e.message}")
                Log.e(TAG, "Stack trace:", e)
                _isConnected.value = false
                _isConnecting.value = false
                _statusMessage.value = "Error en conexión automática: ${e.message}"
            }
        }
    }

    fun connectToAutomotive() {
        Log.d(TAG, "🔄 Iniciando proceso de conexión manual...")

        if (_isConnecting.value) {
            Log.w(TAG, "⚠️ Ya intentando conectar...")
            return
        }

        viewModelScope.launch {
            try {
                _isConnecting.value = true
                _statusMessage.value = "Iniciando conexión WebSocket manual..."

                // Cerrar conexión existente si hay una
                Log.d(TAG, "🔌 Cerrando conexión existente...")
                disconnectFromAutomotive()

                val url = _serverUrl.value
                if (url.isBlank()) {
                    _statusMessage.value = "Error: URL del servidor no especificada"
                    _isConnecting.value = false
                    return@launch
                }

                Log.d(TAG, "🔍 Validando URL: $url")
                if (!url.startsWith("ws://")) {
                    _statusMessage.value = "Error: URL debe comenzar con ws://"
                    _isConnecting.value = false
                    return@launch
                }

                Log.d(TAG, "✅ URL válida, creando cliente WebSocket...")

                // Crear nueva instancia del cliente WebSocket
                webSocketClient = AutomotiveWebSocketClient(
                    serverUrl = url,
                    onMessageReceived = { message ->
                        Log.d(TAG, "📨 Mensaje recibido: $message")
                        _receivedMessages.value = _receivedMessages.value + message
                    },
                    onConnectionEstablished = {
                        Log.d(TAG, "✅ Conexión establecida")
                        _isConnected.value = true
                        _isConnecting.value = false
                        _statusMessage.value = "Conectado a: $url"
                    },
                    onConnectionClosed = {
                        Log.d(TAG, "❌ Conexión cerrada")
                        _isConnected.value = false
                        _isConnecting.value = false
                        _statusMessage.value = "Desconectado"
                    },
                    onError = { error ->
                        Log.e(TAG, "💥 Error conectando a $url: $error")
                        _isConnected.value = false
                        _isConnecting.value = false
                        _statusMessage.value = "Error: $error"
                    }
                )

                Log.d(TAG, "🔌 Cliente WebSocket creado, iniciando conexión...")

                // Ejecutar conexión en hilo IO
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "⏳ Ejecutando conexión en hilo IO...")
                    webSocketClient?.connectWithTimeout(10)
                }

                Log.d(TAG, "⏰ Esperando resultado de conexión...")

            } catch (e: Exception) {
                Log.e(TAG, "💥 Error en conexión manual: ${e.message}")
                Log.e(TAG, "Stack trace:", e)
                _isConnected.value = false
                _isConnecting.value = false
                _statusMessage.value = "Error en conexión: ${e.message}"
            }
        }
    }

    private suspend fun tryConnectToUrl(url: String): Boolean {
        return try {
            Log.d(TAG, "🔌 Intentando conectar a: $url")

            // Crear una instancia temporal para probar la conexión
            val tempClient = AutomotiveWebSocketClient(
                serverUrl = url,
                onMessageReceived = { /* No procesar mensajes durante prueba */ },
                onConnectionEstablished = { /* No procesar durante prueba */ },
                onConnectionClosed = { /* No procesar durante prueba */ },
                onError = { /* No procesar durante prueba */ }
            )

            // Intentar conectar con timeout corto
            withContext(Dispatchers.IO) {
                tempClient.connectWithTimeout(3) // Solo 3 segundos para prueba
            }

            // Verificar si se conectó exitosamente
            val success = tempClient.isConnected()

            if (success) {
                Log.d(TAG, "✅ Conexión exitosa a: $url")
                tempClient.disconnect() // Cerrar conexión de prueba
                true
            } else {
                Log.w(TAG, "❌ Falló conexión a: $url")
                tempClient.cleanup()
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "❌ Falló conexión a: $url: ${e.message}")
            false
        }
    }

    fun disconnectFromAutomotive() {
        Log.d(TAG, "🔌 Desconectando...")

        webSocketClient?.let { client ->
            try {
                client.disconnect()
                client.cleanup()
            } catch (e: Exception) {
                Log.e(TAG, "💥 Error desconectando: ${e.message}")
            }
        }

        webSocketClient = null
        _isConnected.value = false
        _isConnecting.value = false
        Log.d(TAG, "✅ Desconexión completada")
    }

    fun sendMessage() {
        val message = _messageToSend.value.trim()
        if (message.isBlank()) {
            _statusMessage.value = "Error: Mensaje vacío"
            return
        }

        webSocketClient?.let { client ->
            if (client.isConnected()) {
                client.sendMessage(message)
                _messageToSend.value = ""
                _statusMessage.value = "Mensaje enviado: $message"
            } else {
                _statusMessage.value = "Error: No conectado al servidor"
            }
        } ?: run {
            _statusMessage.value = "Error: Cliente WebSocket no disponible"
        }
    }

    fun clearMessages() {
        _receivedMessages.value = emptyList()
        _statusMessage.value = "Mensajes limpiados"
    }

    private fun getLocalNetworkIPs(): List<String> {
        val ips = mutableListOf<String>()

        try {
            // Obtener IPs de interfaces de red
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()

                if (!networkInterface.isLoopback && networkInterface.isUp) {
                    Log.d(TAG, "📡 Interfaz encontrada: ${networkInterface.displayName}")

                    val inetAddresses = networkInterface.inetAddresses
                    while (inetAddresses.hasMoreElements()) {
                        val inetAddress = inetAddresses.nextElement()

                        if (!inetAddress.isLoopbackAddress &&
                            inetAddress.hostAddress?.indexOf(':') ?: -1 < 0 &&
                            inetAddress.hostAddress?.startsWith("192.168.") == true) {

                            val ip = inetAddress.hostAddress
                            if (ip != null) {
                                Log.d(TAG, "🌐 IP encontrada: $ip")
                                ips.add(ip)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error obteniendo IPs de red: ${e.message}")
        }

        // SOLUCIÓN: Usar la IP real de la red local
        val networkIp = "192.168.1.64" // IP real de la PC en la red
        ips.clear() // Limpiar todas las IPs encontradas
        ips.add(networkIp) // Solo agregar la IP de la red
        
        Log.d(TAG, "🎯 SOLUCIÓN RED LOCAL: Usando IP de red: $networkIp")
        Log.d(TAG, "📋 IPs a probar: $ips")

        return ips
    }

    override fun onCleared() {
        super.onCleared()
        disconnectFromAutomotive()
    }
}