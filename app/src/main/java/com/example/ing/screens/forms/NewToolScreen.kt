package com.example.ing.screens.forms

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ing.components.forms.CounterField
import com.example.ing.components.forms.FormActions
import com.example.ing.components.forms.FormDropdown
import com.example.ing.components.forms.FormField
import com.example.ing.components.forms.FieldType
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.ToolsRepository
import com.example.ing.services.SupabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewToolScreen(navController: NavController) {
    var toolName by remember { mutableStateOf("") }
    var toolModel by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableStateOf(50) }
    var temperature by remember { mutableStateOf(15) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showGalleryPermissionDialog by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingAction by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val repository = ToolsRepository()
    val supabaseService = SupabaseService()

    val availabilityOptions = listOf("Disponible", "En uso", "Fuera de servicio")
    val context = LocalContext.current

    var launchGalleryPicker by remember { mutableStateOf<(() -> Unit)?>(null) }
    var launchCamera by remember { mutableStateOf<(() -> Unit)?>(null) }

    // --- CONFLICTO 1 RESUELTO: Se conserva el código para permisos de Android 13+ ---
    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // El resto de la lógica de permisos y launchers...
    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted && pendingAction == "gallery") {
            launchGalleryPicker?.invoke()
            pendingAction = null
        } else {
            pendingAction = null
        }
    }
    val galleryPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }
    launchGalleryPicker = { galleryPickerLauncher.launch("image/*") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted && pendingAction == "camera" && tempCameraUri != null) {
            launchCamera?.invoke()
            pendingAction = null
        } else {
            pendingAction = null
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            imageUri = tempCameraUri
        }
    }
    launchCamera = {
        tempCameraUri?.let { cameraLauncher.launch(it) }
    }

    if (showMenu) { /* ... Tu código de ModalBottomSheet, no necesita cambios ... */ }
    if (showGalleryPermissionDialog) { /* ... Tu código de AlertDialog, no necesita cambios ... */ }
    if (showCameraPermissionDialog) { /* ... Tu código de AlertDialog, no necesita cambios ... */ }

    // --- LÓGICA PARA SUBIR IMAGEN A SUPABASE ---
    suspend fun uploadImageToSupabase(imageUri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val tempFile = File.createTempFile("upload_temp", ".jpg", context.cacheDir)
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.exists()) {
                    supabaseService.uploadImage(tempFile)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF232323)).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(24.dp).clickable { navController.navigateUp() })
                Spacer(modifier = Modifier.width(16.dp))
                Text("Nueva Herramienta", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(125.dp))

            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))) {
                Box(
                    modifier = Modifier.fillMaxWidth().offset(y = (-70).dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier.size(180.dp).clip(CircleShape).background(Color(0xFFE0E0E0)).clickable { showMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(imageUri).crossfade(true).build(), contentDescription = "Imagen seleccionada", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.CameraAlt, "Seleccionar imagen", tint = Color(0xFF9E9E9E), modifier = Modifier.size(72.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 40.dp).verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(70.dp))

                    FormField(label = "Nombre", value = toolName, onValueChange = { toolName = it }, type = FieldType.TEXT, icon = Icons.Default.List, placeholder = "Ingrese el nombre de la herramienta")
                    Spacer(modifier = Modifier.height(18.dp))
                    FormField(label = "Modelo", value = toolModel, onValueChange = { toolModel = it }, type = FieldType.TEXT, icon = Icons.Default.List, placeholder = "Ingrese el modelo de la herramienta")
                    Spacer(modifier = Modifier.height(18.dp))

                    // --- CONFLICTO 2 RESUELTO: Se conserva el FormDropdown que es más limpio ---
                    FormDropdown(
                        label = "Disponibilidad",
                        value = availability,
                        onValueChange = { availability = it },
                        options = availabilityOptions,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        CounterField(label = "Bateria", value = batteryLevel, onValueChange = { batteryLevel = it }, icon = Icons.Default.BatteryStd, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        CounterField(label = "Temperatura", value = temperature, onValueChange = { temperature = it }, icon = Icons.Default.Thermostat, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    FormActions(
                        onAccept = {
                            scope.launch {
                                var imageUrl = ""
                                if (imageUri != null) {
                                    imageUrl = uploadImageToSupabase(imageUri!!, context) ?: ""
                                }
                                val tool = Tool(
                                    name = toolName,
                                    model = toolModel,
                                    availability = when (availability) {
                                        "Disponible" -> "available"
                                        "En uso" -> "in_use" // Añadido para consistencia
                                        "Fuera de servicio" -> "not_available"
                                        else -> "available"
                                    },
                                    battery = batteryLevel,
                                    temperature = temperature,
                                    isActive = true,
                                    url = imageUrl
                                )
                                val result = repository.createTool(tool)
                                if (result.isSuccess) {
                                    navController.navigateUp()
                                } else {
                                    // Manejar error
                                }
                            }
                        },
                        onCancel = { navController.navigateUp() },
                        acceptText = "Aceptar",
                        cancelText = "Cancelar"
                    )
                }
            }
        }
    }
}