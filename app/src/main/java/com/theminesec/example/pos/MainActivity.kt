package com.theminesec.example.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theminesec.example.pos.ui.ExampleSection
import com.theminesec.example.pos.ui.ExampleViewModel
import com.theminesec.example.pos.ui.component.ObjectDisplay
import com.theminesec.example.pos.ui.component.SplitSection
import com.theminesec.example.pos.ui.theme.MsExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MsExampleTheme {
                val viewModel = viewModel(modelClass = ExampleViewModel::class.java)
                val messages by viewModel.messages.collectAsState()
                val lazyListState = rememberLazyListState()
                val snackbarHostState = remember { SnackbarHostState() }

                // scroll to bottom when new message comes in
                LaunchedEffect(messages) {
                    lazyListState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            SplitSection(
                                upperContent = { ExampleSection(snackbarHostState) },
                                lowerContent = {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        LazyColumn(
                                            state = lazyListState,
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(
                                                start = 16.dp,
                                                top = 56.dp,
                                                end = 16.dp,
                                                bottom = 16.dp
                                            )
                                        ) {
                                            items(messages) { msg -> ObjectDisplay(msg) }
                                        }
                                        Button(
                                            onClick = { viewModel.clearLog() },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .defaultMinSize(minHeight = 40.dp, minWidth = 64.dp)
                                                .padding(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = MaterialTheme.colorScheme.background.copy(0.6f),
                                                contentColor = Color.White.copy(0.8f)
                                            ),
                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Clear")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
