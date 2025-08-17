package com.example.ing.services

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.io.File

class SupabaseService {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://ywuppttjhxsoncnvynco.supabase.co",
        supabaseKey = "sb_publishable_ZD2HuOGNK2y1vcOhZWQPeQ_SFgGLwIi"
    ) {
        install(Storage)
    }

    suspend fun uploadImage(file: File): String? {
        return try {
            val fileName = "public/tool_${System.currentTimeMillis()}.jpg" // Usar public/
            val bucket = "tools"
            val result = supabase.storage.from(bucket).upload(
                path = fileName,
                data = file.readBytes(),
                upsert = true
            )
            val publicUrl = supabase.storage.from(bucket).publicUrl(fileName)
            println("Imagen subida: $publicUrl")
            publicUrl
        } catch (e: Exception) {
            println("Error en upload: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
