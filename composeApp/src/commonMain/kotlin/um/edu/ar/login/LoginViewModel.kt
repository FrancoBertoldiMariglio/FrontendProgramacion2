import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loginEnable = MutableStateFlow(false)
    val loginEnable: StateFlow<Boolean> = _loginEnable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val emailPattern = "^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}$".toRegex()

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidPassword(password: String): Boolean = password.length > 8

    private fun isValidEmail(email: String): Boolean = emailPattern.matches(email)

    suspend fun onLoginSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }
}