package com.example.ing.screens.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.ToolsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditToolViewModel(
    savedStateHandle: SavedStateHandle // Permite recibir argumentos de navegación
) : ViewModel() {

    private val repository = ToolsRepository()
    // Obtiene el toolId que se pasa a través de la navegación
    private val toolId: String = savedStateHandle.get<String>("toolId")!!

    // Estado para la herramienta que se está editando
    private val _tool = MutableStateFlow<Tool?>(null)
    val tool = _tool.asStateFlow()

    // Estados de carga y error
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Carga los datos de la herramienta en cuanto se crea el ViewModel
        loadTool()
    }

    fun loadTool() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getToolById(toolId)
            if (result.isSuccess) {
                _tool.value = result.getOrNull()
            }
            _isLoading.value = false
        }
    }

    // Función para actualizar la herramienta
    suspend fun updateTool(name: String, model: String, availability: String, battery: Int, temperature: Int): Result<Unit> {
        val updates = mapOf(
            "name" to name,
            "model" to model,
            "availability" to availability,
            "battery" to battery,
            "temperature" to temperature
        )
        return repository.updateTool(toolId, updates)
    }
}