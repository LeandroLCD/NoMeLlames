package dev.andresfelipecaicedo.nomellames.ui.widget.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

public val Icons.CallLog: ImageVector
    get() {
        if (_callLog != null) {
            return _callLog!!
        }
        _callLog =
            Builder(
                    name = "CallLog",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                )
                .apply {
                    path(
                        fill = SolidColor(Color(0xFFe3e3e3)),
                        stroke = null,
                        strokeLineWidth = 0.0f,
                        strokeLineCap = Butt,
                        strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f,
                        pathFillType = NonZero,
                    ) {
                        moveTo(480.0f, 160.0f)
                        verticalLineToRelative(-80.0f)
                        horizontalLineToRelative(400.0f)
                        verticalLineToRelative(80.0f)
                        close()
                        moveTo(480.0f, 320.0f)
                        verticalLineToRelative(-80.0f)
                        horizontalLineToRelative(400.0f)
                        verticalLineToRelative(80.0f)
                        close()
                        moveTo(480.0f, 480.0f)
                        verticalLineToRelative(-80.0f)
                        horizontalLineToRelative(400.0f)
                        verticalLineToRelative(80.0f)
                        close()
                        moveTo(758.0f, 880.0f)
                        quadToRelative(-125.0f, 0.0f, -247.0f, -54.5f)
                        reflectiveQuadTo(289.0f, 671.0f)
                        reflectiveQuadTo(134.5f, 449.0f)
                        reflectiveQuadTo(80.0f, 202.0f)
                        quadToRelative(0.0f, -18.0f, 12.0f, -30.0f)
                        reflectiveQuadToRelative(30.0f, -12.0f)
                        horizontalLineToRelative(162.0f)
                        quadToRelative(14.0f, 0.0f, 25.0f, 9.5f)
                        reflectiveQuadToRelative(13.0f, 22.5f)
                        lineToRelative(26.0f, 140.0f)
                        quadToRelative(2.0f, 16.0f, -1.0f, 27.0f)
                        reflectiveQuadToRelative(-11.0f, 19.0f)
                        lineToRelative(-97.0f, 98.0f)
                        quadToRelative(20.0f, 37.0f, 47.5f, 71.5f)
                        reflectiveQuadTo(347.0f, 614.0f)
                        quadToRelative(31.0f, 31.0f, 65.0f, 57.5f)
                        reflectiveQuadToRelative(72.0f, 48.5f)
                        lineToRelative(94.0f, -94.0f)
                        quadToRelative(9.0f, -9.0f, 23.5f, -13.5f)
                        reflectiveQuadTo(630.0f, 610.0f)
                        lineToRelative(138.0f, 28.0f)
                        quadToRelative(14.0f, 4.0f, 23.0f, 14.5f)
                        reflectiveQuadToRelative(9.0f, 23.5f)
                        verticalLineToRelative(162.0f)
                        quadToRelative(0.0f, 18.0f, -12.0f, 30.0f)
                        reflectiveQuadToRelative(-30.0f, 12.0f)
                        moveTo(201.0f, 400.0f)
                        lineToRelative(66.0f, -66.0f)
                        lineToRelative(-17.0f, -94.0f)
                        horizontalLineToRelative(-89.0f)
                        quadToRelative(5.0f, 41.0f, 14.0f, 81.0f)
                        reflectiveQuadToRelative(26.0f, 79.0f)
                        moveToRelative(358.0f, 358.0f)
                        quadToRelative(39.0f, 17.0f, 79.5f, 27.0f)
                        reflectiveQuadToRelative(81.5f, 13.0f)
                        verticalLineToRelative(-88.0f)
                        lineToRelative(-94.0f, -19.0f)
                        close()
                        moveTo(559.0f, 758.0f)
                    }
                }
                .build()
        return _callLog!!
    }

private var _callLog: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.CallLog, contentDescription = null)
    }
}
