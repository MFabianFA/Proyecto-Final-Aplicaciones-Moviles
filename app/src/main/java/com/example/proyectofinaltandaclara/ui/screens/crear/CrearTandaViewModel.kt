package com.example.proyectofinaltandaclara.ui.screens.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaltandaclara.data.model.Periodicidad
import com.example.proyectofinaltandaclara.data.model.Tanda
import com.example.proyectofinaltandaclara.data.repository.TandaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CrearTandaUiState(
    val nombre: String = "",
    val montoText: String = "",
    val periodicidad: Periodicidad = Periodicidad.QUINCENAL,
    val fechaInicio: LocalDate = LocalDate.now(),
    val errorNombre: String? = null,
    val errorMonto: String? = null,
    val guardando: Boolean = false,
    val tandaCreadaId: Long? = null
)

class CrearTandaViewModel(
    private val repository: TandaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearTandaUiState())
    val uiState: StateFlow<CrearTandaUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nuevoNombre: String) {
        _uiState.value = _uiState.value.copy(
            nombre = nuevoNombre,
            errorNombre = null
        )
    }

    fun onMontoChanged(nuevoMonto: String) {
        if (nuevoMonto.isEmpty() || nuevoMonto.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
            _uiState.value = _uiState.value.copy(
                montoText = nuevoMonto,
                errorMonto = null
            )
        }
    }

    fun onPeriodicidadSelected(nuevaPeriodicidad: Periodicidad) {
        _uiState.value = _uiState.value.copy(periodicidad = nuevaPeriodicidad)
    }

    fun onFechaInicioChanged(nuevaFecha: LocalDate) {
        _uiState.value = _uiState.value.copy(fechaInicio = nuevaFecha)
    }

    fun guardarTanda() {
        val currentState = _uiState.value
        val nombreTrim = currentState.nombre.trim()
        val montoDouble = currentState.montoText.toDoubleOrNull()

        var hayError = false
        var errorNom: String? = null
        var errorMon: String? = null

        if (nombreTrim.isEmpty()) {
            errorNom = "Por favor ingresa un nombre para la tanda"
            hayError = true
        }

        if (montoDouble == null || montoDouble <= 0) {
            errorMon = "Ingresa un monto válido mayor a $0"
            hayError = true
        }

        if (hayError) {
            _uiState.value = currentState.copy(
                errorNombre = errorNom,
                errorMonto = errorMon
            )
            return
        }

        _uiState.value = currentState.copy(guardando = true)

        viewModelScope.launch {
            val nuevaTanda = Tanda(
                nombre = nombreTrim,
                montoPorPersona = montoDouble!!,
                periodicidad = currentState.periodicidad,
                fechaInicio = currentState.fechaInicio
            )
            val idGenerado = repository.insertarTanda(nuevaTanda)
            _uiState.value = currentState.copy(
                guardando = false,
                tandaCreadaId = idGenerado
            )
        }
    }

    fun resetNavegacion() {
        _uiState.value = _uiState.value.copy(tandaCreadaId = null)
    }
}

class CrearTandaViewModelFactory(
    private val repository: TandaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrearTandaViewModel(repository) as T
    }
}
