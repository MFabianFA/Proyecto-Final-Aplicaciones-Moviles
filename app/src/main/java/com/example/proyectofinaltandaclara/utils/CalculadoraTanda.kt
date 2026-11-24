package com.example.proyectofinaltandaclara.utils

import com.example.proyectofinaltandaclara.data.model.Pago
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.data.model.Periodicidad
import java.time.LocalDate

/**
 * Utilidad con funciones puras para calcular fechas de periodos, periodo actual,
 * montos totales, quién recibe el fondo y adeudos por participante.
 */
object CalculadoraTanda {

    /**
     * Calcula la fecha correspondiente al periodo [periodo] (1..N).
     * Periodo 1 = fechaInicio
     * Periodo n = fechaInicio + (n-1) * periodicidad
     */
    fun calcularFechaPeriodo(
        fechaInicio: LocalDate,
        periodo: Int,
        periodicidad: Periodicidad
    ): LocalDate {
        if (periodo <= 1) return fechaInicio
        val offset = (periodo - 1).toLong()
        return when (periodicidad) {
            Periodicidad.SEMANAL -> fechaInicio.plusDays(offset * 7)
            Periodicidad.QUINCENAL -> fechaInicio.plusDays(offset * 15)
            Periodicidad.MENSUAL -> fechaInicio.plusMonths(offset)
        }
    }

    /**
     * Determina el primer periodo (1..N) cuya fecha es igual o posterior a [fechaReferencia].
     * Si la fecha actual ya superó la fecha del último periodo, devuelve el total de participantes.
     */
    fun calcularPeriodoActual(
        fechaInicio: LocalDate,
        periodicidad: Periodicidad,
        numeroParticipantes: Int,
        fechaReferencia: LocalDate = LocalDate.now()
    ): Int {
        if (numeroParticipantes <= 0) return 1
        for (p in 1..numeroParticipantes) {
            val fechaP = calcularFechaPeriodo(fechaInicio, p, periodicidad)
            if (!fechaReferencia.isAfter(fechaP)) {
                return p
            }
        }
        return numeroParticipantes
    }

    /**
     * Calcula la suma total reunida por periodo (montoPorPersona * numeroParticipantes).
     */
    fun calcularTotalPorPeriodo(montoPorPersona: Double, numeroParticipantes: Int): Double {
        return montoPorPersona * numeroParticipantes
    }

    /**
     * Obtiene el participante asignado con el turno igual a [periodo].
     */
    fun quienCobraEnPeriodo(participantes: List<Participante>, periodo: Int): Participante? {
        return participantes.find { it.turno == periodo }
    }

    /**
     * Calcula los periodos adeudados por cada participante hasta la [fechaReferencia].
     * Un periodo se considera adeudado si su fecha ya transcurrió o es hoy, y no existe Pago registrado.
     */
    fun calcularAdeudos(
        participantes: List<Participante>,
        pagos: List<Pago>,
        fechaInicio: LocalDate,
        periodicidad: Periodicidad,
        fechaReferencia: LocalDate = LocalDate.now()
    ): Map<Long, List<Int>> {
        val resultado = mutableMapOf<Long, List<Int>>()
        val totalParticipantes = participantes.size
        if (totalParticipantes == 0) return emptyMap()

        val periodosTranscurridos = (1..totalParticipantes).filter { p ->
            val fechaP = calcularFechaPeriodo(fechaInicio, p, periodicidad)
            !fechaP.isAfter(fechaReferencia)
        }

        for (participante in participantes) {
            val pagosParticipante = pagos.filter { it.participanteId == participante.id }
            val periodosPagados = pagosParticipante.map { it.periodo }.toSet()
            val adeudos = periodosTranscurridos.filter { p -> !periodosPagados.contains(p) }
            resultado[participante.id] = adeudos
        }

        return resultado
    }

    /**
     * Indica si la tanda ha finalizado (la fecha actual es posterior al último periodo).
     */
    fun estaTandaTerminada(
        fechaInicio: LocalDate,
        periodicidad: Periodicidad,
        numeroParticipantes: Int,
        fechaReferencia: LocalDate = LocalDate.now()
    ): Boolean {
        if (numeroParticipantes <= 0) return false
        val fechaUltimoPeriodo = calcularFechaPeriodo(fechaInicio, numeroParticipantes, periodicidad)
        return fechaReferencia.isAfter(fechaUltimoPeriodo)
    }
}
