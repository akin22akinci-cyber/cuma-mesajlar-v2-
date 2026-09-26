package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.sample.DefaultData
import com.example.ui.theme.EmeraldDarkBackground
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.CumaViewModel
import com.example.util.AlarmScheduler
import com.example.util.WhatsAppSender

@Composable
fun HomeScreen(
    viewModel: CumaViewModel,
    onNavigateToContacts: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToCardCreator: () -> Unit,
    onNavigateToSchedule: () -> Unit
) {
    val context = LocalContext.current
    val schedule by viewModel.schedule.collectAsState()
    val recipients by viewModel.recipients.collectAsState()
    val selectedRecipients by viewModel.selectedRecipients.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val previewUri by viewModel.previewCardUri.collectAsState()

    val currentSchedule = schedule ?: DefaultData.defaultSchedule
    val currentMessage = messages.find { it.id == currentSchedule.selectedMessageId }
        ?: messages.firstOrNull()

    var showSocialShareSheet by remember { mutableStateOf(false) }

    // Calculate remaining time
    val nextMillis = remember(currentSchedule.hour, currentSchedule.minute) {
        AlarmScheduler.calculateNextFridayMillis(currentSchedule.hour, currentSchedule.minute)
    }
    val remainingText = remember(nextMillis) {
        AlarmScheduler.formatRemainingTime(nextMillis)
    }

    LaunchedEffect(Unit) {
        viewModel.generatePreviewCard()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EmeraldPrimary,
                                EmeraldDarkBackground
                            )
                        )
                    )
            ) {
                // Background subtle mosque silhouette
                Image(
                    painter = painterResource(id = R.drawable.img_mosque_sunset),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.22f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HAYIRLI CUMALAR",
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Cuma Mesajları",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        IconButton(
                            onClick = onNavigateToSchedule,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .testTag("header_settings_button")
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Zamanlayıcı", tint = Color.White)
                        }
                    }

                    // Next countdown badge
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Sıradaki Cuma Gönderimi:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = remainingText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Switch(
                                checked = currentSchedule.isEnabled,
                                onCheckedChange = { viewModel.toggleScheduleEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldPrimary,
                                    checkedTrackColor = GoldAccent
                                ),
                                modifier = Modifier.testTag("timer_toggle_switch")
                            )
                        }
                    }
                }
            }
        }

        // WhatsApp Main Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("whatsapp_action_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF25D366), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WhatsApp Gönderimi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${selectedRecipients.size} kişi seçili (Toplam ${recipients.size})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = onNavigateToContacts,
                            modifier = Modifier.testTag("edit_contacts_shortcut")
                        ) {
                            Text("Kişileri Düzenle")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary launch button
                    Button(
                        onClick = { viewModel.startSequentialSend() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("start_sequential_send_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Seçilenlere Sırayla Gönder (${selectedRecipients.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Share button
                    OutlinedButton(
                        onClick = {
                            WhatsAppSender.shareGeneralToWhatsApp(
                                context = context,
                                message = currentMessage?.content ?: "Hayırlı Cumalar",
                                imageUri = previewUri
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("share_status_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp Durumunda / Grupta Paylaş")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Multi-platform social share button
                    FilledTonalButton(
                        onClick = { showSocialShareSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("share_all_social_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Telegram, Insta, Messenger, Snapchat'te Paylaş")
                    }
                }
            }
        }

        // Selected Cuma Message Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToMessages() }
                    .testTag("selected_message_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ChatBubble,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bu Cuma Gönderilecek Mesaj",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Değiştir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    currentMessage?.let { msg ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(msg.category, fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Live Generated Cuma Card Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToCardCreator() }
                    .testTag("selected_card_preview"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (previewUri != null) {
                            AsyncImage(
                                model = previewUri,
                                contentDescription = "Cuma Kartı",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_mosque_sunset),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Resimli Cuma Kartı & Pinterest",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Camii fotoğrafları ve mesajı birleştirip görsel kart olarak paylaşın.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Düzenle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Selected Recipients Carousel Preview
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seçilen Alıcılar (${selectedRecipients.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToContacts) {
                        Text("Tümünü Gör")
                    }
                }

                if (selectedRecipients.isEmpty()) {
                    Text(
                        text = "Henüz seçilen kişi yok. Mesaj iletilecek kişileri seçin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(selectedRecipients) { contact ->
                            AssistChip(
                                onClick = { onNavigateToContacts() },
                                label = { Text(contact.name, maxLines = 1) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = EmeraldPrimary
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSocialShareSheet) {
        SocialShareSheet(
            message = currentMessage?.content ?: "Hayırlı Cumalar",
            imageUri = previewUri,
            onDismiss = { showSocialShareSheet = false }
        )
    }
}

// Alpha extension modifier helper
fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)
