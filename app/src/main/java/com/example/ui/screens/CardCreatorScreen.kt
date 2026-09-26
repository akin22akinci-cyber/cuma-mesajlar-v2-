package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun CardCreatorScreen(viewModel: CumaViewModel) {
    val context = LocalContext.current
    val schedule by viewModel.schedule.collectAsState()
    val previewUri by viewModel.previewCardUri.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val messages by viewModel.messages.collectAsState()

    val currentSchedule = schedule ?: DefaultData.defaultSchedule
    val currentMessage = messages.find { it.id == currentSchedule.selectedMessageId }
        ?: messages.firstOrNull()

    var pinterestInputUrl by remember { mutableStateOf("") }
    var signatureInput by remember { mutableStateOf(currentSchedule.senderSignature) }
    var showShareSheet by remember { mutableStateOf(false) }

    // Modern Zero-Permission Photo Picker (Android 13+ and backported to Android 4.4+)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.pickCustomPhotoUri(uri)
        }
    }

    val mosqueGallery = PinterestHelper.curatedMosqueGallery

    val fontStyles = listOf(
        Pair("Serif", "Klasik Hat"),
        Pair("Sans", "Modern Sans"),
        Pair("Script", "Zarif İtalik"),
        Pair("Bold", "Belirgin Kalın")
    )

    val frameStyles = listOf(
        Pair("Gold", "Altın Varak"),
        Pair("Emerald", "Zümrüt Bordür"),
        Pair("Lantern", "Kandil & Hilal"),
        Pair("Minimal", "Sade / Çerçevesiz")
    )

    val colorOptions = listOf(
        Pair("#FFFFFF", "Saf Beyaz"),
        Pair("#FFF3D0", "Altın Krem"),
        Pair("#E8FFF5", "Zümrüt Nuru"),
        Pair("#FFE4E1", "Gül Ferahı")
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
        // Top Card Live Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_preview_container"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                        Column {
                            Text(
                                text = "Canlı Cuma Kartı Stüdyosu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Fotoğraf, hat yazısı ve süslemeler canlı güncellenir",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { viewModel.generatePreviewCard() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Yenile", tint = EmeraldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Card Image Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .clip(RoundedCornerShape(18.dp))
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

                    // Share to all platforms button
                    Button(
                        onClick = { showShareSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("share_card_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kartı Paylaş (WhatsApp, Telegram, Insta, FB)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Generate Harmonized Prayer for this image button
                    FilledTonalButton(
                        onClick = {
                            val harmonizedPrayer = PinterestHelper.generateMatchingPrayerForImage(
                                currentSchedule.selectedImageKey,
                                currentSchedule.senderSignature
                            )
                            viewModel.addMessage(
                                title = "Görsele Özel Cuma Duası",
                                content = harmonizedPrayer,
                                category = "Özel"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("generate_harmonized_message_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bu Görsele Uygun Anlamlı Dua Oluştur", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Section 1: Photo Options (Gallery, Pinterest, Presets)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("photo_selection_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Fotoğraf Kaynağı Seçin",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Telefonunuzdan kendi fotoğrafınızı yükleyin, Pinterest'ten indirin veya hazır manevi koleksiyonu kullanın.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: Pick Photo & Pinterest
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("pick_from_gallery_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Galeriden Seç", fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PinterestHelper.PINTEREST_SEARCH_URL))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("open_pinterest_search_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pinterest Aç", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Download URL textfield
                    OutlinedTextField(
                        value = pinterestInputUrl,
                        onValueChange = { pinterestInputUrl = it },
                        label = { Text("Pinterest veya Web Resim Linki") },
                        placeholder = { Text("https://tr.pinterest.com/pin/... veya resim linki") },
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

                    if (pinterestInputUrl.isNotBlank()) {
                        FilledTonalButton(
                            onClick = {
                                viewModel.downloadWebImage(pinterestInputUrl) { success ->
                                    if (success) pinterestInputUrl = ""
                                }
                            },
                            enabled = !isProcessing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Resim İndiriliyor...")
                            } else {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("İnternetten İndir ve Kart Yap")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Veya Hazır Manevi Fotoğraflardan Seçin:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(mosqueGallery) { asset ->
                            val isSelected = currentSchedule.selectedImageKey == asset.key

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(110.dp)
                                    .clickable { viewModel.updateSelectedImageKey(asset.key) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(110.dp, 135.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    val resId = asset.localRes ?: R.drawable.img_mosque_sunset
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = asset.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(22.dp)
                                                .background(EmeraldPrimary, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Seçili",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = asset.tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Section 2: Advanced Typography & Frame Designer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("typography_designer_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Yazı & Tipografi Tasarımı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Yazı tipini, çerçeve stilini, rengi ve boyutu özelleştirin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Font style selection
                    Text(
                        text = "Yazı Tipi Stili:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        fontStyles.forEach { (style, label) ->
                            FilterChip(
                                selected = currentSchedule.fontStyle == style,
                                onClick = { viewModel.updateFontStyle(style) },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Frame style selection
                    Text(
                        text = "Çerçeve ve Bordür Tasarımı:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        frameStyles.forEach { (style, label) ->
                            FilterChip(
                                selected = currentSchedule.frameStyle == style,
                                onClick = { viewModel.updateFrameStyle(style) },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Text Color selection
                    Text(
                        text = "Yazı Rengi:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorOptions.forEach { (hex, label) ->
                            val isColorSelected = currentSchedule.textColorHex.equals(hex, ignoreCase = true)
                            AssistChip(
                                onClick = { viewModel.updateTextColorHex(hex) },
                                label = { Text(label, fontSize = 12.sp) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(1.dp, Color.Gray, CircleShape)
                                    )
                                },
                                colors = if (isColorSelected) {
                                    AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                } else AssistChipDefaults.assistChipColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Text size slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Yazı Boyutu:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "%.1fx".format(currentSchedule.textSizeModifier),
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = currentSchedule.textSizeModifier,
                        onValueChange = { viewModel.updateTextSizeModifier(it) },
                        valueRange = 0.8f..1.4f,
                        steps = 5,
                        modifier = Modifier.testTag("text_size_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Signature input
                    OutlinedTextField(
                        value = signatureInput,
                        onValueChange = {
                            signatureInput = it
                            viewModel.updateSenderSignature(it)
                        },
                        label = { Text("Gönderen İmzası (Kartın Altına Eklenir)") },
                        placeholder = { Text("Örn: Akın Akıncı ve Ailesi") },
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

    if (showShareSheet) {
        SocialShareSheet(
            message = currentMessage?.content ?: "Hayırlı Cumalar",
            imageUri = previewUri,
            onDismiss = { showShareSheet = false }
        )
    }
}
