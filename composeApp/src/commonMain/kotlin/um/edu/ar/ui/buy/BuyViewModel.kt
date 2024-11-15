package um.edu.ar.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import um.edu.ar.ui.dispositivos.DispositivosService

class BuyViewModel(
    private val buyService: BuyService,
    private val dispositivosService: DispositivosService
) : ViewModel() {
    private val _uiState = MutableStateFlow(BuyModel())
    val uiState: StateFlow<BuyModel> = _uiState

    fun loadDispositivo(id: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                println("BuyViewModel - Iniciando carga de dispositivo ID: $id")

                val result = dispositivosService.getDispositivo(id)
                println("BuyViewModel - Resultado obtenido: $result")

                result.fold(
                    onSuccess = { dispositivo ->
                        println("BuyViewModel - Dispositivo cargado exitosamente: ${dispositivo.nombre}")
                        val initialOptions = dispositivo.personalizaciones
                            .mapNotNull { pers ->
                                pers.opciones.firstOrNull()?.let { firstOption ->
                                    pers.id to (firstOption.id to (firstOption.precioAdicional ?: 0.0))
                                }
                            }
                            .toMap()

                        _uiState.update {
                            it.copy(
                                dispositivo = dispositivo,
                                selectedOptions = initialOptions,
                                precioFinal = dispositivo.precioBase,
                                isLoading = false,
                                error = null
                            )
                        }
                        println("BuyViewModel - Estado actualizado con dispositivo")
                    },
                    onFailure = { error ->
                        println("BuyViewModel - Error al cargar dispositivo: ${error.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Error desconocido",
                                dispositivo = null
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                println("BuyViewModel - Excepción al cargar dispositivo: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido",
                        dispositivo = null
                    )
                }
            }
        }
    }

    fun processPurchase(userId: Int, username: String) {
        val currentState = _uiState.value
        val dispositivo = currentState.dispositivo ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val ventaRequest = VentaRequest(
                    fechaVenta = Clock.System.now().toString(),
                    ganancia = currentState.precioFinal * 0.2, // 20% de ganancia
                    user = UserDTO(userId, username),
                    idDispositivo = dispositivo.id,
                    personalizaciones = currentState.selectedOptions.map { (id, pair) ->
                        SeleccionPersonalizacion(id, pair.second)
                    },
                    adicionales = currentState.selectedAdicionales.map { (id, precio) ->
                        SeleccionAdicional(id, precio)
                    },
                    precioFinal = currentState.precioFinal
                )

                buyService.processPurchase(ventaRequest)
                    .onSuccess { _uiState.update { it.copy(isLoading = false) } }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateSelection(personalizacionId: Int, opcionId: Int, precio: Double) {
        _uiState.update { state ->
            val newSelections = state.selectedOptions + (personalizacionId to (opcionId to precio))
            calculatePrices(state.copy(selectedOptions = newSelections))
        }
    }

    fun toggleAdicional(adicionalId: Int, precio: Double) {
        _uiState.update { state ->
            val newAdicionales = state.selectedAdicionales.toMutableMap()
            if (adicionalId in newAdicionales) {
                newAdicionales.remove(adicionalId)
            } else {
                newAdicionales[adicionalId] = precio
            }
            calculatePrices(state.copy(selectedAdicionales = newAdicionales))
        }
    }

    private fun calculatePrices(state: BuyModel): BuyModel {
        val dispositivo = state.dispositivo ?: return state
        val basePrice = dispositivo.precioBase
        val optionsPrice = state.selectedOptions.values.sumOf { it.second }
        val adicionalesPrice = state.selectedAdicionales.values.sum()
        return state.copy(precioFinal = basePrice + optionsPrice + adicionalesPrice)
    }
}