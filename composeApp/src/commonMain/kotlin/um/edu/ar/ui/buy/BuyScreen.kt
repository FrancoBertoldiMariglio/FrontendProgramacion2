package um.edu.ar.ui.buy

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import um.edu.ar.ui.dispositivos.Adicional
import um.edu.ar.ui.dispositivos.Dispositivo
import um.edu.ar.ui.dispositivos.Opcion

@Composable
fun BuyScreen(dispositivo: Dispositivo, viewModel: BuyViewModel, navController: NavController) {
    val selectedOptions = remember { mutableStateMapOf<Int, Opcion>() }
    val selectedAdicionales = remember { mutableStateListOf<Adicional>() }
    val finalPrice by viewModel.finalPrice.collectAsState(initial = 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Comprar ${dispositivo.nombre}", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Precio: $${dispositivo.precioBase}", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Características:", style = MaterialTheme.typography.h6)
        dispositivo.caracteristicas.forEach { caracteristica ->
            Text("• ${caracteristica.nombre}: ${caracteristica.descripcion}")
        }
        Spacer(modifier = Modifier.height(16.dp))

        dispositivo.personalizaciones.forEach { personalizacion ->
            Text(personalizacion.nombre, style = MaterialTheme.typography.h6)
            personalizacion.opciones.forEach { opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedOptions[personalizacion.id] == opcion,
                        onClick = {
                            selectedOptions[personalizacion.id] = opcion
                            viewModel.selectOption(personalizacion.id, opcion)
                        }
                    )
                    Text(opcion.nombre)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Adicionales:", style = MaterialTheme.typography.h6)
        dispositivo.adicionales.forEach { adicional ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = selectedAdicionales.contains(adicional),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedAdicionales.add(adicional)
                        } else {
                            selectedAdicionales.remove(adicional)
                        }
                        viewModel.toggleAdicional(adicional)
                    }
                )
                Text(adicional.nombre)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text("Precio Final: $${finalPrice}", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val buyRequest = viewModel.createBuyRequest()
            // Implementar lógica para manejar el objeto buyRequest
        }) {
            Text("Confirmar Compra")
        }
    }
}