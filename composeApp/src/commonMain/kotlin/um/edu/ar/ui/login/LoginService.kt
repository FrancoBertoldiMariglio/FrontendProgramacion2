package um.edu.ar.ui.login

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Authority(val name: String)

@Serializable
data class LoginResponse(
    val idToken: String?,
    val userId: Long,
    val roles: Set<Authority>
)

class LoginService(private val client: HttpClient) {
    suspend fun login(loginModel: LoginModel): LoginResponse {
        try {
            val response = client.post("http://192.168.1.37:8080/api/authenticate") {
                contentType(ContentType.Application.Json)
                setBody(loginModel)
            }
            val responseBody: LoginResponse = response.body()
            return responseBody
        } catch (e: Exception) {
            throw e
        }
    }
}