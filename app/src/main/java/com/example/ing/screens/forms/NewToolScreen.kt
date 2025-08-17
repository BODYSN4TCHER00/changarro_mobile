package com.example.ing.screens.forms

import android.Manifest
import android.content.ContentValues
import android.content.Context
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ing.components.*
import androidx.compose.material.icons.filled.PhotoLibrary
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import com.example.ing.components.forms.CounterField
import com.example.ing.components.forms.FormField
import com.example.ing.components.forms.FieldType
import com.example.ing.components.forms.FormActions
import com.example.ing.components.forms.FormHeader
import com.example.ing.components.forms.FormContainer
import com.example.ing.data.enums.AppColors
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.ToolsRepository
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import com.example.ing.services.SupabaseService
import java.io.File

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
    var pendingAction by remember { mutableStateOf<String?>(null) } // "gallery" o "camera"
    val scope = rememberCoroutineScope()

    val repository = ToolsRepository()
    val supabaseService = SupabaseService()

    val availabilityOptions = listOf("Disponible", "En uso", "Fuera de servicio")
    val context = LocalContext.current

    // Declarar los launchers como variables mutables
    var launchGalleryPicker by remember { mutableStateOf<(() -> Unit)?>(null) }
    var launchCamera by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Permisos para Android 13+
    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Galería
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("NewToolScreen", "galleryPermissionLauncher: isGranted=$isGranted, pendingAction=$pendingAction")
        if (isGranted && pendingAction == "gallery") {
            Log.d("NewToolScreen", "Permiso galería concedido, lanzando picker")
            launchGalleryPicker?.invoke()
            pendingAction = null
        } else {
            Log.d("NewToolScreen", "Permiso galería denegado o acción no pendiente")
            pendingAction = null
        }
    }
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("NewToolScreen", "galleryPickerLauncher: uri=$uri")
        if (uri != null) imageUri = uri
    }
    launchGalleryPicker = { galleryPickerLauncher.launch("image/*") }

    // Cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("NewToolScreen", "cameraPermissionLauncher: isGranted=$isGranted, pendingAction=$pendingAction, tempCameraUri=$tempCameraUri")
        if (isGranted && pendingAction == "camera" && tempCameraUri != null) {
            Log.d("NewToolScreen", "Permiso cámara concedido, lanzando cámara")
            launchCamera?.invoke()
            pendingAction = null
        } else {
            Log.d("NewToolScreen", "Permiso cámara denegado o acción no pendiente")
            pendingAction = null
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d("NewToolScreen", "cameraLauncher: success=$success, tempCameraUri=$tempCameraUri")
        if (success && tempCameraUri != null) {
            imageUri = tempCameraUri
        }
    }
    launchCamera = {
        Log.d("NewToolScreen", "launchCamera: tempCameraUri=$tempCameraUri")
        tempCameraUri?.let { cameraLauncher.launch(it) }
    }

    // Bottom sheet para elegir fuente
    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Seleccionar foto", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF232323))
                Spacer(modifier = Modifier.height(12.dp))
                ListItem(
                    headlineContent = { Text("Galería", color = Color(0xFF232323)) },
                    leadingContent = {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF232323))
                    },
                    modifier = Modifier.clickable {
                        showMenu = false
                        val granted = ContextCompat.checkSelfPermission(context, galleryPermission) == PackageManager.PERMISSION_GRANTED
                        Log.d("NewToolScreen", "Click galería: permission=$galleryPermission, granted=$granted")
                        if (granted) {
                            launchGalleryPicker?.invoke()
                        } else {
                            pendingAction = "gallery"
                            showGalleryPermissionDialog = true
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Cámara", color = Color(0xFF232323)) },
                    leadingContent = {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF232323))
                    },
                    modifier = Modifier.clickable {
                        showMenu = false
                        val permission = Manifest.permission.CAMERA
                        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "tool_photo_${System.currentTimeMillis()}.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ing_tools")
                            }
                        }
                        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        tempCameraUri = uri
                        Log.d("NewToolScreen", "Click cámara: permission=$permission, granted=$granted, tempCameraUri=$tempCameraUri")
                        if (granted) {
                            launchCamera?.invoke()
                        } else {
                            pendingAction = "camera"
                            showCameraPermissionDialog = true
                        }
                    }
                )
            }
        }
    }

    // Diálogo de permiso galería
    if (showGalleryPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showGalleryPermissionDialog = false },
            icon = {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF232323), modifier = Modifier.size(36.dp))
            },
            title = { Text("Permiso de galería", fontWeight = FontWeight.Bold, color = Color(0xFF232323)) },
            text = { Text("La app necesita acceso a la galería para seleccionar una foto. ¿Deseas permitirlo?", color = Color(0xFF232323)) },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    onClick = {
                        showGalleryPermissionDialog = false
                        val granted = ContextCompat.checkSelfPermission(context, galleryPermission) == PackageManager.PERMISSION_GRANTED
                        Log.d("NewToolScreen", "Dialog galería: permission=$galleryPermission, granted=$granted")
                        if (granted) {
                            launchGalleryPicker?.invoke()
                            pendingAction = null
                        } else {
                            pendingAction = "gallery"
                            galleryPermissionLauncher.launch(galleryPermission)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323))
                ) { Text("Permitir", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showGalleryPermissionDialog = false
                    pendingAction = null
                }) {
                    Text("No permitir", color = Color(0xFF232323))
                }
            }
        )
    }

    // Diálogo de permiso cámara
    if (showCameraPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showCameraPermissionDialog = false },
            icon = {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF232323), modifier = Modifier.size(36.dp))
            },
            title = { Text("Permiso de cámara", fontWeight = FontWeight.Bold, color = Color(0xFF232323)) },
            text = { Text("La app necesita acceso a la cámara para tomar una foto. ¿Deseas permitirlo?", color = Color(0xFF232323)) },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    onClick = {
                        showCameraPermissionDialog = false
                        val permission = Manifest.permission.CAMERA
                        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "tool_photo_${System.currentTimeMillis()}.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ing_tools")
                            }
                        }
                        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        tempCameraUri = uri
                        Log.d("NewToolScreen", "Dialog cámara: permission=$permission, granted=$granted, tempCameraUri=$tempCameraUri")
                        if (granted) {
                            launchCamera?.invoke()
                            pendingAction = null
                        } else {
                            pendingAction = "camera"
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323))
                ) { Text("Permitir", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showCameraPermissionDialog = false
                    pendingAction = null
                }) {
                    Text("No permitir", color = Color(0xFF232323))
                }
            }
        )
    }

    // Galería: al conceder permiso, abrir picker inmediatamente
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(context, galleryPermission) == PackageManager.PERMISSION_GRANTED
        Log.d("NewToolScreen", "LaunchedEffect galería: permission=$galleryPermission, granted=$granted, showGalleryPermissionDialog=$showGalleryPermissionDialog")
        if (granted && showGalleryPermissionDialog.not()) {
            launchGalleryPicker?.invoke()
        }
    }
    // Cámara: al conceder permiso, abrir cámara inmediatamente
    LaunchedEffect(tempCameraUri, showCameraPermissionDialog) {
        val permission = Manifest.permission.CAMERA
        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        Log.d("NewToolScreen", "LaunchedEffect cámara: permission=$permission, granted=$granted, tempCameraUri=$tempCameraUri, showCameraPermissionDialog=$showCameraPermissionDialog")
        if (granted && tempCameraUri != null && showCameraPermissionDialog.not()) {
            launchCamera?.invoke()
        }
    }

    val supabase = createSupabaseClient(
        supabaseUrl = "https://ywuppttjhxsoncnvynco.supabase.co",
        supabaseKey = "<prefer publishable key instead of anon key for mobile apps>"
    ) {
        install(Postgrest)
        install(Storage)
    }

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
                    val url = supabaseService.uploadImage(tempFile)
                    url
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF232323))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF232323))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.navigateUp() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Nueva Herramienta",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                // Much more space below the header (mockup-like)
                Spacer(modifier = Modifier.height(125.dp))
            }

            // Main form container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                // Camera/Image circle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-70).dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp) // Más grande
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .clickable { showMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Seleccionar imagen",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }

                // Form content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 40.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(70.dp))

                    FormField(
                        label = "Nombre",
                        value = toolName,
                        onValueChange = { toolName = it },
                        type = FieldType.TEXT,
                        icon = Icons.Default.List,
                        placeholder = "Ingrese el nombre de la herramienta"
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    FormField(
                        label = "Modelo",
                        value = toolModel,
                        onValueChange = { toolModel = it },
                        type = FieldType.TEXT,
                        icon = Icons.Default.List,
                        placeholder = "Ingrese el modelo de la herramienta"
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    // Input de disponibilidad con opciones
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        FormField(
                            label = "Disponibilidad",
                            value = availability,
                            onValueChange = { availability = it },
                            type = FieldType.TEXT,
                            icon = Icons.Default.List,
                            placeholder = "Seleccionar disponibilidad",
                            modifier = Modifier.clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            listOf("Disponible", "No disponible").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        availability = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CounterField(
                            label = "Bateria",
                            value = batteryLevel,
                            onValueChange = { batteryLevel = it },
                            icon = Icons.Default.BatteryStd,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        CounterField(
                            label = "Temperatura",
                            value = temperature,
                            onValueChange = { temperature = it },
                            icon = Icons.Default.Thermostat,
                            modifier = Modifier.weight(1f)
                        )
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
                                        "No disponible" -> "not_available"
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
                                    // Aquí podrías usar un Snackbar o Toast para mostrar el error
                                }
                            }
                        },
                        onCancel = {
                            navController.navigateUp()
                        },
                        acceptText = "Aceptar",
                        cancelText = "Cancelar"
                    )
                }
            }
        }
    }
}