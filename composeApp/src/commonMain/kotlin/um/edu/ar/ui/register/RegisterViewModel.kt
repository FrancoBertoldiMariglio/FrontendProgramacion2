import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import um.edu.ar.network.RegisterService
import um.edu.ar.ui.register.RegisterModel

class RegisterViewModel : ViewModel() {

    private val client = HttpClient(CIO)
    private val registerService = RegisterService(client)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _login = MutableStateFlow("")
    val login: StateFlow<String> = _login

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _registerEnable = MutableStateFlow(false)
    val registerEnable: StateFlow<Boolean> = _registerEnable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

    fun onRegisterChanged(login: String, email: String, password: String) {
        _login.value = login
        _email.value = email
        _password.value = password
        _registerEnable.value = isValidEmail(email) && isValidPassword(password) && login.isNotEmpty()
    }

    private fun isValidPassword(password: String): Boolean = password.length > 8

    private fun isValidEmail(email: String): Boolean = emailPattern.matches(email)

    fun onRegisterSelected(navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val registerModel = RegisterModel(login.value, email.value, password.value)
            val response = registerService.register(registerModel)

            println(response)

            _isLoading.value = false
            if (response.success) {
                navController.navigate("login")
            } else {
                _errorMessage.value = "Registro fallido"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
