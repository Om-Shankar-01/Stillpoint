package com.example.stillpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.stillpoint.ui.Archive
import com.example.stillpoint.ui.Queue
import com.example.stillpoint.ui.Reader
import com.example.stillpoint.ui.archivescreen.ArchiveScreen
import com.example.stillpoint.ui.homescreen.HomeScreen
import com.example.stillpoint.ui.readerScreen.ReaderScreen
import com.example.stillpoint.ui.theme.StillpointTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StillpointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Queue) {
                        composable<Queue> {
                            HomeScreen(navController = navController)
                        }

                        composable<Archive> {
                            ArchiveScreen(navController = navController)
                        }

                        composable<Reader> {backStackEntry ->
                            val readerScreen: Reader = backStackEntry.toRoute()
                            ReaderScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}