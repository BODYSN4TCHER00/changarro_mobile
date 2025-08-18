package com.example.ing.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.ToolsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class ToolsViewModel : ViewModel() {

    //Inyectar tools repository
    private val toolsRepository = ToolsRepository()

    // Todas las herramientas
    private val _allTools = MutableStateFlow<List<Tool>>(emptyList())
    val allTools = _allTools.asStateFlow()

    // Carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //Mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadTools()
        Log.d("ToolsViewModel", "init: loadTools() llamado")
    }

    fun loadTools() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Log.d("ToolsViewModel", "Cargando tools desde Firestore...")
            try {
                val toolsResult = toolsRepository.getAllActiveTools()
                if (toolsResult.isSuccess) {
                    _allTools.value = toolsResult.getOrNull() ?: emptyList()
                    Log.d("ToolsViewModel", "Lista de tools recargada, se enviará al servidor si hay conexión. Total: ${_allTools.value.size}")
                } else {
                    _errorMessage.value = toolsResult.exceptionOrNull()?.message ?: "Error obteniendo tools"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTool(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Log.d("ToolsViewModel", "Eliminando tool con id: $id")
            try {
                val deleteToolResult = toolsRepository.deactivateTool(id)
                if (deleteToolResult.isSuccess) {
                    Log.d("ToolsViewModel", "Tool eliminada exitosamente, recargando lista...")
                    loadTools()
                }
                else _errorMessage.value = deleteToolResult.exceptionOrNull()?.message ?: "Error al eliminar la herramienta"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

}