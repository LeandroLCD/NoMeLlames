package cl.blipblipcode.prefixsapp.ui.widget.icons

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

public val Icons.Logo: ImageVector
    get() {
        if (_logo != null) {
            return _logo!!
        }
        _logo =
            Builder(
                    name = "Logo",
                    defaultWidth = 102.0.dp,
                    defaultHeight = 128.0.dp,
                    viewportWidth = 101.76f,
                    viewportHeight = 127.5f,
                )
                .apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                        stroke = null,
                        strokeLineWidth = 0.0f,
                        strokeLineCap = Butt,
                        strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f,
                        pathFillType = NonZero,
                    ) {
                        moveTo(47.1f, 126.31f)
                        curveToRelative(-6.79f, -2.51f, -14.99f, -7.12f, -20.36f, -11.43f)
                        curveToRelative(-2.78f, -2.23f, -7.33f, -6.72f, -9.86f, -9.72f)
                        curveTo(9.5f, 96.39f, 3.77f, 84.32f, 1.58f, 72.92f)
                        curveTo(0.29f, 66.17f, 0.31f, 66.68f, 0.15f, 42.38f)
                        lineTo(0.0f, 19.29f)
                        lineToRelative(1.72f, -0.65f)
                        arcToRelative(16264.0f, 16264.0f, 0.0f, false, true, 30.56f, -11.52f)
                        curveToRelative(1.89f, -0.7f, 6.87f, -2.6f, 11.07f, -4.2f)
                        lineTo(50.98f, 0.0f)
                        lineTo(53.8f, 1.08f)
                        curveToRelative(1.55f, 0.59f, 4.01f, 1.53f, 5.47f, 2.08f)
                        curveToRelative(1.46f, 0.55f, 8.36f, 3.17f, 15.35f, 5.82f)
                        arcToRelative(31722.0f, 31722.0f, 0.0f, false, false, 19.18f, 7.28f)
                        curveToRelative(3.57f, 1.35f, 6.82f, 2.59f, 7.22f, 2.75f)
                        lineToRelative(0.74f, 0.29f)
                        lineToRelative(-0.1f, 23.09f)
                        curveToRelative(-0.08f, 19.81f, -0.16f, 23.47f, -0.53f, 25.77f)
                        curveToRelative(-1.49f, 9.2f, -3.54f, 15.83f, -7.19f, 23.25f)
                        curveToRelative(-8.02f, 16.3f, -22.18f, 28.83f, -39.65f, 35.09f)
                        curveToRelative(-1.57f, 0.56f, -3.12f, 1.02f, -3.44f, 1.01f)
                        curveToRelative(-0.32f, -0.01f, -2.01f, -0.54f, -3.76f, -1.19f)
                        moveToRelative(32.94f, -75.92f)
                        curveToRelative(0.0f, -0.15f, -1.99f, -2.26f, -4.43f, -4.69f)
                        lineToRelative(-4.43f, -4.42f)
                        lineToRelative(-11.92f, 11.83f)
                        curveToRelative(-6.56f, 6.51f, -12.66f, 12.54f, -13.56f, 13.41f)
                        lineTo(44.05f, 68.1f)
                        lineToRelative(-6.6f, -6.6f)
                        lineToRelative(-6.6f, -6.6f)
                        lineToRelative(-0.83f, 0.79f)
                        curveToRelative(-1.47f, 1.38f, -7.51f, 7.43f, -7.9f, 7.9f)
                        curveToRelative(-0.34f, 0.42f, 0.64f, 1.48f, 10.85f, 11.68f)
                        lineToRelative(11.23f, 11.23f)
                        lineTo(62.11f, 68.58f)
                        curveToRelative(9.86f, -9.86f, 17.93f, -18.05f, 17.93f, -18.19f)
                    }
                }
                .build()
        return _logo!!
    }

private var _logo: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Logo, contentDescription = null)
    }
}
