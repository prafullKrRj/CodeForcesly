package com.prafullkumar.codeforcesly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prafullkumar.codeforcesly.ui.theme.CodeForceslyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeForceslyTheme {
//                ProfileScreen(ProfileViewModel())
//                ContestsScreen(ContestsViewModel())
//                ProblemsScreen(ProblemsViewModel()) { }
                AppNavigation()
            }
        }
    }
}