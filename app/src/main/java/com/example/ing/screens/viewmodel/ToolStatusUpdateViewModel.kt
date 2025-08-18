package com.example.ing.screens.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ing.data.models.Job
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.JobsRepository
import com.example.ing.data.repository.ToolsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ToolStatusUpdateViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobsRepository = JobsRepository()
    private val toolsRepository = ToolsRepository()
    private val jobId: String = savedStateHandle.get<String>("jobId")!!

    private val _job = MutableStateFlow<Job?>(null)
    val job = _job.asStateFlow()

    private val _assignedTools = MutableStateFlow<List<Tool>>(emptyList())
    val assignedTools = _assignedTools.asStateFlow()

    //Carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //Errores
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadJobAndTools()
    }

    private fun loadJobAndTools() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {

                //Detalles del trabajo
                val jobResult = jobsRepository.getJobById(jobId)
                if (jobResult.isSuccess) {
                    _job.value = jobResult.getOrNull()
                }

                // Carga las herramientas asociadas a ese trabajo
                val toolsResult = jobsRepository.getToolsForJob(jobId)
                if (toolsResult.isSuccess) {
                    _assignedTools.value = toolsResult.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar herramientas"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun saveChangesAndUpdateJobStatus(
        updatedToolsState: List<ToolStatusState>,
        newStatus: String
    ): Result<Unit> {
        _isLoading.value = true
        var finalResult: Result<Unit> = Result.success(Unit)
        try {
            // Aquí podrías iterar y guardar cada herramienta actualizada
            updatedToolsState.forEach { toolState ->
                val updates = mutableMapOf<String,Any>(
                    "battery" to toolState.batteryLevel,
                    "temperature" to toolState.temperature
                )
                when (newStatus) {
                    "active" -> updates["availability"] = "in_use"
                    "completed" -> updates["availability"] = "available"
                }

                toolsRepository.updateTool(toolState.toolId, updates)
            }
            // Si todo va bien, actualiza el estado del trabajo
            finalResult = jobsRepository.updateJobStatus(jobId, newStatus)
        } catch (e: Exception) {
            finalResult = Result.failure(e)
        } finally {
            _isLoading.value = false
        }
        return finalResult
    }
}

data class ToolStatusState(
    val toolId: String,
    val toolName: String,
    val toolModel: String,
    var batteryLevel: Int,
    var temperature: Int
)