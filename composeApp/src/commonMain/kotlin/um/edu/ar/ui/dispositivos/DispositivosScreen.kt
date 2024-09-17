import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import um.edu.ar.ui.dispositivos.Dispositivo

@Composable
fun DispositivosScreen(viewModel: DispositivosViewModel, navController: NavController) {
    val dispositivos by viewModel.dispositivos.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(dispositivos) { dispositivo ->
                DispositivoCard(dispositivo)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DispositivoCard(dispositivo: Dispositivo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = dispositivo.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = dispositivo.descripcion)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Precio: ${dispositivo.precioBase} ${dispositivo.moneda}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF87CEEB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Características:",
                fontWeight = FontWeight.Bold
            )
            dispositivo.caracteristicas.forEach { caracteristica ->
                Text("• ${caracteristica.nombre}: ${caracteristica.descripcion}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: Implement device selection */ },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF87CEEB))
            ) {
                Text("Ver detalles", color = Color.White)
            }
        }
    }
}