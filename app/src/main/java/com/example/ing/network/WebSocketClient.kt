package com.example.ing.network
//mostrar logs en consola
import android.util.Log
//cliente HTTP de Ktor con motor OkHttp
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
//registrar las peticiones y respuestas HTTP
import io.ktor.client.plugins.logging.*
//manejo de websockets
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object WebSocketClient {

    // Dirección del servidor WebSocket
    //192.168.1.74 es la dirección IP de donde esta el servidor
    //8080 es el puerto del servidor
    private const val SERVER_URL = "ws://192.168.1.74:8080/chat"

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
                //abre la conexion websocket a SERVER_URL
                client.webSocket(SERVER_URL) {
                    Log.d("WebSocket", "Conectado al servidor")

                    // Mandar identificación primero
                    send("IDENTIFY:$clientType")

                    // Ejemplo: enviar un mensaje inicial
                    send("Hola Automotive")

                    // Escuchar mensajes entrantes
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            //muestra el mensaje recibido
                            Log.d("WebSocket", " $message")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error de conexión: ${e.message}")
            }
        }
    }

    // Función para cerrar conexión
    fun disconnect() {
        job?.cancel()
        Log.d("WebSocket", "Conexión cerrada para $clientType")
    }
}