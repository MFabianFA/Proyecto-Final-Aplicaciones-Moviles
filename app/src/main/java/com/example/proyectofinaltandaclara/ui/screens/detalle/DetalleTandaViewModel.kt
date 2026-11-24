package com.example.proyectofinaltandaclara.ui.screens.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaltandaclara.data.model.Pago
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.data.model.Periodicidad
import com.example.proyectofinaltandaclara.data.model.Tanda
import com.example.proyectofinaltandaclara.data.repository.TandaRepository
import com.example.proyectofinaltandaclara.utils.CalculadoraTanda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AdeudoPersonaEstado(
    val participante: Participante,
    val periodosAdeudados: List<Int>,
    val montoAdeudoTotal: Double
)

data class DetalleTandaUiState(
    val tanda: Tanda? = null,
    val participantes: List<Participante> = emptyList(),
    val pagos: List<Pago> = emptyList(),
    val periodoSeleccionado: Int = 1,
    val periodoActual: Int = 1,
    val cargando: Boolean = true,
    val mostrarDialogoEditar: Boolean = false,
    val mostrarDialogoEliminar: Boolean = false,
    val editNombre: String = "",
    val editMontoText: String = "",
    val editPeriodicidad: Periodicidad = Periodicidad.QUINCENAL,
    val editFechaInicio: LocalDate = LocalDate.now()
) {
    val totalParticipantes: Int get() = participantes.size
}

class DetalleTandaViewModel(
    private val tandaId: Long,
    private val repository: TandaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleTandaUiState())
    val uiState: StateFlow<DetalleTandaUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            combine(
                repository.getTandaPorId(tandaId),
                repository.getParticipantesPorTanda(tandaId),
                repository.getPagosPorTanda(tandaId)
            ) { tanda, participantes, pagos ->
                if (tanda != null) {
                    val nPart = participantes.size
                    val pActual = CalculadoraTanda.calcularPeriodoActual(
                        tanda.fechaInicio, tanda.periodicidad, nPart
                    )
                    val pSel = if (_uiState.value.cargando) pActual else _uiState.value.periodoSeleccionado

                    _uiState.value = _uiState.value.copy(
                        tanda = tanda,
                        participantes = participantes,
                        pagos = pagos,
                        periodoActual = pActual,
                        periodoSeleccionado = pSel.coerceIn(1, if (nPart > 0) nPart else 1),
                        cargando = false
                    )
                }
            }.collect {}
        }
    }

    fun seleccionarPeriodo(periodo: Int) {
        val maxP = _uiState.value.totalParticipantes.coerceAtLeast(1)
        _uiState.value = _uiState.value.copy(
            periodoSeleccionado = periodo.coerceIn(1, maxP)
        )
    }

    fun togglePago(participanteId: Long, periodo: Int) {
        val tanda = _uiState.value.tanda ?: return
        viewModelScope.launch {
            val pagoExistente = repository.getPagosPorTandaDirecto(tandaId).find {
                (it.participanteId == participanteId) && (it.periodo == periodo)
            }
            if (pagoExistente != null) {
                repository.eliminarPago(pagoExistente)
            } else {
                repository.registrarPago(
                    tandaId = tanda.id,
                    participanteId = participanteId,
                    periodo = periodo,
                    fechaPago = LocalDate.now()
                )
            }
        }
    }

    fun abrirDialogoEditar() {
        val tanda = _uiState.value.tanda ?: return
        _uiState.value = _uiState.value.copy(
            mostrarDialogoEditar = true,
            editNombre = tanda.nombre,
            editMontoText = tanda.montoPorPersona.toString(),
            editPeriodicidad = tanda.periodicidad,
            editFechaInicio = tanda.fechaInicio
        )
    }

    fun cerrarDialogoEditar() {
        _uiState.value = _uiState.value.copy(mostrarDialogoEditar = false)
    }

    fun onEditNombreChanged(nombre: String) {
        _uiState.value = _uiState.value.copy(editNombre = nombre)
    }

    fun onEditMontoChanged(monto: String) {
        if (monto.isEmpty() || monto.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
            _uiState.value = _uiState.value.copy(editMontoText = monto)
        }
    }

    fun onEditPeriodicidadChanged(p: Periodicidad) {
        _uiState.value = _uiState.value.copy(editPeriodicidad = p)
    }

    fun onEditFechaInicioChanged(f: LocalDate) {
        _uiState.value = _uiState.value.copy(editFechaInicio = f)
    }

    fun guardarEdicionTanda() {
        val state = _uiState.value
        val tandaOriginal = state.tanda ?: return
        val nombreTrim = state.editNombre.trim()
        val monto = state.editMontoText.toDoubleOrNull() ?: return

        if (nombreTrim.isEmpty() || monto <= 0) return

        viewModelScope.launch {
            val tandaActualizada = tandaOriginal.copy(
                nombre = nombreTrim,
                montoPorPersona = monto,
                periodicidad = state.editPeriodicidad,
                fechaInicio = state.editFechaInicio
            )
            repository.actualizarTanda(tandaActualizada)
            cerrarDialogoEditar()
        }
    }

    fun abrirDialogoEliminar() {
        _uiState.value = _uiState.value.copy(mostrarDialogoEliminar = true)
    }

    fun cerrarDialogoEliminar() {
        _uiState.value = _uiState.value.copy(mostrarDialogoEliminar = false)
    }

    fun eliminarTanda(onEliminada: () -> Unit) {
        val tanda = _uiState.value.tanda ?: return
        viewModelScope.launch {
            repository.eliminarTanda(tanda)
            cerrarDialogoEliminar()
            onEliminada()
        }
    }
}

class DetalleTandaViewModelFactory(
    private val tandaId: Long,
    private val repository: TandaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetalleTandaViewModel(tandaId, repository) as T
    }
}
