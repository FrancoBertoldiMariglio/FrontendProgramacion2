package um.edu.ar.ui.login

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val success: Boolean, val token: String?)

class LoginService(private val client: HttpClient) {
    suspend fun login(loginModel: LoginModel): LoginResponse {
        val response: HttpResponse = client.post("http://192.168.212.218:8080/api/authenticate") {
            contentType(ContentType.Application.Json)
            setBody(loginModel)
        }
        return if (response.status == HttpStatusCode.OK) {
            val token = response.headers[HttpHeaders.Authorization]
            LoginResponse(success = true, token = token)
        } else {
            LoginResponse(success = false, token = null)
        }
    }
}