package com.example.proyectofinaltandaclara.ui.components

import java.text.NumberFormat
import java.util.Locale

/**
 * Formatea un valor numérico Double a formato de moneda mexicana (ejemplo: $5,000).
 */
fun Double.toFormatoMoneda(mostrarCentavos: Boolean = false): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    if (!mostrarCentavos) {
        formato.maximumFractionDigits = 0
        formato.minimumFractionDigits = 0
    }
    return formato.format(this)
}
