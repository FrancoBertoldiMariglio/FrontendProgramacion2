package um.edu.ar.ui.buy

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class BuyService(private val client: HttpClient) {
    suspend fun processPurchase(ventaRequest: VentaRequest): Result<BuyResponse> {
        return try {
            val response = client.post("http://192.168.1.37:8080/api/ventas") {
                contentType(ContentType.Application.Json)
                setBody(ventaRequest)
            }
            Result.success(BuyResponse("Compra realizada con éxito"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}