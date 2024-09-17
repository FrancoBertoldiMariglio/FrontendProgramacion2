package um.edu.ar.ui.register

data class RegisterModel(
    val login: String,
    val email: String,
    val password: String,
) {
    companion object {
        val Empty = RegisterModel("", "", "")
    }
}