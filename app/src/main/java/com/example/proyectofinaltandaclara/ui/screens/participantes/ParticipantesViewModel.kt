package com.example.proyectofinaltandaclara.ui.screens.participantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.data.model.Tanda
import com.example.proyectofinaltandaclara.data.repository.TandaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ParticipantesUiState(
    val tanda: Tanda? = null,
    val participantes: List<Participante> = emptyList(),
    val cargando: Boolean = true,
    val mostrarDialogoAgregarEditar: Boolean = false,
    val participanteEnEdicion: Participante? = null,
    val nombreInput: String = "",
    val telefonoInput: String = "",
    val errorNombre: String? = null
)

class ParticipantesViewModel(
    private val tandaId: Long,
    private val repository: TandaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParticipantesUiState())
    val uiState: StateFlow<ParticipantesUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            repository.getTandaPorId(tandaId).collect { tanda ->
                _uiState.value = _uiState.value.copy(tanda = tanda)
            }
        }
        viewModelScope.launch {
            repository.getParticipantesPorTanda(tandaId).collect { lista ->
                _uiState.value = _uiState.value.copy(
                    participantes = lista,
                    cargando = false
                )
            }
        }
    }

    fun abrirDialogoNuevo() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoAgregarEditar = true,
            participanteEnEdicion = null,
            nombreInput = "",
            telefonoInput = "",
            errorNombre = null
        )
    }

    fun abrirDialogoEditar(participante: Participante) {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoAgregarEditar = true,
            participanteEnEdicion = participante,
            nombreInput = participante.nombre,
            telefonoInput = participante.telefono ?: "",
            errorNombre = null
        )
    }

    fun cerrarDialogo() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoAgregarEditar = false,
            participanteEnEdicion = null,
            nombreInput = "",
            telefonoInput = "",
            errorNombre = null
        )
    }

    fun onNombreChanged(nuevoNombre: String) {
        _uiState.value = _uiState.value.copy(
            nombreInput = nuevoNombre,
            errorNombre = null
        )
    }

    fun onTelefonoChanged(nuevoTelefono: String) {
        _uiState.value = _uiState.value.copy(telefonoInput = nuevoTelefono)
    }

    fun guardarParticipante() {
        val currentState = _uiState.value
        val nombreTrim = currentState.nombreInput.trim()
        if (nombreTrim.isEmpty()) {
            _uiState.value = currentState.copy(errorNombre = "Ingresa el nombre del participante")
            return
        }

        val telefonoClean = currentState.telefonoInput.trim().ifEmpty { null }

        viewModelScope.launch {
            if (currentState.participanteEnEdicion != null) {
                val actualizado = currentState.participanteEnEdicion.copy(
                    nombre = nombreTrim,
                    telefono = telefonoClean
                )
                repository.insertarParticipante(actualizado)
            } else {
                val nuevoTurno = currentState.participantes.size + 1
                val nuevo = Participante(
                    tandaId = tandaId,
                    nombre = nombreTrim,
                    telefono = telefonoClean,
                    turno = nuevoTurno
                )
                repository.insertarParticipante(nuevo)
            }
            cerrarDialogo()
        }
    }

    fun eliminarParticipante(participante: Participante) {
        viewModelScope.launch {
            repository.eliminarParticipante(participante)
            val restantes = repository.getParticipantesPorTandaDirecto(tandaId)
            val reajustados = restantes.mapIndexed { index, p ->
                p.copy(turno = index + 1)
            }
            repository.insertarParticipantes(reajustados)
        }
    }

    fun sortearTurnos() {
        viewModelScope.launch {
            repository.realizarSorteoDeTurnos(tandaId)
        }
    }

    fun moverArriba(index: Int) {
        if (index <= 0) return
        val lista = _uiState.value.participantes.toMutableList()
        val temp = lista[index]
        lista[index] = lista[index - 1]
        lista[index - 1] = temp

        val reajustados = lista.mapIndexed { idx, p -> p.copy(turno = idx + 1) }
        viewModelScope.launch {
            repository.insertarParticipantes(reajustados)
        }
    }

    fun moverAbajo(index: Int) {
        val lista = _uiState.value.participantes.toMutableList()
        if (index >= lista.size - 1) return
        val temp = lista[index]
        lista[index] = lista[index + 1]
        lista[index + 1] = temp

        val reajustados = lista.mapIndexed { idx, p -> p.copy(turno = idx + 1) }
        viewModelScope.launch {
            repository.insertarParticipantes(reajustados)
        }
    }
}

class ParticipantesViewModelFactory(
    private val tandaId: Long,
    private val repository: TandaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ParticipantesViewModel(tandaId, repository) as T
    }
}
