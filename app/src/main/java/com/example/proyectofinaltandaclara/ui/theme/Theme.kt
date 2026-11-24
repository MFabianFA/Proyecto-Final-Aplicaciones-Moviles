package com.example.proyectofinaltandaclara.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = Color.White,
    primaryContainer = VerdeClaroContraste,
    onPrimaryContainer = VerdeOscuro,
    secondary = DoradoAcento,
    onSecondary = Color.Black,
    secondaryContainer = DoradoSuave,
    onSecondaryContainer = Color(0xFF523B00),
    error = RojoError,
    onError = Color.White,
    errorContainer = RojoSuave,
    onErrorContainer = Color(0xFF5C1407),
    background = GrisFondo,
    onBackground = TextoPrincipal,
    surface = GrisSuperficie,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFECEFEF),
    onSurfaceVariant = TextoSecundario
)

@Composable
fun TandaClaraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
