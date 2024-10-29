package um.edu.ar.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import um.edu.ar.ui.register.RegisterModel

@Serializable
data class RegisterResponse(val success: Boolean, val message: String?)

class RegisterService(private val client: HttpClient) {
    suspend fun register(registerModel: RegisterModel): RegisterResponse {
        val response: HttpResponse = client.post("http://localhost:8080/api/register") {
            contentType(ContentType.Application.Json)
            setBody(registerModel)
        }
        return if (response.status == HttpStatusCode.Created) {
            RegisterResponse(success = true, message = "Registro exitoso")
        } else {
            RegisterResponse(success = false, message = "Error en el registro")
        }
    }
}
