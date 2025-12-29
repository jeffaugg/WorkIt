package pies3.workit.ui.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pies3.workit.R


private const val PHASE_IN_DURATION = 1000
private const val PAUSE_DURATION = 500L
private const val PHASE_OUT_DURATION = 800

@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit
) {

    val scale = remember { Animatable(2f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-180f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = PHASE_IN_DURATION, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = PHASE_IN_DURATION / 2, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = PHASE_IN_DURATION, easing = LinearOutSlowInEasing)
            )
        }

        delay(PHASE_IN_DURATION + PAUSE_DURATION)

        launch {
            scale.animateTo(
                targetValue = 8f,
                animationSpec = tween(durationMillis = PHASE_OUT_DURATION, easing = FastOutLinearInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = PHASE_OUT_DURATION, easing = FastOutLinearInEasing)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 180f,
                animationSpec = tween(durationMillis = PHASE_OUT_DURATION, easing = FastOutLinearInEasing)
            )
        }

        delay(PHASE_OUT_DURATION.toLong())
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "WorkIt Logo",
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value)
                .rotate(rotation.value)
                .alpha(alpha.value)
        )
    }
}