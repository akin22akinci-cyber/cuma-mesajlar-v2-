package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.sample.DefaultData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.CumaViewModel
import com.example.util.PinterestHelper
import com.example.util.WhatsAppSender

@Composable
fun CardCreatorScreen(viewModel: CumaViewModel) {
    val context = LocalContext.current
    val schedule by viewModel.schedule.collectAsState()
    val previewUri by viewModel.previewCardUri.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    val currentSchedule = schedule ?: DefaultData.defaultSchedule
    var pinterestInputUrl by remember { mutableStateOf("") }
    var signatureInput by remember { mutableStateOf(currentSchedule.senderSignature) }

    val defaultGallery = listOf(
        Pair("mosque_sunset", "Sultanahmet Günbatımı"),
        Pair("mosque_spiritual", "Manevi Avlu ve Güller")
    )

    LaunchedEffect(Unit) {
        viewModel.generatePreviewCard()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("card_creator_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Top Card Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_preview_container"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Canlı Cuma Kartı Önizleme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(onClick = { viewModel.generatePreviewCard() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Yenile", tint = EmeraldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Card Image Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewUri != null) {
                            AsyncImage(
                                model = previewUri,
                                contentDescription = "Oluşturulan Cuma Kartı",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            CircularProgressIndicator(color = GoldAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test Send Button
                    Button(
                        onClick = {
                            WhatsAppSender.shareGeneralToWhatsApp(
                                context = context,
                                message = "Hayırlı Cumalar",
                                imageUri = previewUri
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_share_card_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kartı WhatsApp ile Test Et", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Pinterest Search & Download Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pinterest_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = Color(0xFFE60023), // Pinterest Red
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Pinterest Cami ve Cuma Fotoğrafları",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pinterest aramasından beğendiğiniz fotoğrafları doğrudan indirin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Open Pinterest search button
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PinterestHelper.PINTEREST_SEARCH_URL))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_pinterest_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pinterest'te Cami ve Cuma Fotoğraflarını Aç")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Paste link and download
                    OutlinedTextField(
                        value = pinterestInputUrl,
                        onValueChange = { pinterestInputUrl = it },
                        label = { Text("Pinterest veya Web Resim Linki") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        trailingIcon = {
                            if (pinterestInputUrl.isNotEmpty()) {
                                IconButton(onClick = { pinterestInputUrl = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Temizle")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pinterest_url_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (pinterestInputUrl.isNotBlank()) {
                                viewModel.downloadWebImage(pinterestInputUrl) { success ->
                                    if (success) {
                                        pinterestInputUrl = ""
                                    }
                                }
                            }
                        },
                        enabled = pinterestInputUrl.isNotBlank() && !isProcessing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("download_pinterest_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("İndiriliyor...")
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Resmi İndir ve Kart Yap")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Local Mosque Gallery
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gallery_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hazır Manevi Cami Fotoğrafları",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kartınızın arka planında kullanmak istediğiniz fotoğrafı seçin:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(defaultGallery) { (key, title) ->
                            val isSelected = currentSchedule.selectedImageKey == key

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(130.dp)
                                    .clickable { viewModel.updateSelectedImageKey(key) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp, 160.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                ) {
                                    val resId = if (key == "mosque_sunset") R.drawable.img_mosque_sunset
                                    else R.drawable.img_mosque_spiritual

                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(24.dp)
                                                .background(EmeraldPrimary, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Seçili",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Card Settings: Signature & Merge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_settings_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kart Özelleştirme & İmza",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mesajı Resimle Birleştir",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Seçili dua ve ayeti resmin üzerine yazarak tek bir kart resmi oluşturur.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = currentSchedule.mergeTextOnImage,
                            onCheckedChange = { viewModel.toggleMergeTextOnImage(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = signatureInput,
                        onValueChange = {
                            signatureInput = it
                            viewModel.updateSenderSignature(it)
                        },
                        label = { Text("Gönderen İmzası (Kartın Altı)") },
                        placeholder = { Text("Örn: Ahmet Yılmaz ve Ailesi") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signature_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
