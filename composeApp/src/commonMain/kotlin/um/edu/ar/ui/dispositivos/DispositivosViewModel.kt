import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import um.edu.ar.ui.dispositivos.DispositivoModel
import um.edu.ar.ui.dispositivos.DispositivosService
import um.edu.ar.utils.createPlatformHttpClient

class DispositivosViewModel() : ViewModel() {

    private val client = createPlatformHttpClient()
    private val dispositivosService: DispositivosService = DispositivosService(client)

    private val _dispositivos = MutableStateFlow<List<DispositivoModel>>(emptyList())
    val dispositivos: StateFlow<List<DispositivoModel>> = _dispositivos

    init {
        loadDispositivos()
    }

    private fun loadDispositivos() {
        viewModelScope.launch {
            try {
                val response = dispositivosService.getDispositivos()
                if (response.success && response.dispositivos != null) {
                    _dispositivos.value = response.dispositivos
                } else {
                    _dispositivos.value = emptyList()
                }
            } catch (e: Exception) {
                _dispositivos.value = emptyList()
            }
        }
    }
}