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

public val Icons.Locked: ImageVector
    get() {
        if (_locked != null) {
            return _locked!!
        }
        _locked =
            Builder(
                    name = "Locked",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 28.0.dp,
                    viewportWidth = 148.54f,
                    viewportHeight = 180.3f,
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
                        moveTo(73.26f, 180.19f)
                        curveToRelative(-19.07f, -7.45f, -29.47f, -13.48f, -40.7f, -23.59f)
                        curveToRelative(-17.36f, -15.63f, -28.06f, -35.42f, -31.5f, -58.26f)
                        curveTo(0.02f, 91.39f, -0.06f, 88.31f, 0.02f, 56.84f)
                        lineToRelative(0.08f, -30.26f)
                        lineToRelative(3.57f, -1.27f)
                        curveToRelative(1.97f, -0.7f, 7.26f, -2.6f, 11.77f, -4.23f)
                        curveToRelative(18.81f, -6.79f, 21.66f, -7.82f, 26.46f, -9.53f)
                        curveTo(52.12f, 7.91f, 70.02f, 1.45f, 72.11f, 0.66f)
                        curveToRelative(2.09f, -0.79f, 2.19f, -0.8f, 3.31f, -0.41f)
                        curveToRelative(1.34f, 0.47f, 26.32f, 9.47f, 40.71f, 14.67f)
                        curveToRelative(19.73f, 7.12f, 29.0f, 10.46f, 30.69f, 11.04f)
                        lineToRelative(1.72f, 0.59f)
                        verticalLineToRelative(32.52f)
                        curveToRelative(0.0f, 35.93f, 0.04f, 34.92f, -1.75f, 43.9f)
                        curveToRelative(-4.2f, 21.1f, -14.53f, 39.18f, -30.67f, 53.64f)
                        curveToRelative(-10.26f, 9.2f, -21.57f, 16.05f, -35.69f, 21.63f)
                        curveToRelative(-4.67f, 1.85f, -6.31f, 2.29f, -7.17f, 1.96f)
                        moveToRelative(21.74f, -58.38f)
                        curveToRelative(1.63f, -0.8f, 2.79f, -1.95f, 3.72f, -3.7f)
                        curveToRelative(0.6f, -1.12f, 0.6f, -1.12f, 0.6f, -15.67f)
                        reflectiveCurveToRelative(0.0f, -14.55f, -0.6f, -15.67f)
                        curveToRelative(-1.33f, -2.5f, -3.53f, -4.14f, -5.99f, -4.47f)
                        lineToRelative(-1.15f, -0.16f)
                        lineToRelative(-0.13f, -5.06f)
                        curveToRelative(-0.15f, -5.63f, -0.48f, -7.17f, -2.22f, -10.51f)
                        curveToRelative(-1.91f, -3.66f, -5.62f, -6.75f, -9.69f, -8.07f)
                        curveToRelative(-1.83f, -0.59f, -2.66f, -0.71f, -5.22f, -0.72f)
                        curveToRelative(-2.65f, -0.01f, -3.31f, 0.08f, -5.16f, 0.72f)
                        curveToRelative(-5.53f, 1.9f, -9.34f, 5.82f, -11.19f, 11.49f)
                        curveToRelative(-0.58f, 1.79f, -0.68f, 2.63f, -0.79f, 7.12f)
                        curveToRelative(-0.13f, 5.09f, -0.13f, 5.09f, -0.75f, 5.09f)
                        curveToRelative(-0.34f, 0.0f, -1.3f, 0.26f, -2.13f, 0.57f)
                        curveToRelative(-2.02f, 0.75f, -3.8f, 2.54f, -4.55f, 4.55f)
                        curveToRelative(-0.55f, 1.48f, -0.57f, 1.86f, -0.57f, 14.96f)
                        curveToRelative(0.0f, 14.58f, 0.03f, 14.91f, 1.47f, 16.98f)
                        curveToRelative(0.93f, 1.35f, 2.32f, 2.38f, 3.95f, 2.95f)
                        curveToRelative(1.22f, 0.43f, 2.83f, 0.46f, 20.11f, 0.4f)
                        curveToRelative(18.79f, -0.06f, 18.79f, -0.06f, 20.29f, -0.8f)
                        moveTo(65.32f, 77.74f)
                        curveToRelative(0.0f, -5.11f, 0.26f, -6.33f, 1.82f, -8.4f)
                        curveToRelative(3.44f, -4.57f, 9.95f, -4.91f, 13.78f, -0.72f)
                        curveToRelative(2.07f, 2.27f, 2.39f, 3.45f, 2.39f, 8.96f)
                        verticalLineToRelative(4.62f)
                        horizontalLineTo(65.32f)
                        close()
                    }
                }
                .build()
        return _locked!!
    }

private var _locked: ImageVector? = null

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Locked, contentDescription = null)
    }
}
