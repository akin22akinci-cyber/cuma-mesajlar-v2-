package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.SocialPlatform
import com.example.util.SocialShareManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialShareSheet(
    message: String,
    imageUri: Uri?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val platforms = listOf(
        SocialPlatform.WHATSAPP,
        SocialPlatform.TELEGRAM,
        SocialPlatform.INSTAGRAM,
        SocialPlatform.MESSENGER,
        SocialPlatform.FACEBOOK,
        SocialPlatform.SNAPCHAT,
        SocialPlatform.UNIVERSAL
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .testTag("social_share_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle top indicator & header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cuma Tebrikini Paylaş",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sosyal medya ve mesajlaşma uygulamalarıyla iletin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social platforms grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                platforms.take(4).forEach { platform ->
                    SocialIconItem(
                        platform = platform,
                        onClick = {
                            SocialShareManager.shareToPlatform(context, platform, message, imageUri)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                platforms.drop(4).forEach { platform ->
                    SocialIconItem(
                        platform = platform,
                        onClick = {
                            SocialShareManager.shareToPlatform(context, platform, message, imageUri)
                            onDismiss()
                        }
                    )
                }
                // Fill spacing for 3 items
                Spacer(modifier = Modifier.size(64.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Universal iOS style share button
            OutlinedButton(
                onClick = {
                    SocialShareManager.shareUniversal(context, message, imageUri)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("share_other_apps_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tüm Uygulamaları Göster (Sistem Menüsü)", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun SocialIconItem(
    platform: SocialPlatform,
    onClick: () -> Unit
) {
    val platformColor = try {
        Color(android.graphics.Color.parseColor(platform.hexColor))
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(platformColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (platform) {
                    SocialPlatform.WHATSAPP -> "WA"
                    SocialPlatform.TELEGRAM -> "TG"
                    SocialPlatform.INSTAGRAM -> "IG"
                    SocialPlatform.MESSENGER -> "MSG"
                    SocialPlatform.FACEBOOK -> "FB"
                    SocialPlatform.SNAPCHAT -> "SC"
                    SocialPlatform.UNIVERSAL -> "✦"
                },
                color = if (platform == SocialPlatform.SNAPCHAT) Color.Black else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = platform.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
