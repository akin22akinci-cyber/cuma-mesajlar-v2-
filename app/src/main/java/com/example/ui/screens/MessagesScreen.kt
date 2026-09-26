package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.FridayMessage
import com.example.data.sample.DefaultData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.CumaViewModel
import com.example.util.WhatsAppSender

@Composable
fun MessagesScreen(viewModel: CumaViewModel) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val currentCategory by viewModel.selectedCategory.collectAsState()

    val currentSchedule = schedule ?: DefaultData.defaultSchedule
    var showAddMessageDialog by remember { mutableStateOf(false) }
    var sharingMessageText by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Tümü", "Ayet & Hadis", "Dualar & Bereket", "Samimi & Kısa", "Akraba & Büyükler", "Peygamberimizin Duaları", "Özel")

    val filteredMessages = remember(messages, currentCategory) {
        if (currentCategory == "Tümü") messages
        else messages.filter { it.category == currentCategory }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMessageDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddComment, contentDescription = null) },
                text = { Text("Yeni Mesaj") },
                modifier = Modifier.testTag("add_message_fab")
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("messages_screen")
        ) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cuma Mesajları Arşivi",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ayetli, dualı ve samimi Cuma tebriklerini seçin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FilledTonalButton(
                            onClick = { viewModel.selectRandomMessage() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rastgele Seç", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = currentCategory == category,
                                onClick = { viewModel.setSelectedCategory(category) },
                                label = { Text(category) }
                            )
                        }
                    }
                }
            }

            // Message Cards List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMessages, key = { it.id }) { message ->
                    val isSelectedForFriday = currentSchedule.selectedMessageId == message.id

                    FridayMessageCard(
                        message = message,
                        isSelected = isSelectedForFriday,
                        onSelect = { viewModel.updateSelectedMessage(message.id) },
                        onToggleFavorite = { viewModel.toggleMessageFavorite(message.id, !message.isFavorite) },
                        onDelete = { viewModel.deleteMessage(message) },
                        onShare = {
                            sharingMessageText = message.content
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (showAddMessageDialog) {
        AddMessageDialog(
            onDismiss = { showAddMessageDialog = false },
            onAdd = { title, content, cat ->
                viewModel.addMessage(title, content, cat)
                showAddMessageDialog = false
            }
        )
    }

    sharingMessageText?.let { msg ->
        SocialShareSheet(
            message = msg,
            imageUri = null,
            onDismiss = { sharingMessageText = null }
        )
    }
}

@Composable
fun FridayMessageCard(
    message: FridayMessage,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("message_card_${message.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary), width = 2.dp)
        else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(message.category, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Seçili Mesaj", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = EmeraldPrimary
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (message.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favori",
                            tint = if (message.isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (message.isCustom) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Sil",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isSelected) {
                    OutlinedButton(
                        onClick = onSelect,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bu Mesajı Seç")
                    }
                } else {
                    Text(
                        text = "Cuma günü bu mesaj gönderilecek",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                FilledTonalButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF25D366)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp Paylaş")
                }
            }
        }
    }
}

@Composable
fun AddMessageDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Özel") }

    val categories = listOf("Özel", "Ayet & Hadis", "Dualar & Bereket", "Samimi & Kısa", "Akraba & Büyükler")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Cuma Mesajı Ekle") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık (Örn. Cuma Duası)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Cuma Mesajınız") },
                    placeholder = { Text("Mesajınızı yazın. İsim eklemek için {isim} yazabilirsiniz.") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Kategori:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onAdd(title, content, selectedCategory)
                    }
                },
                enabled = content.isNotBlank()
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
