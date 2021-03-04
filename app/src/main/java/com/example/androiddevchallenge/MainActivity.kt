/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.main
import com.example.androiddevchallenge.ui.theme.red
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var isRunning by mutableStateOf(false)
var isPause by mutableStateOf(false)
var progress by mutableStateOf(3002f) // 25min
const val DividerLengthInDegrees = 1.8f

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxHeight()
            .background(main)

    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Pomodoro Technique",
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(
                            top = 20.dp
                        )
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(top = 100.dp)
                )

                ConstraintLayout(Modifier.fillMaxWidth().size(220.dp).padding(top = 20.dp)) {
                    val (box, circle, text) = createRefs()
                    AnimatedCircle(
                        Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .constrainAs(circle) {
                                top.linkTo(box.top, margin = 5.dp)
                                start.linkTo(box.start)
                                end.linkTo(box.end)
                            },
                        progress
                    )
                    val seconds = (((progress - 2) % 120) / 2).toInt()
                    Text(
                        text = "${((progress - 2) / 120).toInt()}:${if (seconds < 10) "0$seconds" else seconds}",
                        style = MaterialTheme.typography.h4,
                        color = Color.White,
                        modifier = Modifier
                            .constrainAs(text) {
                                top.linkTo(circle.top, margin = 4.dp)
                                start.linkTo(circle.start, margin = 4.dp)
                                end.linkTo(circle.end, margin = 4.dp)
                                bottom.linkTo(circle.bottom, margin = 4.dp)
                            }
                    )
                }
            }

            setButtonView(coroutineScope)
        }
    }
}

@Composable
fun setButtonView(coroutineScope: CoroutineScope) {
    Row(
        modifier = Modifier
            .padding(
                start = 48.dp,
                end = 48.dp,
                bottom = 24.dp,
                top = 120.dp
            )
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                isRunning = !isPause
                coroutineScope.launch {
                    setAnimation()
                }
                isPause = !isPause
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        ) {
            Text(
                text = if (isPause) "Pause" else "Start",
                color = main,
                style = MaterialTheme.typography.h6
            )
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        Button(
            onClick = {
                isRunning = false
                isPause = false
                progress = 3002f
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = red),
        ) {
            Text(
                text = "Reset",
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
        }
    }
}

@Composable
fun AnimatedCircle(
    modifier: Modifier = Modifier,
    sweep: Float
) {
    val stroke = with(LocalDensity.current) { Stroke(5.dp.toPx()) }
    Canvas(modifier) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        val startAngle = -90f
        drawArc(
            color = Color.White,
            startAngle = startAngle + DividerLengthInDegrees / 2,
            sweepAngle = sweep - DividerLengthInDegrees,
            topLeft = topLeft,
            size = size,
            useCenter = false,
            style = stroke
        )
    }
}

suspend fun setAnimation() {
    while (isRunning) {
        progress -= 1
        if (progress <= 2) {
            progress = 3002f
            isRunning = false
        }
        delay(500)
    }
}
