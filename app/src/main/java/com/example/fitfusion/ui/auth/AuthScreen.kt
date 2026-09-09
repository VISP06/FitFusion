package com.example.fitfusion.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitfusion.ui.theme.BeigeAccent
import com.example.fitfusion.ui.theme.FitFusionTheme
import com.example.fitfusion.ui.theme.NavyDeep

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = BeigeAccent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .border(width = 2.dp, color = NavyDeep, shape = RectangleShape)
                .background(Color(0xFFEAE4D9))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Declare the hoisted state variables at the top of AuthScreen
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            OutlinedTextField(
                value = username,
                onValueChange = { newText -> username = newText },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun screenTester(){
    FitFusionTheme {
        AuthScreen()
    }
}