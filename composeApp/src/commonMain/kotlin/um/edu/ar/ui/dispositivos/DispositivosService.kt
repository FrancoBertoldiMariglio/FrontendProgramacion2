package um.edu.ar.ui.dispositivos

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import um.edu.ar.Constants

@Serializable
data class DispositivosResponse(val success: Boolean, val dispositivos: List<DispositivoModel>?)

@Serializable
data class DispositivoResponse(val success: Boolean, val dispositivo: DispositivoModel?)

class DispositivosService(private val client: HttpClient) {

    val settings: Settings = Settings()

    suspend fun getDispositivos(): DispositivosResponse {
        val token = settings.getString("jwtToken", "")
        // ver de usar una interfaz virtual para que cambie dinamicamenta la IP
        val response: HttpResponse = client.get(Constants.BASE_URL + "/dispositivos") {
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

    suspend fun getDispositivo(id: Int): Result<DispositivoModel> {
        try {
            val token = settings.getString("jwtToken", "")
            println("Obteniendo dispositivo con ID: $id") // Log para debug

            val response: HttpResponse = client.get(Constants.BASE_URL + "/dispositivos/$id") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
            }

            println("Status de la respuesta: ${response.status}") // Log para debug

            if (response.status == HttpStatusCode.OK) {
                val dto: DispositivoDTO = response.body()
                println("Dispositivo recibido: ${dto.nombre}") // Log para debug
                println("Características: ${dto.caracteristicas.size}") // Log para debug
                println("Personalizaciones: ${dto.personalizaciones.size}") // Log para debug
                println("Adicionales: ${dto.adicionales.size}") // Log para debug

                val model = dto.toModel()
                return Result.success(model)
            } else {
                println("Error en la respuesta: ${response.status}") // Log para debug
                return Result.failure(Exception("Error al obtener el dispositivo: ${response.status}"))
            }
        } catch (e: Exception) {
            println("Error en getDispositivo: ${e.message}") // Log para debug
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}