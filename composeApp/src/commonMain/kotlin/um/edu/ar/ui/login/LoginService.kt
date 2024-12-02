package um.edu.ar.ui.login

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import kotlinx.serialization.Serializable
import um.edu.ar.Constants

@Serializable
data class Authority(val name: String)

@Serializable
data class LoginResponse(
    val idToken: String?,
    val userId: Long,
    val roles: Set<Authority>,
    val login: String
)

class LoginService(private val client: HttpClient) {
    suspend fun login(loginModel: LoginModel): LoginResponse {
        try {
            val response = client.post(Constants.BASE_URL + "/authenticate") {
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