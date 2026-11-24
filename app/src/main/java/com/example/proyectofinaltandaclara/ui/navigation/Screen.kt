package com.example.proyectofinaltandaclara.ui.navigation

/**
 * Definición de las rutas de navegación de TandaClara.
 */
sealed class Screen(val route: String) {
    object ListaTandas : Screen("lista_tandas")
    object CrearTanda : Screen("crear_tanda")

    object Participantes : Screen("participantes/{tandaId}") {
        fun createRoute(tandaId: Long) = "participantes/$tandaId"
    }

    object DetalleTanda : Screen("detalle_tanda/{tandaId}") {
        fun createRoute(tandaId: Long) = "detalle_tanda/$tandaId"
    }
}
