package um.edu.ar.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import um.edu.ar.ui.dispositivos.AdicionalModel
import um.edu.ar.ui.dispositivos.DispositivosService
import um.edu.ar.ui.dispositivos.OpcionModel
import um.edu.ar.utils.createPlatformHttpClient


class BuyViewModel : ViewModel() {
    private val client = createPlatformHttpClient()
    private val buyService = BuyService(client)
    private val dispositivosService = DispositivosService(client)

    private val _uiState = MutableStateFlow(BuyModel())
    val uiState: StateFlow<BuyModel> = _uiState

    fun loadDispositivo(id: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                dispositivosService.getDispositivo(id)
                    .onSuccess { dispositivo ->
                        _uiState.value = _uiState.value.copy(
                            dispositivo = dispositivo,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            dispositivo = null,
                            isLoading = false,
                            error = exception.message ?: "Error al cargar el dispositivo"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    dispositivo = null,
                    error = "Error al cargar el dispositivo: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun selectOption(personalizacionId: Int, opcion: OpcionModel) {
        val currentState = _uiState.value
        val newOptions = currentState.selectedOptions.toMutableMap().apply {
            put(personalizacionId, opcion)
        }
        updatePrice(newOptions, currentState.selectedAdicionales)
    }

    fun toggleAdicional(adicional: AdicionalModel) {
        val currentState = _uiState.value
        val newAdicionales = currentState.selectedAdicionales.toMutableList().apply {
            if (contains(adicional)) remove(adicional) else add(adicional)
        }
        updatePrice(currentState.selectedOptions, newAdicionales)
    }

    private fun updatePrice(
        options: Map<Int, OpcionModel>,
        adicionales: List<AdicionalModel>
    ) {
        val currentState = _uiState.value
        val dispositivo = currentState.dispositivo ?: return

        val basePrice = dispositivo.precioBase
        val optionsPrice = options.values.sumOf { it.precioAdicional }
        val adicionalesPrice = adicionales.sumOf { adicional ->
            if (adicional.precioGratis != -1.0 && basePrice + optionsPrice > adicional.precioGratis) {
                0.0
            } else {
                adicional.precio
            }
        }

        _uiState.value = currentState.copy(
            selectedOptions = options,
            selectedAdicionales = adicionales,
            finalPrice = basePrice + optionsPrice + adicionalesPrice
        )
    }

    fun processPurchase() {
        val currentState = _uiState.value
        val dispositivo = currentState.dispositivo ?: return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)
            try {
                val buyRequest = BuyRequest(
                    idDispositivo = dispositivo.id,
                    personalizaciones = currentState.selectedOptions.map { (id, opcion) ->
                        SeleccionPersonalizacion(id, opcion.precioAdicional)
                    },
                    adicionales = currentState.selectedAdicionales.map { adicional ->
                        SeleccionAdicional(adicional.id, adicional.precio)
                    },
                    precioFinal = currentState.finalPrice,
                    fechaVenta = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
                )

                buyService.processPurchase(buyRequest)
                    .onSuccess { buyResponse ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Error al procesar la compra: ${e.message}"
                )
            }
        }
    }
}