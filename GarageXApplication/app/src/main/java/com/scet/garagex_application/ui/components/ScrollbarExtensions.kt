package com.scet.garagex_application.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Luxury Atelier Scrollbar Modifiers for Jetpack Compose.
 * Renders an elegant, visible scrollbar indicator on the right edge of scrollable views.
 */

val DefaultScrollbarThumbColor = Color(0xFF6B1D2F).copy(alpha = 0.8f) // Deep Burgundy
val DefaultScrollbarTrackColor = Color.Black.copy(alpha = 0.06f)

/**
 * Draws a visible vertical scrollbar for regular [ScrollState] (e.g. Column with verticalScroll).
 */
fun Modifier.drawVerticalScrollbar(
    state: ScrollState,
    color: Color = DefaultScrollbarThumbColor,
    trackColor: Color = DefaultScrollbarTrackColor,
    width: Dp = 5.dp
): Modifier = drawWithContent {
    drawContent()
    if (state.maxValue > 0) {
        val totalHeight = size.height
        val trackWidthPx = width.toPx()
        val x = size.width - trackWidthPx - 2.dp.toPx()

        // Subtle track background
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(x, 0f),
            size = Size(trackWidthPx, totalHeight),
            cornerRadius = CornerRadius(trackWidthPx / 2, trackWidthPx / 2)
        )

        val viewHeightRatio = (totalHeight / (totalHeight + state.maxValue)).coerceIn(0.12f, 1f)
        val thumbHeight = (totalHeight * viewHeightRatio).coerceAtLeast(36.dp.toPx())
        val scrollProgress = (state.value.toFloat() / state.maxValue).coerceIn(0f, 1f)
        val thumbY = scrollProgress * (totalHeight - thumbHeight)

        drawRoundRect(
            color = color,
            topLeft = Offset(x, thumbY),
            size = Size(trackWidthPx, thumbHeight),
            cornerRadius = CornerRadius(trackWidthPx / 2, trackWidthPx / 2)
        )
    }
}

/**
 * Draws a visible vertical scrollbar for [LazyListState] (e.g. LazyColumn).
 */
fun Modifier.drawLazyVerticalScrollbar(
    state: LazyListState,
    color: Color = DefaultScrollbarThumbColor,
    trackColor: Color = DefaultScrollbarTrackColor,
    width: Dp = 5.dp
): Modifier = drawWithContent {
    drawContent()
    val layoutInfo = state.layoutInfo
    val totalItemsCount = layoutInfo.totalItemsCount
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    if (totalItemsCount > 0 && visibleItemsInfo.isNotEmpty()) {
        val firstVisible = visibleItemsInfo.first().index
        val visibleCount = visibleItemsInfo.size
        if (visibleCount < totalItemsCount) {
            val totalHeight = size.height
            val trackWidthPx = width.toPx()
            val x = size.width - trackWidthPx - 2.dp.toPx()

            // Subtle track background
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(x, 0f),
                size = Size(trackWidthPx, totalHeight),
                cornerRadius = CornerRadius(trackWidthPx / 2, trackWidthPx / 2)
            )

            val thumbHeightRatio = (visibleCount.toFloat() / totalItemsCount).coerceIn(0.12f, 1f)
            val thumbHeight = (totalHeight * thumbHeightRatio).coerceAtLeast(36.dp.toPx())
            val scrollProgress = (firstVisible.toFloat() / (totalItemsCount - visibleCount).coerceAtLeast(1)).coerceIn(0f, 1f)
            val thumbY = scrollProgress * (totalHeight - thumbHeight)

            drawRoundRect(
                color = color,
                topLeft = Offset(x, thumbY),
                size = Size(trackWidthPx, thumbHeight),
                cornerRadius = CornerRadius(trackWidthPx / 2, trackWidthPx / 2)
            )
        }
    }
}

/**
 * Draws a visible vertical scrollbar for [LazyGridState] (e.g. LazyVerticalGrid).
 */
fun Modifier.drawLazyGridVerticalScrollbar(
    state: LazyGridState,
    color: Color = DefaultScrollbarThumbColor,
    trackColor: Color = DefaultScrollbarTrackColor,
    width: Dp = 5.dp
): Modifier = drawWithContent {
    drawContent()
    val layoutInfo = state.layoutInfo
    val totalItemsCount = layoutInfo.totalItemsCount
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    if (totalItemsCount > 0 && visibleItemsInfo.isNotEmpty()) {
        val firstVisible = visibleItemsInfo.first().index
        val visibleCount = visibleItemsInfo.size
        if (visibleCount < totalItemsCount) {
            val totalHeight = size.height
            val trackWidthPx = width.toPx()
            val x = size.width - trackWidthPx - 2.dp.toPx()

            drawRoundRect(
                color = trackColor,
                topLeft = Offset(x, 0f),
                size = Size(trackWidthPx, totalHeight),
                cornerRadius = CornerRadius(trackWidthPx / 2, trackWidthPx / 2)
            )

            val thumbHeightRatio = (visibleCount.toFloat() / totalItemsCount).coerceIn(0.12f, 1f)
            val thumbHeight = (totalHeight * thumbHeightRatio).coerceAtLeast(36.dp.toPx())
            val scrollProgress = (firstVisible.toFloat() / (totalItemsCount - visibleCount).coerceAtLeast(1)).coerceIn(0f, 1f)
            val thumbY = scrollProgress * (totalHeight - thumbHeight)

            drawRoundRect(
                color = color,
                topLeft = Offset(x, thumbY),
                size = Size(trackWidthPx, thumbHeight),
                cornerRadius = CornerRadius(trackWidthPx / 2, trackWidthPx / 2)
            )
        }
    }
}
