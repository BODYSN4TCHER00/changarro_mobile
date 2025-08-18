package com.example.ing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.ing.components.navigation.AppNavigation
import com.example.ing.components.navigation.BottomNavigation
import com.example.ing.network.WebSocketClient
import com.example.ing.screens.viewmodel.JobsViewModel
import com.example.ing.screens.viewmodel.ToolsViewModel
import com.example.ing.ui.theme.IngTheme
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModels para observar los datos
        val toolsViewModel: ToolsViewModel by viewModels()
        val jobsViewModel: JobsViewModel by viewModels()
        val gson = Gson()

        // Conectar al servidor al iniciar la app
        WebSocketClient.connect("Mobile")

        // Solo enviar datos cuando la conexión esté activa
        var syncJob: Job? = null
        lifecycleScope.launch {
            WebSocketClient.isConnected.collectLatest { connected ->
                syncJob?.cancel()
                if (connected) {
                    syncJob = launch {
                        launch {
                            toolsViewModel.allTools.collectLatest { tools ->
                                try {
                                    val json = gson.toJson(mapOf("type" to "tools", "data" to tools))
                                    WebSocketClient.sendData(json)
                                    android.util.Log.d("WebSocketSync", "Enviando tools: $json")
                                } catch (e: Exception) {
                                    android.util.Log.e("WebSocketSync", "Error serializando/enviando tools: ${e.message}")
                                }
                            }
                        }
                        launch {
                            jobsViewModel.allJobs.collectLatest { jobs ->
                                try {
                                    val json = gson.toJson(mapOf("type" to "jobs", "data" to jobs))
                                    WebSocketClient.sendData(json)
                                    android.util.Log.d("WebSocketSync", "Enviando jobs: $json")
                                } catch (e: Exception) {
                                    android.util.Log.e("WebSocketSync", "Error serializando/enviando jobs: ${e.message}")
                                }
                            }
                        }
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            IngTheme {
                val navController = rememberNavController()
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    // Main Content
                    AppNavigation(navController = navController)
                    
                    // Global Bottom Navigation
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 12.dp)
                    ) {
                        BottomNavigation(navController = navController)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketClient.disconnect() // Cerrar conexión al salir
    }
}