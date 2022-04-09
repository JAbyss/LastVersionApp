import android.annotation.SuppressLint
import android.graphics.Paint as OldPaint
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint as ComposePaint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SegmentProgressBar(
    selectedItem: MutableState<Int>,
    listValues: List<String>,
    modifier: Modifier
) {

    val context = LocalContext.current

    val animatesColors = listOf(
        remember { androidx.compose.animation.Animatable(Color.Gray) },
        remember { androidx.compose.animation.Animatable(Color.Gray) },
        remember { androidx.compose.animation.Animatable(Color.Gray) },
        remember { androidx.compose.animation.Animatable(Color.Gray) },
    )
    val scope = rememberCoroutineScope()

    scope.launch {
        animateIndicator(0, selectedItem.value, animatesColors)
    }
    val maxCount = 4

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    scope.launch {
                        var a = size.width
                        when (it.x) {
                            in size.width * 0.5f..size.width * 0.75f -> {
                                animateIndicator(
                                    oldValue = selectedItem.value,
                                    newValue = 2,
                                    animatesColors
                                )
                                selectedItem.value = 2
                                Toast
                                    .makeText(context, "1", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            in size.width * 0.25f..size.width * 0.5f -> {
                                animateIndicator(
                                    oldValue = selectedItem.value,
                                    newValue = 1,
                                    animatesColors
                                )
                                selectedItem.value = 1
                                Toast
                                    .makeText(context, "2", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            in 0f..size.width * 0.25f -> {
                                animateIndicator(
                                    oldValue = selectedItem.value,
                                    newValue = 0,
                                    animatesColors
                                )
                                selectedItem.value = 0
                                Toast
                                    .makeText(context, "3", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            in size.width * 0.75f..size.width.toFloat() -> {
                                animateIndicator(
                                    oldValue = selectedItem.value,
                                    newValue = 3,
                                    animatesColors
                                )
                                selectedItem.value = 3
                                Toast
                                    .makeText(context, "4", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else -> {
                                Toast
                                    .makeText(
                                        context,
                                        "it = ${it.x}, size = ${size.width}",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                    }
                }
            }
    ) {
        repeat(maxCount) {
            drawSegment(maxCount, it, color = animatesColors[it])
        }
        val paintForCircle = ComposePaint()
        paintForCircle.color = Color.White
        val centerOffset = Offset(center.x, -center.y * 2)
        drawCircle(
            color = Color.White,
            center = centerOffset,
            radius = 55f,
            alpha = 0.9f
        )
        drawIntoCanvas {
            val paint = OldPaint()
            paint.textAlign = OldPaint.Align.CENTER
            paint.textSize = 64f
            paint.color = 0xff000000.toInt()
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            it.nativeCanvas.drawText(
                listValues[selectedItem.value],
                center.x,
                -center.y,
                paint
            )
        }
    }
}

private suspend fun animateIndicator(
    oldValue: Int,
    newValue: Int,
    listAnimatalbes: List<Animatable<Color, AnimationVector4D>>
) {
        if (oldValue > newValue) {
            listAnimatalbes.forEachIndexed { index, animatable ->
                if (index > newValue && index != 0)
                    if (animatable.value != Color.Gray)
                        animatable.animateTo(Color.Gray)
            }
        } else {
            listAnimatalbes.forEachIndexed { index, animatable ->
                if (index <= newValue)
                    if (animatable.value != Color.Green)
                        animatable.animateTo(Color.Green)
            }
        }
}

private fun DrawScope.drawSegment(maxCount: Int, i: Int, color: Animatable<Color, AnimationVector4D>) {
    val path = if (i < maxCount / 2) {
        Path().apply {
            reset()
            val spacer = (size.width / maxCount) * 0.10f
            val deviation = (size.width / maxCount) * 0.15f
            val width = (size.width / maxCount) - spacer - deviation
            val start = if (i != 0) (size.width / maxCount) * (i) else 0f
            moveTo(spacer + start, 0f)
            lineTo(start + width, 0f)
            lineTo(start + width + deviation, size.height)
            lineTo(spacer + start + deviation, size.height)
            lineTo(spacer + start, 0f)
            close()
        }
    } else {
        Path().apply {
            reset()
            val spacer = (size.width / maxCount) * 0.10f
            val deviation = (size.width / maxCount) * 0.15f
            val width = (size.width / maxCount) - spacer - deviation
            val start = if (i != 0) (size.width / maxCount) * (i) + deviation else 0f
            moveTo(spacer + start, 0f)
            lineTo(start + width, 0f)
            lineTo(start + width - deviation, size.height)
            lineTo(spacer + start - deviation, size.height)
            lineTo(spacer + start, 0f)
            close()
        }
    }
    drawPath(
        path = path,
        color = color.value,
        alpha = 1f
    )
}