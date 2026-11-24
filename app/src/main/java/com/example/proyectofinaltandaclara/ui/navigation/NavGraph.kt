package com.example.proyectofinaltandaclara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinaltandaclara.TandaApplication
import com.example.proyectofinaltandaclara.ui.screens.crear.CrearTandaScreen
import com.example.proyectofinaltandaclara.ui.screens.crear.CrearTandaViewModel
import com.example.proyectofinaltandaclara.ui.screens.crear.CrearTandaViewModelFactory
import com.example.proyectofinaltandaclara.ui.screens.detalle.DetalleTandaScreen
import com.example.proyectofinaltandaclara.ui.screens.detalle.DetalleTandaViewModel
import com.example.proyectofinaltandaclara.ui.screens.detalle.DetalleTandaViewModelFactory
import com.example.proyectofinaltandaclara.ui.screens.lista.ListaTandasScreen
import com.example.proyectofinaltandaclara.ui.screens.lista.ListaTandasViewModel
import com.example.proyectofinaltandaclara.ui.screens.lista.ListaTandasViewModelFactory
import com.example.proyectofinaltandaclara.ui.screens.participantes.ParticipantesScreen
import com.example.proyectofinaltandaclara.ui.screens.participantes.ParticipantesViewModel
import com.example.proyectofinaltandaclara.ui.screens.participantes.ParticipantesViewModelFactory

/**
 * Gráfica de navegación de Compose que enlaza las pantallas con sus respectivos ViewModels.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.ListaTandas.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as TandaApplication
    val repository = app.repository

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla 1: Mis Tandas (Lista)
        composable(route = Screen.ListaTandas.route) {
            val viewModel: ListaTandasViewModel = viewModel(
                factory = ListaTandasViewModelFactory(repository)
            )
            ListaTandasScreen(
                viewModel = viewModel,
                onCrearTandaClick = {
                    navController.navigate(Screen.CrearTanda.route)
                },
                onTandaClick = { tandaId ->
                    navController.navigate(Screen.DetalleTanda.createRoute(tandaId))
                },
                onGestionarParticipantesClick = { tandaId ->
                    navController.navigate(Screen.Participantes.createRoute(tandaId))
                }
            )
        }

        // Pantalla 2: Nueva Tanda
        composable(route = Screen.CrearTanda.route) {
            val viewModel: CrearTandaViewModel = viewModel(
                factory = CrearTandaViewModelFactory(repository)
            )
            CrearTandaScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onTandaCreada = { tandaId ->
                    navController.popBackStack()
                    navController.navigate(Screen.Participantes.createRoute(tandaId))
                }
            )
        }

        // Pantalla 3: Gestión de Participantes
        composable(
            route = Screen.Participantes.route,
            arguments = listOf(
                navArgument("tandaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tandaId = backStackEntry.arguments?.getLong("tandaId") ?: 0L
            val viewModel: ParticipantesViewModel = viewModel(
                key = "participantes_$tandaId",
                factory = ParticipantesViewModelFactory(tandaId, repository)
            )
            ParticipantesScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        // Pantalla 4: Detalle de Tanda (con pestañas Pagos, Turnos, Resumen)
        composable(
            route = Screen.DetalleTanda.route,
            arguments = listOf(
                navArgument("tandaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tandaId = backStackEntry.arguments?.getLong("tandaId") ?: 0L
            val viewModel: DetalleTandaViewModel = viewModel(
                key = "detalle_$tandaId",
                factory = DetalleTandaViewModelFactory(tandaId, repository)
            )
            DetalleTandaScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onGestionarParticipantes = { id ->
                    navController.navigate(Screen.Participantes.createRoute(id))
                }
            )
        }
    }
}
