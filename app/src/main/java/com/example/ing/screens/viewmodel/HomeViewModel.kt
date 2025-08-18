package com.example.ing.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ing.data.models.Job
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.JobsRepository
import com.example.ing.data.repository.ToolsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val jobsRepository = JobsRepository()
    private val toolsRepository = ToolsRepository()

    // Trabajos activos
    private val _activeJobs = MutableStateFlow<List<Job>>(emptyList())
    val activeJobs = _activeJobs.asStateFlow()

    private val _toolsInUse = MutableStateFlow<List<Tool>>(emptyList())
    val toolsInUse = _toolsInUse.asStateFlow()

    // Carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //Mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    //Cargar los datos cuando se inicializa
    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val jobsResult = jobsRepository.getActiveJobs()
                if (jobsResult.isSuccess) {
                    _activeJobs.value = jobsResult.getOrNull()?.take(10) ?: emptyList()
                } else {
                    _errorMessage.value = jobsResult.exceptionOrNull()?.message ?: "Error"
                }

                val toolsResult = toolsRepository.getToolsInUse()
                if (toolsResult.isSuccess) {
                    _toolsInUse.value = toolsResult.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = toolsResult.exceptionOrNull()?.message ?: "Error al cargar las herramientas"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}