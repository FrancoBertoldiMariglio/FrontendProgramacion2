package um.edu.ar.ui.buy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import um.edu.ar.ui.dispositivos.Dispositivo
import um.edu.ar.ui.dispositivos.Opcion
import um.edu.ar.ui.dispositivos.Adicional

class BuyViewModel {

    private val _dispositivo = MutableStateFlow<Dispositivo?>(null)
    val dispositivo: StateFlow<Dispositivo?> = _dispositivo

    private val _selectedOptions = MutableStateFlow<Map<Int, Opcion>>(emptyMap())
    val selectedOptions: StateFlow<Map<Int, Opcion>> = _selectedOptions

    private val _selectedAdicionales = MutableStateFlow<List<Adicional>>(emptyList())
    val selectedAdicionales: StateFlow<List<Adicional>> = _selectedAdicionales

    private val _finalPrice = MutableStateFlow(0.0)
    val finalPrice: StateFlow<Double> = _finalPrice

    fun getDispositivoById(id: String): Dispositivo? {
        return _dispositivo.value
    }

    fun selectOption(personalizacionId: Int, opcion: Opcion) {
        _selectedOptions.value = _selectedOptions.value.toMutableMap().apply {
            put(personalizacionId, opcion)
        }
        _finalPrice.value = calculateFinalPrice()
    }

    fun toggleAdicional(adicional: Adicional) {
        _selectedAdicionales.value = _selectedAdicionales.value.toMutableList().apply {
            if (contains(adicional)) {
                remove(adicional)
            } else {
                add(adicional)
            }
        }
        _finalPrice.value = calculateFinalPrice()
    }

    private fun calculateFinalPrice(): Double {
        val dispositivo = _dispositivo.value ?: return 0.0
        val basePrice = dispositivo.precioBase

        val personalizacionesPrice = _selectedOptions.value.values.sumOf { it.precioAdicional }

        val adicionalesPrice = _selectedAdicionales.value.sumOf { adicional ->
            if (adicional.precioGratis != -1.0 && basePrice + personalizacionesPrice > adicional.precioGratis) {
                0.0
            } else {
                adicional.precio
            }
        }

        return basePrice + personalizacionesPrice + adicionalesPrice
    }

    fun createBuyRequest(): BuyRequest? {
        val dispositivo = _dispositivo.value ?: return null
        val finalPrice = calculateFinalPrice()
        val fechaVenta = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()

        val personalizaciones = _selectedOptions.value.map { (id, opcion) ->
            SeleccionPersonalizacion(id, opcion.precioAdicional)
        }

        val adicionales = _selectedAdicionales.value.map { adicional ->
            val adicionalPrice = if (adicional.precioGratis != -1.0 && finalPrice > adicional.precioGratis) {
                0.0
            } else {
                adicional.precio
            }
            SeleccionAdicional(adicional.id, adicionalPrice)
        }

        return BuyRequest(
            idDispositivo = dispositivo.id,
            personalizaciones = personalizaciones,
            adicionales = adicionales,
            precioFinal = finalPrice,
            fechaVenta = fechaVenta
        )
    }
}