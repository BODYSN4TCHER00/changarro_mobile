package com.example.ing.network
//mostrar logs en consola
//cliente HTTP de Ktor con motor OkHttp
//registrar las peticiones y respuestas HTTP
//manejo de websockets
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object WebSocketClient {

    // Dirección del servidor WebSocket
    //192.168.1.74 es la dirección IP de donde esta el servidor
    //8080 es el puerto del servidor
    private const val SERVER_URL = "ws://192.168.1.64:8080/chat"

    // Cliente Ktor
    //motor de red
    private val client = HttpClient(OkHttp) {
        //soporte websocket en el cliente
        install(WebSockets)
        //registra todas las solicitudes y mensajes
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    //hilo de conexión
    private var job: Job? = null
    //guardar el tipo de cliente
    private var clientType: String = "Desconocido"
    //guardar la sesión websocket
    private var session: DefaultClientWebSocketSession? = null
    // Estado de conexión
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    // Canal para mensajes salientes
    private val outgoingMessages = Channel<String>(Channel.UNLIMITED)

    // Función para iniciar conexión
    fun connect(type: String) {
        clientType = type

        //evita conexiones duplicadas
        if (job?.isActive == true) {
            Log.d("WebSocket", "Ya está conectado")
            return
        }
        //lanza una corutina en hilo de entrada/salida (Dispatchers.IO).
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.webSocket(SERVER_URL) {
                    session = this // Guardar la sesión activa
                    _isConnected.value = true
                    Log.d("WebSocket", "Conectado al servidor")

                    // Mandar identificación primero
                    send("IDENTIFY:$clientType")
                    send("Hola Automotive")

                    // Lanzar una corutina para enviar mensajes del canal
                    val sendJob = launch {
                        outgoingMessages.consumeEach { msg ->
                            try {
                                send(msg)
                                Log.d("WebSocket", "Enviando datos: $msg")
                            } catch (e: Exception) {
                                Log.e("WebSocket", "Error al enviar datos: ${e.message}")
                            }
                        }
                    }

                    // Escuchar mensajes entrantes
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            Log.d("WebSocket", " $message")
                        }
                    }
                    sendJob.cancel()
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error de conexión: ${e.message}")
            } finally {
                session = null // Limpiar la sesión al desconectar
                _isConnected.value = false
            }
        }
    }

    // Función para cerrar conexión
    fun disconnect() {
        job?.cancel()
        session = null // Limpiar la sesión al desconectar
        _isConnected.value = false
        outgoingMessages.close()
        Log.d("WebSocket", "Conexión cerrada para $clientType")
    }

    // Función para enviar datos por WebSocket
    fun sendData(json: String) {
        if (job?.isActive == true) {
            try {
                outgoingMessages.trySend(json)
            } catch (e: Exception) {
                Log.e("WebSocket", "Error al poner mensaje en el canal: ${e.message}")
            }
        } else {
            Log.e("WebSocket", "No se puede enviar, conexión no activa")
        }
    }
}