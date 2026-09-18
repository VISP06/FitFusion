package com.example.fitfusion.ui.auth

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitfusion.data.database.SupabaseClient
import com.example.fitfusion.ui.theme.BeigeAccent
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.theme.Typography
import io.github.jan.supabase.auth.auth

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onSignedOut: () -> Unit
) {
    val userEmail = SupabaseClient.client.auth.currentUserOrNull()?.email ?: "Unknown User"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeAccent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(NavyDeep, RectangleShape)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BeigeAccent)
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, color = NavyDeep, shape = RectangleShape)
                    .background(Color(0xFFEAE4D9))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PROFILE",
                    style = Typography.titleMedium,
                    color = NavyDeep
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "SIGNED IN AS",
                    fontSize = 12.sp,
                    color = NavyDeep,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = userEmail,
                    fontSize = 16.sp,
                    color = NavyDeep,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(24.dp))

                TextButton(onClick = {
                    viewModel.signOut()
                    onSignedOut()
                }) {
                    Text("-> Log out", color = NavyDeep)
                }
            }
        }
    }
}
