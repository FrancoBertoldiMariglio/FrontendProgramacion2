package um.edu.ar.ui.login

data class LoginModel(
    val email: String,
    val password: String,
) {
    companion object {
        val Empty = LoginModel("", "")
    }
}