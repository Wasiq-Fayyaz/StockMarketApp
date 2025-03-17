package com.plcoding.stockmarketapp.presentation.company_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlin.math.roundToInt
import android.graphics.*

import android.graphics.Paint
import androidx.compose.foundation.Canvas




@Composable
fun StockChart(
    infos: List<IntradayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Green
) {

    val spacing = 100f
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }
    val upperValue = remember {
        (infos.maxOfOrNull { it.close }?.plus(1))?.roundToInt() ?: 0
    }

    val lowerValue = remember(infos) {
        infos.minOfOrNull { it.close }?.toInt() ?: 0
    }

    val density = LocalDensity.current


    val textPaint = remember(density) {
        Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }

    }

    Canvas(modifier = modifier){
        val spacePerHour = (size.width - spacing) / infos.size
    }
}