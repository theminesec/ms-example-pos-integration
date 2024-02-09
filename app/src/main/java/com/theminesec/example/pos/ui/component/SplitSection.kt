package com.theminesec.example.pos.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun SplitSection(
    upperContent: @Composable () -> Unit,
    lowerContent: @Composable () -> Unit,
) {
    val density = LocalDensity.current.density
    val screenHeight = LocalConfiguration.current.screenHeightDp
    var upperColumnHeight by remember {
        // initial 600.dp.value * screen density
        mutableFloatStateOf(600 * density)
    }
    var isDragging by remember { mutableStateOf(false) }
    val dragState = rememberDraggableState { delta ->
        upperColumnHeight = (upperColumnHeight + delta)
            .coerceAtLeast(100 * density)
            .coerceAtMost((screenHeight - 100) * density)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .height((upperColumnHeight / density).dp)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "MineSec Example\nPOS Lib", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            upperContent()
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(if (isDragging) 0.3f else 0.1f))
                .padding(8.dp)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = dragState,
                    onDragStarted = { isDragging = true },
                    onDragStopped = { isDragging = false }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(6.dp)
                    .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            lowerContent()
        }
    }
}
