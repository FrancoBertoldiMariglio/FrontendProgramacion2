package um.edu.ar.ui.buy

import kotlinx.serialization.Serializable

@Serializable
data class BuyRequest (
    val idDispositivo: Int,
    val personalizaciones: List<SeleccionPersonalizacion>,
    val adicionales: List<SeleccionAdicional>,
    val precioFinal: Double,
    val fechaVenta: String
)

@Serializable
data class SeleccionPersonalizacion (
    val id: Int,
    val precio: Double
)

@Serializable
data class SeleccionAdicional (
    val id: Int,
    val precio: Double
)