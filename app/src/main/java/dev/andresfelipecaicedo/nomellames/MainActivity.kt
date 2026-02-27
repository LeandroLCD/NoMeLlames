package dev.andresfelipecaicedo.nomellames

import android.Manifest
import android.app.role.RoleManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.andresfelipecaicedo.nomellames.data.AppDatabase
import dev.andresfelipecaicedo.nomellames.data.BlockedCallDao
import dev.andresfelipecaicedo.nomellames.data.PrefixRepository
import dev.andresfelipecaicedo.nomellames.ui.theme.NoMeLlamesTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var isCallScreeningEnabled by mutableStateOf(false)
    private var permissionsGranted by mutableStateOf(false)

    private lateinit var prefixRepository: PrefixRepository
    private lateinit var blockedCallDao: BlockedCallDao

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.values.all { it }
        if (permissionsGranted) {
            requestCallScreeningRole()
        }
    }

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkCallScreeningRole()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefixRepository = PrefixRepository.getInstance(applicationContext)
        blockedCallDao = AppDatabase.getDatabase(applicationContext).blockedCallDao()

        checkPermissionsAndRole()

        setContent {
            NoMeLlamesTheme {
                MainApp(
                    isEnabled = isCallScreeningEnabled,
                    permissionsGranted = permissionsGranted,
                    prefixRepository = prefixRepository,
                    blockedCallDao = blockedCallDao,
                    onRequestPermissions = { requestPermissions() },
                    onRequestRole = { requestCallScreeningRole() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndRole()
    }

    private fun checkPermissionsAndRole() {
        permissionsGranted = checkSelfPermission(Manifest.permission.READ_PHONE_STATE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_CALL_LOG) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        checkCallScreeningRole()
    }

    private fun checkCallScreeningRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        isCallScreeningEnabled = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }

    private fun requestPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
            )
        )
    }

    private fun requestCallScreeningRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            roleRequestLauncher.launch(intent)
        }
    }
}

@Composable
fun MainApp(
    isEnabled: Boolean,
    permissionsGranted: Boolean,
    prefixRepository: PrefixRepository,
    blockedCallDao: BlockedCallDao,
    onRequestPermissions: () -> Unit,
    onRequestRole: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inicio", "Prefijos", "Historial", "Ajustes")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title) },
                        icon = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeScreen(
                isEnabled = isEnabled,
                permissionsGranted = permissionsGranted,
                prefixRepository = prefixRepository,
                onRequestPermissions = onRequestPermissions,
                onRequestRole = onRequestRole,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> PrefixScreen(
                prefixRepository = prefixRepository,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> HistoryScreen(
                blockedCallDao = blockedCallDao,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> SettingsScreen(
                prefixRepository = prefixRepository,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun HomeScreen(
    isEnabled: Boolean,
    permissionsGranted: Boolean,
    prefixRepository: PrefixRepository,
    onRequestPermissions: () -> Unit,
    onRequestRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefixes by prefixRepository.prefixes.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NoMeLlames",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (prefixes.isEmpty()) {
            Text(
                text = "No hay prefijos configurados",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ve a la pestaña \"Prefijos\" para agregar los prefijos o números que deseas bloquear.\n\nNo incluyas el código de país.\nEjemplo: escribe 320, no +57320.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "Bloquea llamadas de ${prefixes.size} prefijos configurados",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!permissionsGranted) {
            Text(
                text = "Se necesitan permisos para funcionar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequestPermissions) {
                Text("Otorgar Permisos")
            }
        } else if (!isEnabled) {
            Text(
                text = "Configura esta app como filtro de llamadas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequestRole) {
                Text("Configurar como Filtro")
            }
        } else {
            Text(
                text = "Activo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "El bloqueo de llamadas spam esta funcionando",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun PrefixScreen(
    prefixRepository: PrefixRepository,
    modifier: Modifier = Modifier
) {
    val prefixes by prefixRepository.prefixes.collectAsState()
    var newPrefix by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Prefijos Bloqueados",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Las llamadas de números que empiecen con estos prefijos serán bloqueadas. No incluyas el código de país (ej: escribe 320, no +57320).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newPrefix,
                onValueChange = { newPrefix = it.filter { c -> c.isDigit() } },
                label = { Text("Nuevo prefijo") },
                placeholder = { Text("Ej: 320") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (newPrefix.isNotBlank()) {
                        prefixRepository.addPrefix(newPrefix)
                        newPrefix = ""
                    }
                },
                enabled = newPrefix.isNotBlank()
            ) {
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (prefixes.isEmpty()) {
            Text(
                text = "No hay prefijos configurados",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(prefixes.toList().sorted()) { prefix ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = prefix, style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { prefixRepository.removePrefix(prefix) }) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    blockedCallDao: BlockedCallDao,
    modifier: Modifier = Modifier
) {
    val blockedCalls by blockedCallDao.getAllBlockedCalls().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de Bloqueos",
                style = MaterialTheme.typography.headlineMedium
            )

            if (blockedCalls.isNotEmpty()) {
                TextButton(onClick = { scope.launch { blockedCallDao.deleteAllBlockedCalls() } }) {
                    Text("Limpiar", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (blockedCalls.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay llamadas bloqueadas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(blockedCalls) { call ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = call.phoneNumber, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Prefijo: ${call.matchedPrefix}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = dateFormat.format(Date(call.timestamp)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    prefixRepository: PrefixRepository,
    modifier: Modifier = Modifier
) {
    val skipCallLog by prefixRepository.skipCallLog.collectAsState()
    val skipNotification by prefixRepository.skipNotification.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ocultar del historial de llamadas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Las llamadas bloqueadas no apareceran en el registro de llamadas del telefono",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = skipCallLog,
                    onCheckedChange = { prefixRepository.setSkipCallLog(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ocultar notificaciones",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "No mostrar notificacion de llamada perdida para llamadas bloqueadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = skipNotification,
                    onCheckedChange = { prefixRepository.setSkipNotification(it) }
                )
            }
        }
    }
}
