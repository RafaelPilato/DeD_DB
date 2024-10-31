package com.example.ded2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.ded2.ui.theme.DeD2Theme
import org.example.controlePersonagem.personagem
import org.example.controlePersonagem.Racas.*


class Telas : ComponentActivity() {
    private var viewModel = PersonagemViewModel()
    private var Personagem = personagem("", humano())
    private var atributos = PersonagemController(Personagem)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeD2Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                }

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "EstudoRafa") {
                    composable("EstudoRafa") { WelcomeMessage(navController, Personagem, viewModel) }
                    composable("second_screen") { SecondScreen(atributos, navController) }
                    composable("verPersonagem") {ViewPersonagemScreen(Personagem)}

                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WelcomeMessage(navController: NavHostController, Personagem: personagem, viewModel: PersonagemViewModel) {
        // Variáveis do ViewModel
        var nomePersonagem by remember { mutableStateOf(Personagem.nome) }
        //var selectedRace by remember { mutableStateOf(Personagem.racaPersonagem) }

        // Menu suspenso
        var expanded by remember { mutableStateOf(false) }

        // Lista de opções para a lista
        val racas = listOf(
            "Alto Elfo", "Anão", "Anão da Colina", "Anão da montanha", "Draconato", "Drow",
            "Elfo", "Elfo da Floresta", "Gnomo", "Gnomo das Rochas", "Halfling", "Halfling Pés-Leves",
            "Halfling Robusto", "Humano", "Meio Helfo", "Meio Orc", "Tiefling"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Text(
                text = "Bem vindo a criação de personagem!",
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(70.dp))

            // TextField para inserir o nome do personagem
            TextField(
                value = nomePersonagem,
                onValueChange = {
                    nomePersonagem = it
                    Personagem.nome = it // Atualiza o nome do personagem
                },
                label = { Text("Insira o nome do personagem:") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Dropdown para a seleção de raça
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Raça") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    racas.forEach { raca ->
                        DropdownMenuItem(
                            text = { Text(raca) },
                            onClick = {
                                Personagem.racaPersonagem = viewModel.setClassRaca(raca)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Botão para navegar para a segunda tela, passando os valores como argumentos
            Button(onClick = {

                navController.navigate("second_screen")
            }) {
                Text("Próximo")
            }
        }
    }

    @Composable
    fun SecondScreen(personagemController: PersonagemController, navController: NavHostController) {
        var pontosRestantes by remember { mutableStateOf(personagemController.getPontosDisponiveis()) }
        var snackbarVisible by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(16.dp)){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Distribuição dos atributos", fontSize = 18.sp)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Pontos restantes:", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$pontosRestantes", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // colunas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Atributo", modifier = Modifier.weight(1f))
                Text(text = "Valor", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(8.dp))

            AtributoInputRow(
                "Força",
                personagemController,
                { pontosRestantes = personagemController.getPontosDisponiveis()},
                { showMessage(it, { snackbarMessage = it; snackbarVisible = true }) })
            Spacer(modifier = Modifier.height(8.dp))
            AtributoInputRow(
                "Destreza",
                personagemController,
                { pontosRestantes = personagemController.getPontosDisponiveis() },
                { showMessage(it, { snackbarMessage = it; snackbarVisible = true }) })
            Spacer(modifier = Modifier.height(8.dp))
            AtributoInputRow(
                "Constituição",
                personagemController,
                { pontosRestantes = personagemController.getPontosDisponiveis() },
                { showMessage(it, { snackbarMessage = it; snackbarVisible = true }) })
            Spacer(modifier = Modifier.height(8.dp))
            AtributoInputRow(
                "Sabedoria",
                personagemController,
                { pontosRestantes = personagemController.getPontosDisponiveis() },
                { showMessage(it, { snackbarMessage = it; snackbarVisible = true }) })
            Spacer(modifier = Modifier.height(8.dp))
            AtributoInputRow(
                "Inteligência",
                personagemController,
                { pontosRestantes = personagemController.getPontosDisponiveis() },
                { showMessage(it, { snackbarMessage = it; snackbarVisible = true }) })
            Spacer(modifier = Modifier.height(8.dp))
            AtributoInputRow(
                "Carisma",
                personagemController,
                { pontosRestantes = personagemController.getPontosDisponiveis() },
                { showMessage(it, { snackbarMessage = it; snackbarVisible = true }) })

            if (snackbarVisible) {
                Snackbar(
                    action = {
                        Button(onClick = { snackbarVisible = false }) {
                            Text("Fechar")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(snackbarMessage)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("EstudoRafa") }) {
                Text("Voltar")
            }

            Button(onClick = { navController.navigate("verPersonagem") }) {
                Text("Finalizar")
            }
        }
    }

    @Composable
    fun AtributoInputRow(label: String, personagemController: PersonagemController, updatePontos: () -> Unit, onError: (String) -> Unit) {
        var textValue by remember { mutableStateOf("") }

        var indiceAtributo : Int = 0

        when(label){
            "Força" -> indiceAtributo = 1
            "Destreza" -> indiceAtributo = 2
            "Constituição" -> indiceAtributo = 3
            "Sabedoria" -> indiceAtributo = 5
            "Inteligência" -> indiceAtributo = 4
            "Carisma" -> indiceAtributo = 6
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Alinhamento vertical centralizado
        ) {
            // Coluna de Atributos
            Text(text = label, modifier = Modifier.weight(1f))

            // Campo de entrada de número inteiro
            OutlinedTextField(
                value = textValue,
                onValueChange = { newValue ->
                    // Permitir apenas números
                    if (newValue.all { it.isDigit() }) {
                        textValue = newValue
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .weight(1f)
                    .width(50.dp)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused && textValue.isNotEmpty()) {
                            try {
                                val valor = textValue.toInt()
                                personagemController.setAtributo(valor, indiceAtributo)
                                updatePontos() // Atualiza os pontos
                            } catch (e: IllegalArgumentException) {
                                onError(e.message ?: "Erro desconhecido")
                                textValue = "" // Limpa o campo em caso de erro
                            } catch (e: NumberFormatException) {
                                onError("Por favor, insira um número válido.")
                                textValue = "" // Limpa o campo em caso de erro
                            }
                        }
                    }
            )
        }
    }

    fun showMessage(message: String, callback: (String) -> Unit) {
        callback(message)
    }

    @Composable
    fun ViewPersonagemScreen(
        Personagem: personagem,
        //navController: NavHostController
    ) {

        Personagem.valorBonus()
        Personagem.modificarVida()

        // Pegando os valores de atributos e status do personagem
        val nomePersonagem = remember { mutableStateOf(Personagem.nome) }
        val racaPersonagem = remember { mutableStateOf(Personagem.racaPersonagem) }
        val nivelPersonagem = remember { mutableStateOf(Personagem.nivel) }
        val xpPersonagem = remember { mutableStateOf(Personagem.xp) }
        val forcaPersonagem = remember { mutableStateOf(Personagem.forca) }
        val destrezaPersonagem = remember { mutableStateOf(Personagem.destreza) }
        val constituicaoPersonagem = remember { mutableStateOf(Personagem.constituicao) }
        val inteligenciaPersonagem = remember { mutableStateOf(Personagem.inteligencia) }
        val sabedoriaPersonagem = remember { mutableStateOf(Personagem.sabedoria) }
        val carismaPersonagem = remember { mutableStateOf(Personagem.carisma) }
        val vidaPersonagem = remember { mutableStateOf(Personagem.vida) }

        // Layout da tela com as informações do personagem
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Informações do Personagem",
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Exibindo as informações do personagem
            Text(text = "Nome: ${nomePersonagem.value}", fontSize = 18.sp)
            Text(text = "Raça: ${racaPersonagem.value}", fontSize = 18.sp)
            Text(text = "Nível: ${nivelPersonagem.value}", fontSize = 18.sp)
            Text(text = "XP: ${xpPersonagem.value}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))

            // Exibindo os atributos
            Text(text = "Atributos", fontSize = 20.sp)
            Text(text = "Força: ${forcaPersonagem.value}", fontSize = 18.sp)
            Text(text = "Destreza: ${destrezaPersonagem.value}", fontSize = 18.sp)
            Text(text = "Constituição: ${constituicaoPersonagem.value}", fontSize = 18.sp)
            Text(text = "Inteligência: ${inteligenciaPersonagem.value}", fontSize = 18.sp)
            Text(text = "Sabedoria: ${sabedoriaPersonagem.value}", fontSize = 18.sp)
            Text(text = "Carisma: ${carismaPersonagem.value}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))

            // Exibindo a vida
            Text(text = "Vida: ${vidaPersonagem.value}", fontSize = 18.sp)
        }
    }
}