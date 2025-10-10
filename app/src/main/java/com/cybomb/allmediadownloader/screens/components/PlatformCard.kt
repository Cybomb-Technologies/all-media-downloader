package com.cybomb.allmediadownloader.screens.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cybomb.allmediadownloader.datamodels.DownloaderScreen

@Composable
fun PlatformIcon(item: DownloaderScreen, onClick: () -> Unit) {
    val cardSize = 80.dp
    val iconSize = 42.dp
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "icon_scale"
    )

    // Outer glow using brand color
    Box(
        modifier = Modifier.run {
            graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = item.color.copy(alpha = 0.35f),
                    spotColor = item.color.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                )
                .clickable(
                    onClick = onClick,
                    onClickLabel = "Open ${item.label}",
                    indication = ripple(
                        bounded = true,
                        color = item.color.copy(alpha = 0.3f)
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                )

//                .pointerInput(Unit) {
//                    detectTapGestures(
//                        onPress = {
//                            isPressed = true
//                            tryAwaitRelease()
//                            isPressed = false
//                        }
//                    )
//                }
                .size(cardSize)
        },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.label,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    shadowElevation = 2f
                    shape = RoundedCornerShape(12.dp)
                    clip = true
                },
            colorFilter = null // Keeps brand colors pure
        )
    }
}

//@Composable
//fun PlatformIcon(item: DownloaderScreen, onClick: () -> Unit) {
//    val clickAreaSize = 72.dp // Define a standard square touch target area
//
//    Box(
//        modifier = Modifier
//            .size(clickAreaSize)
//            .clickable(onClick = onClick),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = painterResource(id = item.iconRes),
//            contentDescription = item.label,
//            // The size of the logo itself
//            modifier = Modifier.size(48.dp)
//        )
//    }
//}




//@Composable
//fun PlatformCard(item: DownloaderScreen, onClick: () -> Unit) {
//    Card(
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.1f)),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(120.dp)
//            .clickable(onClick = onClick)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Icon(
//                item.icon,
//                contentDescription = item.label,
//                tint = item.color,
//                modifier = Modifier.size(40.dp)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                item.label,
//                style = MaterialTheme.typography.labelMedium,
//                fontWeight = FontWeight.SemiBold,
//                color = item.color
//            )
//        }
//    }
//}
