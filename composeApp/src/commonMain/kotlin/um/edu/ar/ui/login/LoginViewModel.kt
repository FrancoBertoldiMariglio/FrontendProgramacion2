import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import um.edu.ar.ui.login.LoginService
import um.edu.ar.ui.login.LoginModel
import um.edu.ar.utils.createPlatformHttpClient

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

//    private val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

    fun onLoginChanged(username: String, password: String) {
        _username.value = username
        _password.value = password
        _loginEnable.value = isValidPassword(password)
    }

    private fun isValidPassword(password: String): Boolean = password.length > 8

//    private fun isValidEmail(email: String): Boolean = emailPattern.matches(email)

    fun onLoginSelected(navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null

            val loginModel = LoginModel(username.value, password.value)
            val loginResponse = loginService.login(loginModel)

            _isLoading.value = false

            if (loginResponse.success) {
                navController.navigate("dispositivos")
            } else {
                _loginError.value = "Login failed"
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }
}