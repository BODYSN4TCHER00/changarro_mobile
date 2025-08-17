package com.example.ing.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ing.data.models.Job
import com.example.ing.data.repository.JobsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobsViewModel : ViewModel() {

    //Inyectar jobs repository
    private val jobsRepository = JobsRepository()

    // Todos los trabajos
    private val _allJobs = MutableStateFlow<List<Job>>(emptyList())
    val allJobs = _allJobs.asStateFlow()

    //Carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //Mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val jobsResult = jobsRepository.getAllJobs()
                if (jobsResult.isSuccess) {
                    _allJobs.value = jobsResult.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = jobsResult.exceptionOrNull()?.message ?: "Error obteniendo las herramientas"
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

            try {
                val deleteJobResult = jobsRepository.deleteJob(id)
                if (deleteJobResult.isSuccess) loadJobs()
                else _errorMessage.value = deleteJobResult.exceptionOrNull()?.message ?: "Error al eliminar la herramienta"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createJob(job: Job) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val createJobResult = jobsRepository.createJob(job)
                if (createJobResult.isSuccess) {
                    loadJobs()
                } else {
                    _errorMessage.value = createJobResult.exceptionOrNull()?.message ?: "Error creando trabajo nuevo"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateJobStatus(id: String, newStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val updateResult = jobsRepository.updateJobStatus(id, newStatus)
                if (updateResult.isSuccess) {
                    loadJobs()
                } else {
                    _errorMessage.value = updateResult.exceptionOrNull()?.message ?: "Error al actualizar el estado"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}