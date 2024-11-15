package um.edu.ar.ui.buy

import kotlinx.serialization.Serializable
import um.edu.ar.ui.dispositivos.DispositivoModel

data class BuyModel(
    val dispositivo: DispositivoModel? = null,
    val selectedOptions: Map<Int, Pair<Int, Double>> = emptyMap(),
    val selectedAdicionales: Map<Int, Double> = emptyMap(),
    val precioFinal: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Serializable
data class UserDTO(
    val id: Int,
    val username: String
)

@Serializable
data class VentaRequest(
    val fechaVenta: String,
    val ganancia: Double,
    val user: UserDTO,
    val idDispositivo: Int,
    val personalizaciones: List<SeleccionPersonalizacion>,
    val adicionales: List<SeleccionAdicional>,
    val precioFinal: Double
)

@Serializable
data class SeleccionPersonalizacion(
    val id: Int,
    val precio: Double
)

@Serializable
data class SeleccionAdicional(
    val id: Int,
    val precio: Double
)

@Serializable
data class BuyResponse(
    val message: String
)