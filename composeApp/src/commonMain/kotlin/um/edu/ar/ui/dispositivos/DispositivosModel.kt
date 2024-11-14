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

@Serializable
data class DispositivoDTO(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precioBase: Double,
    val moneda: String,
    val caracteristicas: List<CaracteristicaDTO>,
    val personalizaciones: List<PersonalizacionDTO>,
    val adicionales: List<AdicionalDTO>
)

@Serializable
data class CaracteristicaDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

@Serializable
data class PersonalizacionDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val opciones: List<OpcionDTO>
)

@Serializable
data class OpcionDTO(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precioAdicional: Double
)

@Serializable
data class AdicionalDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val precioGratis: Double,
    val dispositivos: List<Int> = emptyList()
)

fun DispositivoDTO.toModel() = DispositivoModel(
    id = id,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    precioBase = precioBase,
    moneda = moneda,
    caracteristicas = caracteristicas.map { it.toModel() },
    personalizaciones = personalizaciones.map { it.toModel() },
    adicionales = adicionales.map { it.toModel() }
)

fun CaracteristicaDTO.toModel() = CaracteristicaModel(
    id = id,
    nombre = nombre,
    descripcion = descripcion
)

fun PersonalizacionDTO.toModel() = PersonalizacionModel(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    opciones = opciones.map { it.toModel() }
)

fun OpcionDTO.toModel() = OpcionModel(
    id = id,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    precioAdicional = precioAdicional
)

fun AdicionalDTO.toModel() = AdicionalModel(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    precioGratis = precioGratis
)