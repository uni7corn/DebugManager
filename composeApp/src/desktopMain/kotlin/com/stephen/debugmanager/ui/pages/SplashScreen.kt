package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(appContent: @Composable () -> Unit) {
    var splashState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    LaunchedEffect(Unit) {
        delay(2400L)
        splashState.targetState = false
    }
    Box {
        if (!splashState.targetState)
            appContent()
        AnimatedVisibility(
            visibleState = splashState,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            SplashScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .animateEnterExit(
                        enter = fadeIn(),
                        exit = fadeOut()
                    )
                    .background(Color.White)
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
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "Splash Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to DebugManager",
                style = MaterialTheme.typography.h5,
                fontSize = 24.sp
            )
        }
    }
}