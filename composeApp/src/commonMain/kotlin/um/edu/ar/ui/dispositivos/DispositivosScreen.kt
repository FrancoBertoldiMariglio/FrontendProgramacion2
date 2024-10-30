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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import um.edu.ar.ui.dispositivos.DispositivoModel

@Composable
fun DispositivosScreen(viewModel: DispositivosViewModel, navController: NavController) {
    val dispositivos by viewModel.dispositivos.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(600.dp)
                .padding(3.dp)
        ) {
            LazyColumn {
                items(dispositivos) { dispositivo ->
                    DispositivoCard(dispositivo, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DispositivoCard(dispositivo: DispositivoModel, navController: NavController) {
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
                text = "Price: ${dispositivo.precioBase} ${dispositivo.moneda}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF87CEEB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Characteristics:",
                fontWeight = FontWeight.Bold
            )
            dispositivo.caracteristicas.forEach { caracteristica ->
                Text("• ${caracteristica.nombre}: ${caracteristica.descripcion}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            BuyButton(dispositivo, navController)
        }
    }
}

@Composable
fun BuyButton(dispositivo: DispositivoModel, navController: NavController) {
    val dispositivoJson = Json.encodeToString(dispositivo)
    Button(
        onClick = { navController.navigate("buy/$dispositivoJson") },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF87CEEB),
            disabledBackgroundColor = Color(0xFFB0B0B0),
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "Buy")
    }
}
