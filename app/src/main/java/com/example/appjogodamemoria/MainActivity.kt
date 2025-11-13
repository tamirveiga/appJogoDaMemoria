package com.example.appjogodamemoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appjogodamemoria.ui.theme.TemaAppJogoDaMemoria
import com.example.appjogodamemoria.ui.telas.NavegacaoApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemaAppJogoDaMemoria {
                NavegacaoApp()
            }
        }
    }
}