package com.example.fitfusion.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitfusion.ui.theme.NavyDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFEAE4D9),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PRIVACY & SECURITY",
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyDeep
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PolicyCard(
                title = "01 / DATA ENCRYPTION",
                description = "Your clothing images and wardrobe inventory are encrypted and stored securely in the cloud. We do not sell your personal data or wardrobe history to third-party data brokers."
            )

            PolicyCard(
                title = "02 / AI & IMAGE PRIVACY",
                description = "FitFusion AI analyzes clothing categories, colors, and materials to generate outfits. Image processing isolates garment metadata only—it never processes facial recognition or personal identity data."
            )

            PolicyCard(
                title = "03 / AFFILIATE & MONETIZATION",
                description = "Future styling recommendations may include missing wardrobe pieces available for purchase. Clicking or purchasing through these links helps support FitFusion via affiliate commissions without additional cost to you."
            )
        }
    }
}

@Composable
fun PolicyCard(
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(BorderStroke(2.dp, NavyDeep), shape = RectangleShape)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyDeep
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = NavyDeep.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )
        }
    }
}
