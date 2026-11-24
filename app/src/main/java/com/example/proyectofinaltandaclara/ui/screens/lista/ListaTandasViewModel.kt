package com.example.proyectofinaltandaclara.ui.screens.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinaltandaclara.data.model.Tanda
import com.example.proyectofinaltandaclara.data.repository.TandaRepository
import com.example.proyectofinaltandaclara.utils.CalculadoraTanda
import com.example.proyectofinaltandaclara.utils.DatosEjemploHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TandaResumenUiState(
    val tanda: Tanda,
    val numeroParticipantes: Int,
    val montoTotalPeriodo: Double,
    val periodoActual: Int,
    val quienCobraActual: String,
    val totalAdeudos: Int,
    val estaTerminada: Boolean
)

data class ListaTandasUiState(
    val tandas: List<TandaResumenUiState> = emptyList(),
    val cargando: Boolean = true
)

class ListaTandasViewModel(
    private val repository: TandaRepository
) : ViewModel() {

    val uiState: StateFlow<ListaTandasUiState> = repository.todasLasTandas.map { listaTandas ->
        val resumenes = listaTandas.map { tanda ->
            val participantes = repository.getParticipantesPorTandaDirecto(tanda.id)
            val pagos = repository.getPagosPorTandaDirecto(tanda.id)

            val nPart = participantes.size
            val montoTotal = CalculadoraTanda.calcularTotalPorPeriodo(tanda.montoPorPersona, nPart)
            val periodoAct = CalculadoraTanda.calcularPeriodoActual(tanda.fechaInicio, tanda.periodicidad, nPart)
            val quienCobra = CalculadoraTanda.quienCobraEnPeriodo(participantes, periodoAct)?.nombre
                ?: if (nPart == 0) "Sin participantes" else "Sin turno asignado"
            val adeudosMap = CalculadoraTanda.calcularAdeudos(participantes, pagos, tanda.fechaInicio, tanda.periodicidad)
            val totalAdeudosCount = adeudosMap.values.sumOf { it.size }
            val terminada = CalculadoraTanda.estaTandaTerminada(tanda.fechaInicio, tanda.periodicidad, nPart)

            TandaResumenUiState(
                tanda = tanda,
                numeroParticipantes = nPart,
                montoTotalPeriodo = montoTotal,
                periodoActual = periodoAct,
                quienCobraActual = quienCobra,
                totalAdeudos = totalAdeudosCount,
                estaTerminada = terminada
            )
        }
        ListaTandasUiState(tandas = resumenes, cargando = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListaTandasUiState()
    )

    fun eliminarTanda(tanda: Tanda) {
        viewModelScope.launch {
            repository.eliminarTanda(tanda)
        }
    }

    fun cargarTandaEjemplo() {
        viewModelScope.launch {
            DatosEjemploHelper.crearTandaDeEjemplo(repository)
        }
    }
}

class ListaTandasViewModelFactory(
    private val repository: TandaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListaTandasViewModel(repository) as T
    }
}
