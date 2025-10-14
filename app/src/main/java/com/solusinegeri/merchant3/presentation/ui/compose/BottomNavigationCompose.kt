package com.solusinegeri.merchant3.presentation.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.utils.DynamicColors

sealed class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object Home : BottomNavItem("home", "Home", R.drawable.ic_home)
    object Analytics : BottomNavItem("analytics", "Analytics", R.drawable.ic_analytics)
    object News : BottomNavItem("news", "News", R.drawable.ic_news)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.ic_profile)
}

@Composable
fun BottomNavigationCompose(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Analytics,
        BottomNavItem.News,
        BottomNavItem.Profile
    )

    val context = LocalContext.current
    val primaryColor = Color(DynamicColors.getPrimaryColor(context))
    val secondaryColor = colorResource(id = R.color.text_secondary)

    // Dynamic dimensions
    val fabDiameter = 64.dp
    val fabSpaceWidth = fabDiameter * 1.25f // Slightly larger than FAB for spacing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        // ⬇️ NAV "floating" berbentuk pill
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            tonalElevation = 8.dp, // pill
            border = BorderStroke(1.dp, Color(0x11000000)) // halo tipis
        ) {
            Column(Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        if (index == items.size / 2) {
                            // celah di tengah (biar terlihat seperti ada notch untuk FAB)
                            Spacer(modifier = Modifier.width(fabSpaceWidth))
                        }

                        val isSelected = currentRoute == item.route

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.1f else 1f,
                            animationSpec = tween(durationMillis = 200),
                            label = "scale_animation"
                        )

                        val alpha by animateFloatAsState(
                            targetValue = if (isSelected) 0.16f else 0.06f,
                            animationSpec = tween(durationMillis = 200),
                            label = "alpha_animation"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) primaryColor.copy(alpha = alpha)
                                    else Color(0x00000000),
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
                                    .size(22.dp)
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
        }

        // ⬇️ FAB di tengah, sedikit “menembus” nav biar makin floating
        val fabScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 200),
            label = "fab_scale"
        )

        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(64.dp)
                .scale(fabScale),
            containerColor = primaryColor,
            contentColor = Color.White,
            shape = CircleShape,
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
