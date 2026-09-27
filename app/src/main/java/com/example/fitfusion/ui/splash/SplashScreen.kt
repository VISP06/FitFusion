package com.example.fitfusion.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitfusion.R

@Composable
fun AnimatedSplashScreen() {
    val transition = rememberInfiniteTransition(label = "splash_scan")

    // Animates 0f → 1f → 0f, driving the wipe clip from bottom to top and back
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAE4D9)),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1 – faded base logo always visible
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "FitFusion Logo",
            modifier = Modifier.size(150.dp),
            alpha = 0.2f
        )

        // Layer 2 – solid overlay logo, clipped bottom-to-top as progress grows
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .drawWithContent {
                    clipRect(
                        top = size.height * (1f - progress),
                        bottom = size.height
                    ) {
                        this@drawWithContent.drawContent()
                    }
                }
        )
    }
}
