package um.edu.ar.utils

import DispositivosScreen
import DispositivosViewModel
import LoginViewModel
import RegisterViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.json.Json
import um.edu.ar.ui.buy.BuyScreen
import um.edu.ar.ui.buy.BuyViewModel
import um.edu.ar.ui.dispositivos.Dispositivo
import um.edu.ar.ui.register.RegisterScreen
import um.edu.ar.ui.login.LoginScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login" 
    ) {
        composable("login") {
            LoginScreen(viewModel = LoginViewModel(), navController = navController)
        }
        composable("register") {
            RegisterScreen(viewModel = RegisterViewModel(), navController = navController)
        }
        composable("dispositivos") {
            DispositivosScreen(viewModel = DispositivosViewModel(), navController = navController)
        }
        composable(
            route = "buy/{dispositivoJson}",
            arguments = listOf(navArgument("dispositivoJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val dispositivoJson = backStackEntry.arguments?.getString("dispositivoJson")
            dispositivoJson?.let {
                val dispositivo = Json.decodeFromString<Dispositivo>(it)
                BuyScreen(dispositivo = dispositivo, viewModel = BuyViewModel(), navController = navController)
            }
        }
    }
}