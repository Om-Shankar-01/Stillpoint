package com.example.stillpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stillpoint.transitions.*
import com.example.stillpoint.ui.AppDrawer
import com.example.stillpoint.ui.Archive
import com.example.stillpoint.ui.Queue
import com.example.stillpoint.ui.Reader
import com.example.stillpoint.ui.archivescreen.ArchiveScreen
import com.example.stillpoint.ui.homescreen.HomeScreen
import com.example.stillpoint.ui.readerScreen.ReaderScreen
import com.example.stillpoint.ui.theme.StillpointTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AppDrawer(
                                currentDestination = currentDestination,
                                onNavigateToQueue = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate(Queue) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onNavigateToArchive = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate(Archive) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Queue,
                            enterTransition = { stackIn() },
                            exitTransition = { stackOut() },
                            popEnterTransition = { stackPopIn() },
                            popExitTransition = { stackPopOut() }
                        ) {

                            composable<Queue> {
                                HomeScreen(
                                    navController = navController,
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }

                            composable<Archive> {
                                ArchiveScreen(
                                    navController = navController,
                                )
                            }

                            composable<Reader> {
                                ReaderScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
