package com.example.ing.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ing.data.models.Job
import com.example.ing.data.repository.JobsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

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
        Log.d("JobsViewModel", "init: loadJobs() llamado")
    }

    fun loadJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Log.d("JobsViewModel", "Cargando jobs desde Firestore...")
            try {
                val jobsResult = jobsRepository.getAllJobs()
                if (jobsResult.isSuccess) {
                    _allJobs.value = jobsResult.getOrNull() ?: emptyList()
                    Log.d("JobsViewModel", "Lista de jobs recargada, se enviará al servidor si hay conexión. Total: ${_allJobs.value.size}")
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
            Log.d("JobsViewModel", "Eliminando job con id: $id")
            try {
                val deleteJobResult = jobsRepository.deleteJob(id)
                if (deleteJobResult.isSuccess) {
                    Log.d("JobsViewModel", "Job eliminado exitosamente, recargando lista...")
                    loadJobs()
                }
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
            Log.d("JobsViewModel", "Creando nuevo job: $job")
            try {
                val createJobResult = jobsRepository.createJob(job)
                if (createJobResult.isSuccess) {
                    Log.d("JobsViewModel", "Job creado exitosamente, recargando lista...")
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
            Log.d("JobsViewModel", "Actualizando status del job $id a $newStatus")
            try {
                val updateResult = jobsRepository.updateJobStatus(id, newStatus)
                if (updateResult.isSuccess) {
                    Log.d("JobsViewModel", "Status actualizado exitosamente, recargando lista...")
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