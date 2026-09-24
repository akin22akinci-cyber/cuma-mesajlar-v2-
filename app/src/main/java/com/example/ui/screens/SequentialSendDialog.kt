package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.SequentialSendStatus
import com.example.util.WhatsAppSender

@Composable
fun SequentialSendDialog(
    status: SequentialSendStatus,
    onAdvance: (Boolean) -> Unit,
    onCancel: () -> Unit
) {
    if (!status.isActive || status.contacts.isEmpty()) return

    val context = LocalContext.current
    val currentContact = status.contacts.getOrNull(status.currentIndex) ?: return
    val progress = (status.currentIndex + 1).toFloat() / status.contacts.size

    val personalizedMessage = status.currentMessage.replace("{isim}", currentContact.name)

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("sequential_send_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WhatsApp Gönderim Sırası",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onCancel, modifier = Modifier.testTag("cancel_send_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = EmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${status.currentIndex + 1} / ${status.contacts.size} Kişi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current Contact Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = currentContact.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentContact.phoneNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Message snippet preview
                Text(
                    text = "“${personalizedMessage.take(90)}...”",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action 1: Open WhatsApp for this contact
                Button(
                    onClick = {
                        WhatsAppSender.launchDirectSend(
                            context = context,
                            phoneNumber = currentContact.phoneNumber,
                            message = personalizedMessage,
                            imageUri = status.currentImageUri
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("launch_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // WhatsApp Green
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WhatsApp'ta Aç ve Gönder",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action 2: Advance to next contact
                FilledTonalButton(
                    onClick = { onAdvance(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_contact_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (status.currentIndex + 1 < status.contacts.size) "Gönderildi, Sıradakine Geç" else "Tamamla",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Skip button
                TextButton(
                    onClick = { onAdvance(false) },
                    modifier = Modifier.testTag("skip_contact_button")
                ) {
                    Text("Bu Kişiyi Atla", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
