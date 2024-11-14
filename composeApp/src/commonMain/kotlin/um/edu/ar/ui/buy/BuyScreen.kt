import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import um.edu.ar.ui.buy.BuyViewModel
import um.edu.ar.ui.dispositivos.AdicionalModel
import um.edu.ar.ui.dispositivos.CaracteristicaModel
import um.edu.ar.ui.dispositivos.OpcionModel
import um.edu.ar.ui.dispositivos.PersonalizacionModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.*


@Composable
fun BuyScreen(
    dispositivoId: Int,
    viewModel: BuyViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dispositivoId) {
        viewModel.loadDispositivo(dispositivoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        uiState.dispositivo?.let { dispositivo ->
            Text("Comprar ${dispositivo.nombre}", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Precio: $${dispositivo.precioBase}", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))

            // Características
            CharacteristicsList(dispositivo.caracteristicas)

            // Personalizaciones
            PersonalizacionesList(
                personalizaciones = dispositivo.personalizaciones,
                selectedOptions = uiState.selectedOptions,
                onOptionSelected = viewModel::selectOption
            )

            // Adicionales
            AdicionalesList(
                adicionales = dispositivo.adicionales,
                selectedAdicionales = uiState.selectedAdicionales,
                onAdicionalToggled = viewModel::toggleAdicional
            )

            Spacer(modifier = Modifier.height(32.dp))

            PriceAndConfirmation(
                finalPrice = uiState.finalPrice,
                onConfirmClick = { viewModel.processPurchase() },
                isLoading = uiState.isLoading
            )
        }
    }
}

@Composable
private fun CharacteristicsList(caracteristicas: List<CaracteristicaModel>) {
    Text("Características:", style = MaterialTheme.typography.h6)
    caracteristicas.forEach { caracteristica ->
        Text("• ${caracteristica.nombre}: ${caracteristica.descripcion}")
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun PersonalizacionesList(
    personalizaciones: List<PersonalizacionModel>,
    selectedOptions: Map<Int, OpcionModel>,
    onOptionSelected: (Int, OpcionModel) -> Unit
) {
    personalizaciones.forEach { personalizacion ->
        Text(personalizacion.nombre, style = MaterialTheme.typography.h6)
        personalizacion.opciones.forEach { opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOptions[personalizacion.id] == opcion,
                    onClick = { onOptionSelected(personalizacion.id, opcion) }
                )
                Text(opcion.nombre)
            }
        }
    }
}

@Composable
private fun AdicionalesList(
    adicionales: List<AdicionalModel>,
    selectedAdicionales: List<AdicionalModel>,
    onAdicionalToggled: (AdicionalModel) -> Unit
) {
    Text("Adicionales:", style = MaterialTheme.typography.h6)
    adicionales.forEach { adicional ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Checkbox(
                checked = selectedAdicionales.contains(adicional),
                onCheckedChange = { onAdicionalToggled(adicional) }
            )
            Text(adicional.nombre)
        }
    }
}

@Composable
private fun PriceAndConfirmation(
    finalPrice: Double,
    onConfirmClick: () -> Unit,
    isLoading: Boolean
) {
    Text("Precio Final: $${finalPrice}", style = MaterialTheme.typography.h6)
    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onConfirmClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
        } else {
            Text("Confirmar Compra")
        }
    }
}