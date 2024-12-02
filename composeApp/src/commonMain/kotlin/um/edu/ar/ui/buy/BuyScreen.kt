package um.edu.ar.ui.buy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings

@Composable
fun BuyScreen(
    viewModel: BuyViewModel,
    dispositivoId: Int,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings: Settings = Settings()

    val userId = settings.getInt("userId", 0)
    val username = settings.getString("username", "")

    LaunchedEffect(dispositivoId) {
        viewModel.loadDispositivo(dispositivoId)
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        uiState.dispositivo == null -> Text("No hay dispositivo seleccionado")
        else -> BuyContent(
            uiState = uiState,
            onOptionSelect = viewModel::updateSelection,
            onAdicionalToggle = viewModel::toggleAdicional,
            onPurchase = { viewModel.processPurchase(userId, username) }
        )
    }
}

@Composable
private fun BuyContent(
    uiState: BuyModel,
    onOptionSelect: (Int, Int, Double) -> Unit,
    onAdicionalToggle: (Int, Double) -> Unit,
    onPurchase: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = uiState.dispositivo?.nombre ?: "",
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(uiState.dispositivo?.descripcion ?: "")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Precio base: $${uiState.dispositivo?.precioBase ?: "0.00"}",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
            }

            // Personalizaciones
            uiState.dispositivo?.personalizaciones?.forEach { personalizacion ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = personalizacion.nombre,
                                style = MaterialTheme.typography.subtitle1
                            )
                            Spacer(Modifier.height(8.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                personalizacion.opciones.chunked(2).forEach { rowOptions ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        rowOptions.forEach { opcion ->
                                            val isSelected = uiState.selectedOptions[personalizacion.id]?.first == opcion.id
                                            OutlinedButton(
                                                onClick = {
                                                    onOptionSelect(
                                                        personalizacion.id,
                                                        opcion.id,
                                                        opcion.precioAdicional ?: 0.0
                                                    )
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    backgroundColor = if (isSelected) {
                                                        MaterialTheme.colors.primary
                                                    } else {
                                                        MaterialTheme.colors.surface
                                                    },
                                                    contentColor = if (isSelected) {
                                                        MaterialTheme.colors.onPrimary
                                                    } else {
                                                        MaterialTheme.colors.onSurface
                                                    }
                                                )
                                            ) {
                                                Text("${opcion.nombre} ${opcion.precioAdicional?.let { "(+$$it)" } ?: ""}")
                                            }
                                        }
                                        if (rowOptions.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Adicionales
            if (uiState.dispositivo?.adicionales?.isNotEmpty() == true) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Adicionales",
                                style = MaterialTheme.typography.subtitle1
                            )
                            Spacer(Modifier.height(8.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.dispositivo.adicionales.chunked(2).forEach { rowAdicionales ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        rowAdicionales.forEach { adicional ->
                                            val isSelected = adicional.id in uiState.selectedAdicionales
                                            OutlinedButton(
                                                onClick = {
                                                    onAdicionalToggle(
                                                        adicional.id,
                                                        adicional.precio ?: 0.0
                                                    )
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    backgroundColor = if (isSelected) {
                                                        MaterialTheme.colors.primary
                                                    } else {
                                                        MaterialTheme.colors.surface
                                                    },
                                                    contentColor = if (isSelected) {
                                                        MaterialTheme.colors.onPrimary
                                                    } else {
                                                        MaterialTheme.colors.onSurface
                                                    }
                                                )
                                            ) {
                                                Text("${adicional.nombre} ${adicional.precio?.let { "(+$$it)" } ?: ""}")
                                            }
                                        }
                                        if (rowAdicionales.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Precio Final y Botón de Compra
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Precio Final:",
                                style = MaterialTheme.typography.h6
                            )
                            Text(
                                "$${uiState.precioFinal}",
                                style = MaterialTheme.typography.h6
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onPurchase,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colors.onPrimary
                                )
                            } else {
                                Text("Confirmar Compra")
                            }
                        }
                    }
                }
            }
        }
    }
}