package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.RecipientContact
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.CumaViewModel
import com.example.util.WhatsAppSender
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ContactsScreen(viewModel: CumaViewModel) {
    val context = LocalContext.current
    val recipients by viewModel.recipients.collectAsState()
    val selectedRecipients by viewModel.selectedRecipients.collectAsState()
    val currentFilterGroup by viewModel.recipientFilterGroup.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Permission launcher for contacts
    val contactPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.importPhoneContacts()
        }
    }

    val groups = listOf("Tümü", "Aile", "Akrabalar", "Arkadaşlar", "Rehber")

    val filteredList = remember(recipients, currentFilterGroup, searchQuery) {
        recipients.filter { contact ->
            val matchesGroup = currentFilterGroup == "Tümü" || contact.groupName.equals(currentFilterGroup, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.phoneNumber.contains(searchQuery)
            matchesGroup && matchesSearch
        }
    }

    val allSelected = recipients.isNotEmpty() && selectedRecipients.size == recipients.size

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Kişi Ekle") },
                modifier = Modifier.testTag("add_contact_fab")
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("contacts_screen")
        ) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Alıcı Kişiler",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${selectedRecipients.size} / ${recipients.size} kişi seçildi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Import from Phonebook button
                        OutlinedButton(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_CONTACTS
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    viewModel.importPhoneContacts()
                                } else {
                                    contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("import_contacts_button")
                        ) {
                            Icon(Icons.Default.ContactPhone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rehberden Al", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("İsim veya telefon numarası ara...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Temizle")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contacts_search_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Group Filter Chips & Select All Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(groups) { group ->
                                FilterChip(
                                    selected = currentFilterGroup == group,
                                    onClick = { viewModel.setRecipientFilterGroup(group) },
                                    label = { Text(group) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = { viewModel.selectAllRecipients(!allSelected) },
                            modifier = Modifier.testTag("select_all_button")
                        ) {
                            Text(if (allSelected) "Kaldır" else "Tümünü Seç", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Contacts List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PersonSearch,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Aramaya uygun kişi bulunamadı." else "Henüz kişi eklenmedi.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.id }) { contact ->
                        ContactRowItem(
                            contact = contact,
                            onToggleSelection = { viewModel.toggleRecipient(contact.id, it) },
                            onDelete = { viewModel.deleteRecipient(contact) },
                            onDirectSend = {
                                WhatsAppSender.launchDirectSend(
                                    context = context,
                                    phoneNumber = contact.phoneNumber,
                                    message = "Hayırlı Cumalar dilerim ${contact.name}!"
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, group ->
                viewModel.addRecipient(name, phone, group)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ContactRowItem(
    contact: RecipientContact,
    onToggleSelection: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onDirectSend: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onToggleSelection(!contact.isSelected) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (contact.isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = contact.isSelected,
                onCheckedChange = { onToggleSelection(it) },
                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Initial Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (contact.isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    color = if (contact.isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (contact.lastSentTimestamp != null) {
                    val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(Date(contact.lastSentTimestamp))
                    Text(
                        text = "Son gönderim: $dateStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            // Direct WhatsApp test button
            IconButton(
                onClick = onDirectSend,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "WhatsApp ile İlet",
                    tint = Color(0xFF25D366)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, group: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("Aile") }

    val groups = listOf("Aile", "Akrabalar", "Arkadaşlar", "İş", "Diğer")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Alıcı Kişi Ekle") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Kişi Adı / Lakap") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefon Numarası (+90...)") },
                    placeholder = { Text("05xxxxxxxxx") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_phone_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Grup Seçin:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(groups) { group ->
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = { selectedGroup = group },
                            label = { Text(group) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, phone, selectedGroup)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                modifier = Modifier.testTag("save_contact_button")
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
