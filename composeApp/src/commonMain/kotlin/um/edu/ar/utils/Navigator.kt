package um.edu.ar.utils

import BuyScreen
import DispositivosScreen
import DispositivosViewModel
import LoginViewModel
import RegisterViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import um.edu.ar.ui.buy.BuyService
import um.edu.ar.ui.buy.BuyViewModel
import um.edu.ar.ui.dispositivos.DispositivosService
import um.edu.ar.ui.login.LoginScreen
import um.edu.ar.ui.register.RegisterScreen
import um.edu.ar.utils.createPlatformHttpClient

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val httpClient = createPlatformHttpClient()

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
            val buyService = BuyService(httpClient)
            val dispositivosService = DispositivosService(httpClient)
            val buyViewModel = BuyViewModel(buyService, dispositivosService)

            BuyScreen(
                dispositivoId = dispositivoId,
                viewModel = buyViewModel,
                navController = navController
            )
        }
    }
}