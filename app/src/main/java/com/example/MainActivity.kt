package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CumaViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Ana Sayfa", Icons.Default.Home)
    object Contacts : Screen("contacts", "Kişiler", Icons.Default.People)
    object Messages : Screen("messages", "Mesajlar", Icons.Default.MenuBook)
    object Cards : Screen("cards", "Cuma Kartı", Icons.Default.PhotoLibrary)
    object Schedule : Screen("schedule", "Zamanlayıcı", Icons.Default.AccessTime)
}

class MainActivity : ComponentActivity() {
    private val viewModel: CumaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (intent.getBooleanExtra("EXTRA_START_SEND_FLOW", false)) {
            viewModel.startSequentialSend()
        }

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("EXTRA_START_SEND_FLOW", false)) {
            viewModel.startSequentialSend()
        }
    }
}

@Composable
fun MainAppScreen(viewModel: CumaViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val snackbarHostState = remember { SnackbarHostState() }
    val statusMessage by viewModel.statusMessage.collectAsState()
    val sequentialSendState by viewModel.sequentialSendState.collectAsState()

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* result handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    val screens = listOf(
        Screen.Home,
        Screen.Contacts,
        Screen.Messages,
        Screen.Cards,
        Screen.Schedule
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                screen.title,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToContacts = { navController.navigate(Screen.Contacts.route) },
                    onNavigateToMessages = { navController.navigate(Screen.Messages.route) },
                    onNavigateToCardCreator = { navController.navigate(Screen.Cards.route) },
                    onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) }
                )
            }
            composable(Screen.Contacts.route) {
                ContactsScreen(viewModel = viewModel)
            }
            composable(Screen.Messages.route) {
                MessagesScreen(viewModel = viewModel)
            }
            composable(Screen.Cards.route) {
                CardCreatorScreen(viewModel = viewModel)
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(viewModel = viewModel)
            }
        }

        // Sequential WhatsApp Dispatcher Dialog
        if (sequentialSendState.isActive) {
            SequentialSendDialog(
                status = sequentialSendState,
                onAdvance = { isSent -> viewModel.advanceSequentialSend(isSent) },
                onCancel = { viewModel.cancelSequentialSend() }
            )
        }
    }
}
