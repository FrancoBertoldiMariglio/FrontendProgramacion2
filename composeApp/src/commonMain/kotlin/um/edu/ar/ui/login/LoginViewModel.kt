import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import um.edu.ar.ui.login.LoginService
import um.edu.ar.ui.login.LoginModel
import um.edu.ar.utils.createPlatformHttpClient

private val settings: Settings = Settings()


class LoginViewModel : ViewModel() {
    private val client = createPlatformHttpClient()
    private val loginService = LoginService(client)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loginEnable = MutableStateFlow(false)
    val loginEnable: StateFlow<Boolean> = _loginEnable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    fun onLoginChanged(username: String, password: String) {
        _username.value = username
        _password.value = password
        _loginEnable.value = isValidPassword(password)
    }

    private fun isValidPassword(password: String): Boolean = password.length > 8

    fun onLoginSelected(navController: NavController) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _loginError.value = null

                if (username.value.isBlank() || password.value.isBlank()) {
                    _loginError.value = "Username and password are required"
                    return@launch
                }

                val loginModel = LoginModel(username.value, password.value)
                val loginResponse = loginService.login(loginModel)
                val token = loginResponse.id_token ?: ""

                if (token.isNotEmpty()) {
                    settings.putString("jwtToken", token)
                    settings.putLong("userId", loginResponse.user_id)
                    settings.putString("roles", loginResponse.roles.joinToString(",") { it.name })

                    navController.navigate("dispositivos")
                } else {
                    _loginError.value = "Login failed"
                    settings.remove("jwtToken")
                    settings.remove("userId")
                    settings.remove("roles")
                }
            } catch (e: Exception) {
                _loginError.value = "An error occurred: ${e.message}"
                settings.remove("jwtToken")
                settings.remove("userId")
                settings.remove("roles")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }
}