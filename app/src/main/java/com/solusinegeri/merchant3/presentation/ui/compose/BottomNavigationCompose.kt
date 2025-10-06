package com.solusinegeri.merchant3.presentation.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.utils.DynamicColors

sealed class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object Home : BottomNavItem("home", "Home", R.drawable.ic_home)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.ic_profile)
}

@Composable
fun BottomNavigationCompose(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Profile)

    val context = LocalContext.current
    val primaryColor = Color(DynamicColors.getPrimaryColor(context))
    val secondaryColor = colorResource(id = R.color.text_secondary)

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Top drop shadow (hanya di sisi atas nav)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp) // keluar sedikit ke atas
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x33000000), Color.DarkGray)
                    )
                )
        )

        // NAV container tanpa rounded + ada elevation/shadow
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            tonalElevation = 10.dp,
            shadowElevation = 10.dp // shadow global (boleh dibiarkan juga)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = tween(durationMillis = 200),
                        label = "scale_animation"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = if (isSelected) 0.15f else 0.05f,
                        animationSpec = tween(durationMillis = 200),
                        label = "alpha_animation"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            // indikator boleh tetap rounded (ini bukan “nav container”)
                            .background(
                                if (isSelected) primaryColor.copy(alpha = alpha)
                                else Color.White.copy(alpha = alpha),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 12.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(item.route) }
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.title,
                            tint = if (isSelected) primaryColor else secondaryColor,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(scale)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.title,
                            color = if (isSelected) primaryColor else secondaryColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.scale(scale)
                        )
                    }
                }
            }
        }

        // FAB
        val fabScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 200),
            label = "fab_scale"
        )

        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.Center)
                .scale(fabScale),
            containerColor = primaryColor,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 12.dp,
                pressedElevation = 16.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_qr_nav),
                contentDescription = "QR Scanner",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationComposePreview() {
    BottomNavigationCompose(
        currentRoute = "home",
        onNavigate = {},
        onFabClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationComposeProfilePreview() {
    BottomNavigationCompose(
        currentRoute = "profile",
        onNavigate = {},
        onFabClick = {}
    )
}
