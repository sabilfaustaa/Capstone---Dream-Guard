package com.android.dreamguard.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.dreamguard.R

@Composable
fun CustomBottomNavigation(
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .background(Color(0xFF6C63FF), shape = RoundedCornerShape(50.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationTab(
                icon = ImageVector.vectorResource(id = R.drawable.ic_home),
                label = "Home",
                backgroundColor = Color(0xFF9ED9D9),
                contentColor = Color.White
            ) {
                onTabSelected("Home")
            }

            Spacer(modifier = Modifier.width(50.dp))

            BottomNavigationTab(
                icon = ImageVector.vectorResource(id = R.drawable.ic_profile),
                label = "Profile",
                backgroundColor = Color.Transparent,
                contentColor = Color.White
            ) {
                onTabSelected("Profile")
            }
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = (-25).dp)
                .background(Color(0xFF6C63FF), shape = CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .clickable { onTabSelected("Predict") },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_predict),
                    contentDescription = "Predict Icon",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
                Text(
                    text = "Predict",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavigationTab(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .width(100.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(backgroundColor, shape = RoundedCornerShape(25.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$label Icon",
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
