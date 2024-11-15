import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import um.edu.ar.ui.dispositivos.DispositivoModel


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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            uiState.isLoading && uiState.dispositivo == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.error != null && uiState.dispositivo == null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            uiState.dispositivo != null -> {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    uiState.dispositivo?.let { dispositivo ->
                        Text("Comprar ${dispositivo.nombre}", style = MaterialTheme.typography.h5)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Precio: $${dispositivo.precioBase}", style = MaterialTheme.typography.h6)
                        Spacer(modifier = Modifier.height(16.dp))

                        CharacteristicsList(dispositivo.caracteristicas)

                        PersonalizacionesList(
                            personalizaciones = dispositivo.personalizaciones,
                            selectedOptions = uiState.selectedOptions,
                            onOptionSelected = { persId, opcionId, precio ->
                                viewModel.updateSelection(persId, opcionId, precio)
                            }
                        )

                        AdicionalesList(
                            adicionales = dispositivo.adicionales,
                            selectedAdicionales = uiState.selectedAdicionales,
                            onAdicionalToggled = { adicionalId, precio ->
                                viewModel.toggleAdicional(adicionalId, precio)
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (uiState.isLoading) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        PriceAndConfirmation(
                            precioFinal = uiState.finalPrice,
                            onConfirmClick = { viewModel.processPurchase() },
                            isLoading = currentState.isLoading
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "No se pudo cargar el dispositivo",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CharacteristicsList(caracteristicas: List<CaracteristicaModel>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "Características:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        caracteristicas.forEach { caracteristica ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    "• ${caracteristica.nombre}:",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.width(120.dp)
                )
                Text(
                    caracteristica.descripcion,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun PersonalizacionesList(
    personalizaciones: List<PersonalizacionModel>,
    selectedOptions: Map<Int, Pair<Int, Double>>,
    onOptionSelected: (Int, Int, Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "Personalizaciones:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        personalizaciones.forEach { personalizacion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        personalizacion.nombre,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        personalizacion.descripcion,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    personalizacion.opciones.forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionSelected(
                                        personalizacion.id,
                                        opcion.id,
                                        opcion.precioAdicional
                                    )
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOptions[personalizacion.id]?.first == opcion.id,
                                onClick = {
                                    onOptionSelected(
                                        personalizacion.id,
                                        opcion.id,
                                        opcion.precioAdicional
                                    )
                                }
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    opcion.nombre,
                                    style = MaterialTheme.typography.subtitle1
                                )
                                if (opcion.precioAdicional > 0) {
                                    Text(
                                        "+$${opcion.precioAdicional}",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun AdicionalesList(
    adicionales: List<AdicionalModel>,
    selectedAdicionales: Map<Int, Double>,
    onAdicionalToggled: (Int, Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "Adicionales:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                adicionales.forEach { adicional ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAdicionalToggled(adicional.id, adicional.precio)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedAdicionales.containsKey(adicional.id),
                            onCheckedChange = {
                                onAdicionalToggled(adicional.id, adicional.precio)
                            }
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                adicional.nombre,
                                style = MaterialTheme.typography.subtitle1
                            )
                            Text(
                                adicional.descripcion,
                                style = MaterialTheme.typography.caption
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Precio: $${adicional.precio}",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.secondary
                                )
                                if (adicional.precioGratis != -1.0) {
                                    Text(
                                        "(Gratis si el total supera $${adicional.precioGratis})",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                    if (adicional != adicionales.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceAndConfirmation(
    precioFinal: Double,
    onConfirmClick: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Precio Final",
                style = MaterialTheme.typography.h6
            )
            Text(
                "$${(precioFinal * 100).toInt() / 100.0}",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = onConfirmClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Confirmar Compra")
                }
            }
        }
    }
}