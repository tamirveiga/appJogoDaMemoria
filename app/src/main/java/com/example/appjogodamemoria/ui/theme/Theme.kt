package com.example.appjogodamemoria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.appjogodamemoria.ui.theme.FundoClaro
import com.example.appjogodamemoria.ui.theme.LaranjaPastel
import com.example.appjogodamemoria.ui.theme.TextoEscuro
import com.example.appjogodamemoria.ui.theme.TipografiaApp
import com.example.appjogodamemoria.ui.theme.VerdePastel

private val esquemaCores = lightColorScheme(
    primary = VerdePastel,
    secondary = LaranjaPastel,
    background = FundoClaro,
    onPrimary = TextoEscuro
)

@Composable
fun TemaAppJogoDaMemoria(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = esquemaCores,
        typography = TipografiaApp,
        content = content
    )
}