package com.example.proyectofinaltandaclara.utils

import com.example.proyectofinaltandaclara.data.model.Periodicidad
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.data.model.Tanda
import com.example.proyectofinaltandaclara.data.repository.TandaRepository
import java.time.LocalDate

/**
 * Helper para generar una tanda demostrativa de prueba ("Tanda de la Oficina")
 * con 10 integrantes, periodicidad semanal y pagos registrados en periodos pasados y actual.
 */
object DatosEjemploHelper {

    suspend fun crearTandaDeEjemplo(repository: TandaRepository): Long {
        val fechaInicioHaceTresSemanas = LocalDate.now().minusWeeks(3)

        val tandaEjemplo = Tanda(
            nombre = "Tanda de la Oficina",
            montoPorPersona = 500.0,
            periodicidad = Periodicidad.SEMANAL,
            fechaInicio = fechaInicioHaceTresSemanas
        )

        val tandaId = repository.insertarTanda(tandaEjemplo)

        val nombresIntegrantes = listOf(
            "Juan Pérez" to "555-123-4567",
            "María López" to "555-234-5678",
            "Carlos Gómez" to "555-345-6789",
            "Ana Martínez" to "555-456-7890",
            "Roberto Hernández" to "555-567-8901",
            "Sofía Rodríguez" to "555-678-9012",
            "Luis García" to "555-789-0123",
            "Elena Sánchez" to "555-890-1234",
            "Miguel Torres" to "555-901-2345",
            "Laura Ramírez" to "555-012-3456"
        )

        val participantes = nombresIntegrantes.mapIndexed { index, (nombre, tel) ->
            Participante(
                tandaId = tandaId,
                nombre = nombre,
                telefono = tel,
                turno = index + 1
            )
        }

        val idsGenerados = repository.insertarParticipantes(participantes)

        // Periodo 1 (Hace 3 semanas): Todos pagaron
        idsGenerados.forEach { pId ->
            repository.registrarPago(tandaId, pId, 1, fechaInicioHaceTresSemanas)
        }

        // Periodo 2 (Hace 2 semanas): 9 pagaron (Miguel #9 adeuda)
        idsGenerados.take(8).forEach { pId ->
            repository.registrarPago(tandaId, pId, 2, fechaInicioHaceTresSemanas.plusDays(7))
        }
        repository.registrarPago(tandaId, idsGenerados[9], 2, fechaInicioHaceTresSemanas.plusDays(7))

        // Periodo 3 (Hace 1 semana): 7 pagaron (Carlos #3, Miguel #9, Laura #10 adeudaron)
        listOf(0, 1, 3, 4, 5, 6, 7).forEach { index ->
            repository.registrarPago(tandaId, idsGenerados[index], 3, fechaInicioHaceTresSemanas.plusDays(14))
        }

        // Periodo 4 (Actual): 3 personas pagaron (Juan #1, María #2, Roberto #5)
        listOf(0, 1, 4).forEach { index ->
            repository.registrarPago(tandaId, idsGenerados[index], 4, LocalDate.now())
        }

        return tandaId
    }
}
