package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_android_debug
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.theme.pageTitleText
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(appContent: @Composable () -> Unit) {
    val splashState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    LaunchedEffect(Unit) {
        delay(2400L)
        splashState.targetState = false
    }
    Box {
        appContent()
        AnimatedVisibility(
            visibleState = splashState,
            enter = EnterTransition.None,
            exit = fadeOut(tween(400))
        ) {
            SplashScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_android_debug),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = "Splash Logo",
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CenterText(
                text = "Welcome to DebugManager",
                style = pageTitleText,
            )
        }
    }
}