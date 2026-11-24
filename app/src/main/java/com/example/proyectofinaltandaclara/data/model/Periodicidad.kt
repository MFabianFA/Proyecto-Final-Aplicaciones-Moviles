package com.example.proyectofinaltandaclara.data.model

/**
 * Representa la frecuencia con la que se realizan los aportes y cobros en la tanda.
 * - SEMANAL: Cada 7 días transcurridos.
 * - QUINCENAL: Cada 15 días transcurridos.
 * - MENSUAL: El mismo número de día en el mes siguiente.
 */
enum class Periodicidad(val tituloHtml: String) {
    SEMANAL("Semanal (cada 7 días)"),
    QUINCENAL("Quincenal (cada 15 días)"),
    MENSUAL("Mensual (mismo día del mes)")
}
