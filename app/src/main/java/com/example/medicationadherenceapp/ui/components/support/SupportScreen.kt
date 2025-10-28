package com.example.medicationadherenceapp.ui.components.support

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SupportScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Main container card for the entire section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.6f)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // --- Header ---
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Emergency Contacts",
                                tint = Color(0xFF0D6EFD),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency Contacts",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }

                        // --- Info Card ---
                        InfoCard()

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- Contact Cards ---
                        ContactCard(
                            icon = Icons.Default.Call,
                            iconBackgroundColor = Color(0xFFE7F0FF),
                            iconTint = Color(0xFF0D6EFD),
                            contactName = "Dr. Sarah Johnson",
                            contactType = "Primary Doctor",
                            availability = "Mon-Fri 9AM-5PM",
                            buttonText = "Call (555) 123-4567",
                            buttonColor = Color(0xFF0D6EFD)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ContactCard(
                            icon = Icons.Default.FavoriteBorder,
                            iconBackgroundColor = Color(0xFFE3F2E7),
                            iconTint = Color(0xFF198754),
                            contactName = "Mary (Daughter)",
                            contactType = "Emergency Contact",
                            availability = "Anytime",
                            buttonText = "Call (555) 987-6543",
                            buttonColor = Color(0xFF198754),
                            showSecondaryButton = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ContactCard(
                            icon = Icons.Default.WarningAmber,
                            iconBackgroundColor = Color(0xFFFEEFEF),
                            iconTint = Color(0xFFDC3545),
                            contactName = "Poison Control",
                            contactType = "Emergency",
                            availability = "24/7",
                            buttonText = "Call 1-800-222-1222",
                            buttonColor = Color(0xFFDC3545)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
        border = BorderStroke(1.dp, Color(0xFFB6D4FE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Warning",
                tint = Color(0xFF0D6EFD),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "If you're feeling unwell or confused about your medications, don't hesitate to call for help.",
                fontSize = 14.sp,
                color = Color(0xFF0A58CA)
            )
        }
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    contactName: String,
    contactType: String,
    availability: String,
    buttonText: String,
    buttonColor: Color,
    showSecondaryButton: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(contactName, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Text(contactType, fontSize = 14.sp, color = Color.Gray)
                    Text(availability, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* TODO: Implement call action */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(buttonText, fontSize = 14.sp)
                    }
                }
                if (showSecondaryButton) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { /* TODO: Implement chat action */ },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 380)
@Composable
fun SupportScreenPreview() {
    MaterialTheme {
        SupportScreen()
    }
}
