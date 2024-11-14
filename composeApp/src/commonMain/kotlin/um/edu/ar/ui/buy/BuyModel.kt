package um.edu.ar.ui.buy

import kotlinx.serialization.Serializable
import um.edu.ar.ui.dispositivos.AdicionalModel
import um.edu.ar.ui.dispositivos.DispositivoModel
import um.edu.ar.ui.dispositivos.OpcionModel

data class BuyModel(
    val dispositivo: DispositivoModel? = null,
    val selectedOptions: Map<Int, OpcionModel> = emptyMap(),
    val selectedAdicionales: List<AdicionalModel> = emptyList(),
    val finalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Serializable
data class BuyRequest(
    val idDispositivo: Int,
    val personalizaciones: List<SeleccionPersonalizacion>,
    val adicionales: List<SeleccionAdicional>,
    val precioFinal: Double,
    val fechaVenta: String
)

@Serializable
data class SeleccionPersonalizacion(
    val idPersonalizacion: Int,
    val precio: Double
)

@Serializable
data class SeleccionAdicional(
    val idAdicional: Int,
    val precio: Double
)