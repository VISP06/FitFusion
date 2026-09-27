package com.example.fitfusion.ui.auth

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitfusion.ui.theme.NavyDeep

@Composable
fun ManifestoGateScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToApp: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFEAE4D9),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "THE FITFUSION MANIFESTO",
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.acceptManifesto {
                            onNavigateToApp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyDeep,
                        contentColor = Color(0xFFEAE4D9)
                    )
                ) {
                    Text(
                        text = "I ACCEPT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        viewModel.logout()
                    }
                ) {
                    Text(
                        text = "Decline & Logout",
                        color = NavyDeep,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
