package um.edu.ar.ui.dispositivos

import kotlinx.serialization.Serializable

@Serializable
data class DispositivoModel (
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precioBase: Double,
    val moneda: String,
    val caracteristicas: List<CaracteristicaModel>,
    val personalizaciones: List<PersonalizacionModel>,
    val adicionales: List<AdicionalModel>,
)

@Serializable
data class CaracteristicaModel (
    val id: Int,
    val nombre: String,
    val descripcion: String
)

@Serializable
data class PersonalizacionModel (
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val opciones: List<OpcionModel>
)

@Serializable
data class OpcionModel (
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precioAdicional: Double
)

@Serializable
data class AdicionalModel (
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val precioGratis: Double
)