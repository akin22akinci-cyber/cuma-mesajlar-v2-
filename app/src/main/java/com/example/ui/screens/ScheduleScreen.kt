package com.example.ui.screens

import android.app.TimePickerDialog
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.sample.DefaultData
import com.example.receiver.FridayAlarmReceiver
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.CumaViewModel
import com.example.util.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleScreen(viewModel: CumaViewModel) {
    val context = LocalContext.current
    val schedule by viewModel.schedule.collectAsState()
    val sentLogs by viewModel.sentLogs.collectAsState()

    val currentSchedule = schedule ?: DefaultData.defaultSchedule

    val nextMillis = remember(currentSchedule.hour, currentSchedule.minute) {
        AlarmScheduler.calculateNextFridayMillis(currentSchedule.hour, currentSchedule.minute)
    }
    val remainingText = remember(nextMillis) {
        AlarmScheduler.formatRemainingTime(nextMillis)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("schedule_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_timer_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(EmeraldPrimary.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, tint = EmeraldPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Cuma Zamanlayıcısı",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Her Cuma otomatik bildirim ve gönderim",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = currentSchedule.isEnabled,
                            onCheckedChange = { viewModel.toggleScheduleEnabled(it) },
                            modifier = Modifier.testTag("schedule_main_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Time Display & Picker Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Gönderim Saati:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = AlarmScheduler.formatTime(currentSchedule.hour, currentSchedule.minute),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "Kalan süre: $remainingText",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        viewModel.updateScheduleTime(selectedHour, selectedMinute)
                                    },
                                    currentSchedule.hour,
                                    currentSchedule.minute,
                                    true
                                ).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("change_time_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Saati Değiştir")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Test notification button
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(context, FridayAlarmReceiver::class.java).apply {
                                action = "com.aistudio.cumamesaj.ACTION_FRIDAY_ALARM"
                            }
                            context.sendBroadcast(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("test_alarm_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Zamanlayıcı Bildirimini Test Et")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Information: No API needed guide
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("info_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kolay ve API Gerektirmeyen Kullanım",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Karmaşık WhatsApp Cloud API veya kayıt gerekmez.\n" +
                                "• Cuma vakti geldiğinde size sesli ve titreşimli hatırlatma yapar.\n" +
                                "• Tek bir dokunuşla seçtiğiniz kişilere resimli kartı ve mesajınızı WhatsApp üzerinden doğrudan iletir.\n" +
                                "• Sıralı Gönderim ekranı ile onlarca kişiye saniyeler içinde sırayla gönderebilirsiniz.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Sent History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gönderim Geçmişi (${sentLogs.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (sentLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz gönderim geçmişi bulunmuyor. Mesaj gönderdiğinizde burada listelenecektir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(sentLogs) { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.contactName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                .format(Date(log.timestamp))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "İletildi",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF25D366),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
