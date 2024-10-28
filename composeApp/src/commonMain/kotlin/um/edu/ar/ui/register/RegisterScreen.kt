package um.edu.ar.ui.register

import RegisterViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import proyectoprogramacion2.composeapp.generated.resources.Res
import proyectoprogramacion2.composeapp.generated.resources.logo

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, navController: NavController) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .padding(3.dp)
        ) {
            Register(
                modifier = Modifier.padding(2.dp),
                viewModel = viewModel,
                navController = navController
            )
        }

        if (errorMessage != null) {
            Snackbar(
                action = {
                    TextButton(onClick = { viewModel.clearErrorMessage() }) {
                        Text("Cerrar")
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) { Text(text = errorMessage ?: "") }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}


@Composable
fun Register(modifier: Modifier, viewModel: RegisterViewModel, navController: NavController) {

    val login: String by viewModel.login.collectAsState(initial = "")
    val email: String by viewModel.email.collectAsState(initial = "")
    val password: String by viewModel.password.collectAsState(initial = "")
    val registerEnable: Boolean by viewModel.registerEnable.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        LoginField(login) { viewModel.onRegisterChanged(it, email, password) }
        Spacer(modifier = Modifier.padding(4.dp))
        EmailField(email) { viewModel.onRegisterChanged(login, it, password) }
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordField(password) { viewModel.onRegisterChanged(login, email, it) }
        Spacer(modifier = Modifier.padding(4.dp))
        LogInButton(Modifier.align(Alignment.End), navController)
        Spacer(modifier = Modifier.padding(8.dp))
        RegisterButton(registerEnable) {
            coroutineScope.launch {
                viewModel.onRegisterSelected(navController)
            }
        }
    }
}


@Composable
fun RegisterButton(registerEnable: Boolean, onRegisterSelected: () -> Unit) {
    Button(
        onClick = { onRegisterSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF87CEEB),
            disabledBackgroundColor = Color(0xFFB0B0B0),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ), enabled = registerEnable
    ) {
        Text(text = "Register")
    }
}

@Composable
fun LoginField(login: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = login, onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "Login") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF636262),
            backgroundColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = email, onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "Email") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF636262),
            backgroundColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = password, onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "Password") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF636262),
            backgroundColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(Res.drawable.logo),
        contentDescription = "Header",
        modifier = modifier
    )
}

@Composable
fun LogInButton(modifier: Modifier, navController: NavController) {
    Text(
        text = "Already have an account?",
        modifier = modifier.clickable { navController.navigate("login") },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF87CEEB)
    )
}