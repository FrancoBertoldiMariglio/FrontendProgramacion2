package um.edu.ar.ui.buy

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import um.edu.ar.Constants

class BuyService(private val client: HttpClient) {

    val settings: Settings = Settings()
    suspend fun processPurchase(ventaRequest: VentaRequest): Result<BuyResponse> {
        val token = settings.getString("jwtToken", "")
        return try {
            println("BuyService - Realizando compra")
            val response = client.post(Constants.BASE_URL + "/ventas") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(ventaRequest)
            }
            println("BuyService - Compra realizada con éxito $response")
            Result.success(BuyResponse("Compra realizada con éxito"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}