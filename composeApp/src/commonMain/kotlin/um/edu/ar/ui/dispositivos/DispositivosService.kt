package um.edu.ar.ui.dispositivos

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class DispositivosResponse(val success: Boolean, val dispositivos: List<DispositivoModel>?)

class DispositivosService(private val client: HttpClient) {

    val settings: Settings = Settings()

    suspend fun getDispositivos(): DispositivosResponse {
        val token = settings.getString("jwtToken", "")
        val response: HttpResponse = client.get("http://192.168.0.106:8080/api/dispositivos") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }
        return if (response.status == HttpStatusCode.OK) {
            DispositivosResponse(success = true, dispositivos = response.body())
        } else {
            DispositivosResponse(success = false, dispositivos = null)
        }
    }
}