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
    val id_token: String?,
    val user_id: Long,
    val roles: Set<Authority>
)

class LoginService(private val client: HttpClient) {
    suspend fun login(loginModel: LoginModel): LoginResponse {
        return try {
            val response = client.post("http://localhost:8080/api/authenticate") {
                contentType(ContentType.Application.Json)
                setBody(loginModel)
            }
            response.body()
        } catch (e: Exception) {
            throw e
        }
    }
}