package pies3.workit.ui.features.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pies3.workit.R
import pies3.workit.ui.theme.BackgroundEndGradient
import pies3.workit.ui.theme.BackgroundStartGradient


private const val SPLASH_DURATION = 2200L

private const val ANIMATION_DURATION = 1500
@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = { OvershootInterpolator(2f).getInterpolation(it) }
        ),
        label = "LogoScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION - 500
        ),
        label = "LogoAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(SPLASH_DURATION)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundStartGradient, BackgroundEndGradient)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_workit),
            contentDescription = "WorkIt Logo",
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .alpha(alpha)
        )
    }
}