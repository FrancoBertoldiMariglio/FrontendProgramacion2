package um.edu.ar.utils

//import BuyScreen
import DispositivosScreen
import DispositivosViewModel
import LoginViewModel
import RegisterViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import um.edu.ar.ui.buy.BuyScreen
import um.edu.ar.ui.buy.BuyService
import um.edu.ar.ui.buy.BuyViewModel
import um.edu.ar.ui.dispositivos.DispositivosService
import um.edu.ar.ui.login.LoginScreen
import um.edu.ar.ui.register.RegisterScreen
import um.edu.ar.utils.createPlatformHttpClient

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val httpClient = remember { createPlatformHttpClient() }
    val services = remember {
        Pair(
            BuyService(httpClient),
            DispositivosService(httpClient)
        )
    }
    val buyViewModel = remember {
        BuyViewModel(services.first, services.second)
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(),
                navController = navController
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = RegisterViewModel(),
                navController = navController
            )
        }
        composable("dispositivos") {
            DispositivosScreen(
                viewModel = DispositivosViewModel(),
                navController = navController
            )
        }
        composable(
            route = "buy/{dispositivoId}",
            arguments = listOf(navArgument("dispositivoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dispositivoId = backStackEntry.arguments?.getInt("dispositivoId") ?: return@composable

            LaunchedEffect(buyViewModel, dispositivoId) {
                buyViewModel.loadDispositivo(dispositivoId)
            }

            BuyScreen(
                viewModel = buyViewModel,
                dispositivoId = dispositivoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}