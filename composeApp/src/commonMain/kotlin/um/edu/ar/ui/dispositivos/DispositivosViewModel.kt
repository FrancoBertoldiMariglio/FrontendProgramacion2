import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import um.edu.ar.ui.dispositivos.Dispositivo

class DispositivosViewModel : ViewModel() {

    private val _dispositivos = MutableStateFlow<List<Dispositivo>>(emptyList())
    val dispositivos: StateFlow<List<Dispositivo>> = _dispositivos

    init {
        loadDispositivos()
    }

    private fun loadDispositivos() {
        _dispositivos.value = Json.decodeFromString(example)
    }
}

val example = """
[
  {
    "id": 1,
    "codigo": "NTB01",
    "nombre": "Notebook 1",
    "descripcion": "Notebook básica con pantalla de 14 pulgadas",
    "precioBase": 1500.00,
    "moneda": "USD",
    "caracteristicas": [
      {
        "id": 1,
        "nombre": "Pantalla",
        "descripcion": "Pantalla LCD 14\""
      },
      {
        "id": 2,
        "nombre": "Procesador",
        "descripcion": "Intel i5 de 10ma generación"
      }
    ],
    "personalizaciones": [
      {
        "id": 1,
        "nombre": "CPU",
        "descripcion": "Opciones de Procesador",
        "opciones": [
          {
            "id": 1,
            "codigo": "PROC01",
            "nombre": "Intel i5",
            "descripcion": "Procesador 1.6 GHz - 6 Cores",
            "precioAdicional": 0.00
          },
          {
            "id": 2,
            "codigo": "PROC02",
            "nombre": "Intel i7",
            "descripcion": "Procesador 2.0 GHz - 8 Cores",
            "precioAdicional": 200.00
          }
        ]
      },
      {
        "id": 2,
        "nombre": "Memoria RAM",
        "descripcion": "Opciones de Memoria",
        "opciones": [
          {
            "id": 1,
            "codigo": "RAM01",
            "nombre": "8GB DDR4",
            "descripcion": "8GB RAM DDR4",
            "precioAdicional": 0.00
          },
          {
            "id": 2,
            "codigo": "RAM02",
            "nombre": "16GB DDR4",
            "descripcion": "16GB RAM DDR4",
            "precioAdicional": 100.00
          }
        ]
      }
    ],
    "adicionales": [
      {
        "id": 1,
        "nombre": "Mouse",
        "descripcion": "Mouse Bluetooth 3 botones",
        "precio": 30.50,
        "precioGratis": 1500.00
      }
    ]
  },
  {
    "id": 2,
    "codigo": "NTB02",
    "nombre": "Notebook 2",
    "descripcion": "Notebook avanzada con pantalla OLED 16 pulgadas",
    "precioBase": 2250.00,
    "moneda": "USD",
    "caracteristicas": [
      {
        "id": 3,
        "nombre": "Pantalla",
        "descripcion": "Pantalla OLED 16\""
      },
      {
        "id": 4,
        "nombre": "Cámara",
        "descripcion": "Cámara web 1080p"
      },
      {
        "id": 5,
        "nombre": "Batería",
        "descripcion": "Batería de 80Wh"
      }
    ],
    "personalizaciones": [
      {
        "id": 3,
        "nombre": "CPU",
        "descripcion": "Opciones de Procesador",
        "opciones": [
          {
            "id": 3,
            "codigo": "PROC03",
            "nombre": "Intel Core Y1",
            "descripcion": "Procesador 1.2 GHz - 10 Cores",
            "precioAdicional": 0.00
          },
          {
            "id": 4,
            "codigo": "PROC04",
            "nombre": "Intel Core Y2",
            "descripcion": "Procesador 1.7 GHz - 24 Cores",
            "precioAdicional": 700.00
          }
        ]
      },
      {
        "id": 4,
        "nombre": "Memoria RAM",
        "descripcion": "Opciones de Memoria",
        "opciones": [
          {
            "id": 5,
            "codigo": "RAM03",
            "nombre": "16GB DDR4",
            "descripcion": "16GB RAM DDR4",
            "precioAdicional": 0.00
          },
          {
            "id": 6,
            "codigo": "RAM04",
            "nombre": "32GB DDR4",
            "descripcion": "32GB RAM DDR4",
            "precioAdicional": 400.00
          }
        ]
      }
    ],
    "adicionales": [
      {
        "id": 2,
        "nombre": "Funda",
        "descripcion": "Funda protectora de neopreno",
        "precio": 50.00,
        "precioGratis": 2000.00
      }
    ]
  }
]
"""