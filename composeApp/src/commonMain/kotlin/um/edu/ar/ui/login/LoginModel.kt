package um.edu.ar.ui.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginModel(
    val username: String,
    val password: String,
) {
    companion object {
        val Empty = LoginModel("", "")
    }
}