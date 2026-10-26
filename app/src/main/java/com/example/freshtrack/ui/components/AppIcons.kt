package com.example.freshtrack.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {

    val Home: ImageVector = icon("Home") {
        moveTo(12f, 3f)
        lineTo(2f, 12f)
        horizontalLineTo(5f)
        verticalLineTo(21f)
        horizontalLineTo(10f)
        verticalLineTo(15f)
        horizontalLineTo(14f)
        verticalLineTo(21f)
        horizontalLineTo(19f)
        verticalLineTo(12f)
        horizontalLineTo(22f)
        close()
    }

    val Products: ImageVector = icon("Products") {
        moveTo(4f, 5f)
        horizontalLineTo(20f)
        verticalLineTo(8f)
        horizontalLineTo(4f)
        close()
        moveTo(4f, 10.5f)
        horizontalLineTo(20f)
        verticalLineTo(13.5f)
        horizontalLineTo(4f)
        close()
        moveTo(4f, 16f)
        horizontalLineTo(20f)
        verticalLineTo(19f)
        horizontalLineTo(4f)
        close()
    }

    val Stats: ImageVector = icon("Stats") {
        moveTo(4f, 20f)
        verticalLineTo(12f)
        horizontalLineTo(8f)
        verticalLineTo(20f)
        close()
        moveTo(10f, 20f)
        verticalLineTo(4f)
        horizontalLineTo(14f)
        verticalLineTo(20f)
        close()
        moveTo(16f, 20f)
        verticalLineTo(9f)
        horizontalLineTo(20f)
        verticalLineTo(20f)
        close()
    }

    val Add: ImageVector = icon("Add") {
        moveTo(11f, 4f)
        horizontalLineTo(13f)
        verticalLineTo(11f)
        horizontalLineTo(20f)
        verticalLineTo(13f)
        horizontalLineTo(13f)
        verticalLineTo(20f)
        horizontalLineTo(11f)
        verticalLineTo(13f)
        horizontalLineTo(4f)
        verticalLineTo(11f)
        horizontalLineTo(11f)
        close()
    }

    val Back: ImageVector = icon("Back") {
        moveTo(20f, 11f)
        horizontalLineTo(7.83f)
        lineTo(13.41f, 5.41f)
        lineTo(12f, 4f)
        lineTo(4f, 12f)
        lineTo(12f, 20f)
        lineTo(13.41f, 18.59f)
        lineTo(7.83f, 13f)
        horizontalLineTo(20f)
        close()
    }

    val Close: ImageVector = icon("Close") {
        moveTo(19f, 6.41f)
        lineTo(17.59f, 5f)
        lineTo(12f, 10.59f)
        lineTo(6.41f, 5f)
        lineTo(5f, 6.41f)
        lineTo(10.59f, 12f)
        lineTo(5f, 17.59f)
        lineTo(6.41f, 19f)
        lineTo(12f, 13.41f)
        lineTo(17.59f, 19f)
        lineTo(19f, 17.59f)
        lineTo(13.41f, 12f)
        close()
    }

    private fun icon(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black), pathBuilder = pathBuilder).build()
}
