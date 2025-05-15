package com.example.juakaliconnect.screens.artisans

import androidx.compose.foundation.Image
import com.example.juakaliconnect.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.juakaliconnect.auth.AuthViewModel
import com.example.juakaliconnect.navigation.BottomNavBar
import com.example.juakaliconnect.navigation.Routes

@Composable
fun ArtisanDashboardScreen(navController: NavHostController, artisanId: String, authViewModel: AuthViewModel, userRole: String) {
    Scaffold(
        bottomBar = { BottomNavBar(navController, authViewModel, userRole) }
    ) { paddingValues -> // ✅ Capture content padding
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) { // 🔥 Apply padding here
            Image(
                painter = painterResource(id = R.drawable.ln),
                contentDescription = "Dashboard Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp), // ✅ Keep original padding
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "👋 Welcome, Artisan!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0277BD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📩 Service Requests", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text("Manage incoming client requests efficiently.", color = Color.White)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Routes.REQUESTS) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                        ) {
                            Text("🔍 View Requests")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4A148C))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚙️ Profile Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text("Update profile details and availability.", color = Color.White)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Routes.PROFILE_UPDATE) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                        ) {
                            Text("🛠 Update Profile")
                        }
                    }
                }
            }
        }
    }
}