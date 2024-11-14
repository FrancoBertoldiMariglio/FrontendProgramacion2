package um.edu.ar.ui.buy

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient

import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable


@Serializable
data class BuyResponse(
    val message: String
)

class BuyService(private val client: HttpClient) {
    val settings: Settings = Settings()

    suspend fun processPurchase(buyRequest: BuyRequest): Result<BuyResponse> {
        return try {
            val token = settings.getString("jwtToken", "")
            val response: HttpResponse = client.post("http://192.168.1.37:8080/api/ventas") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(buyRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(BuyResponse(message = "Compra realizada con éxito"))
            } else {
                Result.failure(Exception("Error al procesar la compra: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}