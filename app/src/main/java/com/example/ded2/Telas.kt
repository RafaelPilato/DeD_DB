package com.example.ded2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.ded2.Data.DeD_DB
import com.example.ded2.Data.PersonagemDAO
import com.example.ded2.ui.theme.DeD2Theme
import kotlinx.coroutines.launch
import org.example.controlePersonagem.personagem
import org.example.controlePersonagem.Racas.*



class Telas : ComponentActivity() {
    private var viewModel = PersonagemViewModel()
    private var Personagem = personagem("", humano())
    private var atributos = PersonagemController(Personagem)
    private var personagemBD = PersonagemBD()

    private lateinit var personagemDao : PersonagemDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DeD_DB.getDatabase(this)
        personagemDao = db.personagemDao()


        enableEdgeToEdge()
        setContent {
            DeD2Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                }

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "EstudoRafa") {
                    composable("EstudoRafa") { WelcomeMessage(navController, Personagem, viewModel, ::getPersonagemBD, personagensList.value, ::deletePersonagemBD) }
                    composable("second_screen") { SecondScreen(atributos, navController) }
                    composable("verPersonagem") {ViewPersonagemScreen(Personagem, personagemBD, ::savePersonagemDB, ::getPersonagemBD, ::updatePersonagemBD, personagensList.value)}

                }
            }
        }
    }

    private fun savePersonagemDB() {
        lifecycleScope.launch {
            personagemDao.insert(personagemBD)
        }
    }

    private var personagensList = mutableStateOf(listOf<PersonagemBD>())

    private fun getPersonagemBD() {
        lifecycleScope.launch {
            personagensList.value = personagemDao.getAllPersonagens()
        }
    }

    private fun deletePersonagemBD(personagem: PersonagemBD) {
        lifecycleScope.launch {
            personagemDao.delete(personagem)
            getPersonagemBD() // Atualiza a lista de personagens após a deleção
        }
    }

    private fun updatePersonagemBD(id: Int, novoNome: String) {
        lifecycleScope.launch {
            val personagem = personagemDao.getPersonagemById(id)
            personagem?.let {
                it.nome = novoNome
                personagemDao.updateNome(it.id, it.nome)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeMessage(
    navController: NavHostController,
    Personagem: personagem,
    viewModel: PersonagemViewModel,
    getClick: () -> Unit,
    personagensList: List<PersonagemBD>,
    onDeleteClick: (PersonagemBD) -> Unit
) {

    // Variáveis do ViewModel
    var nomePersonagem by remember { mutableStateOf(Personagem.nome) }

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

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = getClick, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Listar Personagens")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exibição da lista de personagens
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(personagensList) { personagem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "ID: ${personagem.id}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Nome: ${personagem.nome}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Raça: ${personagem.raca}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Nível: ${personagem.nivel}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "XP: ${personagem.xp}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Força: ${personagem.forca}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Destreza: ${personagem.destreza}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Constituição: ${personagem.constituicao}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Inteligência: ${personagem.inteligencia}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Sabedoria: ${personagem.sabedoria}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Carisma: ${personagem.carisma}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Vida: ${personagem.vida}", fontSize = 18.sp, color = Color.Black)

                    // Botão para deletar o personagem
                    Button(
                        onClick = { onDeleteClick(personagem) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Text("Deletar", color = Color.White)
                    }

                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
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
    personamgeDB: PersonagemBD,
    saveClick: () -> Unit,
    getClick: () -> Unit,
    updateClick: (Int, String) -> Unit,  // Função de update
    personagensList: List<PersonagemBD>
) {
    var editandoNome by remember { mutableStateOf(false) }
    var novoNome by remember { mutableStateOf(Personagem.nome) }

    Personagem.valorBonus()
    Personagem.modificarVida()

    personamgeDB.apply {
        nome = Personagem.nome
        raca = Personagem.racaPersonagem.toString()
        nivel = Personagem.nivel
        xp = Personagem.xp
        forca = Personagem.forca
        destreza = Personagem.destreza
        constituicao = Personagem.constituicao
        inteligencia = Personagem.inteligencia
        sabedoria = Personagem.sabedoria
        carisma = Personagem.carisma
        vida = Personagem.vida
    }

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

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = saveClick, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Salvar Personagem")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Exibição ou edição do nome
        if (editandoNome) {
            OutlinedTextField(
                value = novoNome,
                onValueChange = { novoNome = it },
                label = { Text("Nome do Personagem") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                updateClick(personamgeDB.id, novoNome)  // Chama a função de update
                editandoNome = false
            }) {
                Text("Salvar Nome")
            }
        } else {
            Text(text = "Nome: ${novoNome}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { editandoNome = true }) {
                Text("Editar Nome")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = getClick, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Listar Personagens")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exibição da lista de personagens
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(personagensList) { personagem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "ID: ${personagem.id}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Nome: ${personagem.nome}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Raça: ${personagem.raca}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Nível: ${personagem.nivel}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "XP: ${personagem.xp}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Força: ${personagem.forca}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Destreza: ${personagem.destreza}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Constituição: ${personagem.constituicao}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Inteligência: ${personagem.inteligencia}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Sabedoria: ${personagem.sabedoria}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Carisma: ${personagem.carisma}", fontSize = 18.sp, color = Color.Black)
                    Text(text = "Vida: ${personagem.vida}", fontSize = 18.sp, color = Color.Black)
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}