package um.edu.ar.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import um.edu.ar.ui.dispositivos.AdicionalModel
import um.edu.ar.ui.dispositivos.DispositivoModel
import um.edu.ar.ui.dispositivos.DispositivosService
import um.edu.ar.ui.dispositivos.OpcionModel
import um.edu.ar.utils.createPlatformHttpClient


class BuyViewModel : ViewModel() {
    private val client = createPlatformHttpClient()
    private val buyService = BuyService(client)
    private val dispositivosService = DispositivosService(client)

    private val _uiState = MutableStateFlow(BuyModel())
    val uiState: StateFlow<BuyModel> = _uiState.asStateFlow()

    private var currentJob: Job? = null
    private var loadedDispositivo: DispositivoModel? = null

    fun loadDispositivo(id: Int) {
        if (_uiState.value.dispositivo?.id == id && !_uiState.value.isLoading) {
            return
        }

        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                withContext(Dispatchers.Default) {
                    dispositivosService.getDispositivo(id)
                        .onSuccess { dispositivo ->
                            withContext(Dispatchers.Main) {
                                val initialOptions = dispositivo.personalizaciones
                                    .mapNotNull { personalizacion ->
                                        personalizacion.opciones.firstOrNull()?.let {
                                            personalizacion.id to it
                                        }
                                    }
                                    .toMap()

                                _uiState.update {
                                    BuyModel(
                                        dispositivo = dispositivo,
                                        selectedOptions = initialOptions,
                                        finalPrice = dispositivo.precioBase,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                        .onFailure { exception ->
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = exception.message
                                    )
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun toggleAdicional(adicional: AdicionalModel) {
        println("Toggling adicional: ${adicional.nombre}")
        _uiState.update { currentState ->
            val newAdicionales = currentState.selectedAdicionales.toMutableList().apply {
                if (contains(adicional)) {
                    println("Removiendo adicional: ${adicional.nombre}")
                    remove(adicional)
                } else {
                    println("Agregando adicional: ${adicional.nombre}")
                    add(adicional)
                }
            }
            val dispositivo = currentState.dispositivo ?: return@update currentState

            val basePrice = dispositivo.precioBase
            val optionsPrice = currentState.selectedOptions.values.sumOf { it.precioAdicional }
            val adicionalesPrice = newAdicionales.sumOf { adicionalItem ->
                if (adicionalItem.precioGratis != -1.0 && basePrice + optionsPrice > adicionalItem.precioGratis) {
                    println("Adicional gratis: ${adicionalItem.nombre}")
                    0.0
                } else {
                    println("Adicional con precio: ${adicionalItem.nombre} - ${adicionalItem.precio}")
                    adicionalItem.precio
                }
            }

            println("Nuevo precio total: ${basePrice + optionsPrice + adicionalesPrice}")
            currentState.copy(
                selectedAdicionales = newAdicionales,
                finalPrice = basePrice + optionsPrice + adicionalesPrice
            )
        }
    }

    fun processPurchase() {
        println("Iniciando proceso de compra")
        val currentState = _uiState.value
        val dispositivo = currentState.dispositivo

        if (dispositivo == null) {
            println("Error: No hay dispositivo seleccionado")
            _uiState.update { it.copy(error = "No hay dispositivo seleccionado") }
            return
        }

        viewModelScope.launch {
            try {
                println("Preparando request de compra para dispositivo: ${dispositivo.nombre}")
                _uiState.update { it.copy(isLoading = true, error = null) }

                val buyRequest = BuyRequest(
                    idDispositivo = dispositivo.id,
                    personalizaciones = currentState.selectedOptions.map { (id, opcion) ->
                        println("Agregando personalización: ID $id, Precio ${opcion.precioAdicional}")
                        SeleccionPersonalizacion(id, opcion.precioAdicional)
                    },
                    adicionales = currentState.selectedAdicionales.map { adicional ->
                        println("Agregando adicional: ${adicional.nombre}, Precio ${adicional.precio}")
                        SeleccionAdicional(adicional.id, adicional.precio)
                    },
                    precioFinal = currentState.finalPrice,
                    fechaVenta = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
                )

                println("Enviando request de compra con precio final: ${buyRequest.precioFinal}")

                withContext(Dispatchers.Default) {  // Cambiado de IO a Default
                    buyService.processPurchase(buyRequest)
                        .onSuccess { response ->
                            println("Compra procesada exitosamente: ${response.message}")
                            withContext(Dispatchers.Main) {
                                _uiState.update { it.copy(
                                    isLoading = false,
                                    error = null
                                )}
                            }
                        }
                        .onFailure { exception ->
                            println("Error al procesar compra: ${exception.message}")
                            withContext(Dispatchers.Main) {
                                _uiState.update { it.copy(
                                    isLoading = false,
                                    error = "Error al procesar la compra: ${exception.message}"
                                )}
                            }
                        }
                }
            } catch (e: Exception) {
                println("Excepción durante el proceso de compra: ${e.message}")
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Error al procesar la compra: ${e.message}"
                )}
            }
        }
    }

    fun selectOption(personalizacionId: Int, opcion: OpcionModel) {
        println("Seleccionando opción: ${opcion.nombre} para personalización $personalizacionId")
        _uiState.update { currentState ->
            val newOptions = currentState.selectedOptions.toMutableMap().apply {
                put(personalizacionId, opcion)
            }

            val dispositivo = currentState.dispositivo ?: return@update currentState

            val basePrice = dispositivo.precioBase
            val optionsPrice = newOptions.values.sumOf { it.precioAdicional }
            val adicionalesPrice = currentState.selectedAdicionales.sumOf { adicional ->
                if (adicional.precioGratis != -1.0 && basePrice + optionsPrice > adicional.precioGratis) {
                    0.0
                } else {
                    adicional.precio
                }
            }

            println("Nuevo precio total: ${basePrice + optionsPrice + adicionalesPrice}")
            currentState.copy(
                selectedOptions = newOptions,
                finalPrice = basePrice + optionsPrice + adicionalesPrice
            )
        }
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

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }
}