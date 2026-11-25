package fr.uha.hassenforder.easy.memorial.icons.outlined

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val Icons.Outlined.Candle: ImageVector
  get() {
    if (_candle != null) {
      return _candle!!
    }
    _candle =
      ImageVector.Builder(
          name = "candle",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero,
          ) {
            moveTo(6f, 20f)
            horizontalLineTo(18f)
            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
            quadTo(19f, 19.43f, 19f, 19f)
            horizontalLineTo(5f)
            quadToRelative(0f, 0.43f, 0.29f, 0.71f)
            reflectiveQuadTo(6f, 20f)
            close()
            moveTo(10f, 7.16f)
            quadTo(9.2f, 6.32f, 9.25f, 5.13f)
            quadTo(9.3f, 3.82f, 10.16f, 2.84f)
            reflectiveQuadTo(12f, 1f)
            quadToRelative(0.98f, 0.85f, 1.84f, 1.84f)
            reflectiveQuadToRelative(0.91f, 2.29f)
            quadTo(14.8f, 6.32f, 14f, 7.16f)
            reflectiveQuadTo(12f, 8f)
            reflectiveQuadTo(10f, 7.16f)
            close()
            moveTo(11f, 17f)
            horizontalLineToRelative(2f)
            verticalLineTo(11f)
            horizontalLineTo(11f)
            verticalLineToRelative(6f)
            close()
            moveTo(12.54f, 5.77f)
            quadTo(12.75f, 5.55f, 12.75f, 5.22f)
            quadToRelative(0f, -0.43f, -0.24f, -0.77f)
            reflectiveQuadTo(12f, 3.77f)
            quadTo(11.73f, 4.1f, 11.49f, 4.45f)
            reflectiveQuadTo(11.25f, 5.22f)
            quadToRelative(0f, 0.33f, 0.21f, 0.55f)
            reflectiveQuadTo(12f, 6f)
            reflectiveQuadTo(12.54f, 5.77f)
            close()
            moveToRelative(8.25f, 11.01f)
            quadTo(21f, 16.58f, 21f, 16.25f)
            reflectiveQuadTo(20.79f, 15.71f)
            reflectiveQuadTo(20.25f, 15.5f)
            quadToRelative(-0.32f, 0f, -0.54f, 0.21f)
            reflectiveQuadTo(19.5f, 16.25f)
            reflectiveQuadToRelative(0.21f, 0.54f)
            reflectiveQuadTo(20.25f, 17f)
            reflectiveQuadToRelative(0.54f, -0.21f)
            close()
            moveTo(18f, 22f)
            horizontalLineTo(6f)
            quadTo(4.75f, 22f, 3.88f, 21.13f)
            reflectiveQuadTo(3f, 19f)
            verticalLineTo(17f)
            horizontalLineTo(9f)
            verticalLineTo(11f)
            quadTo(9f, 10.17f, 9.59f, 9.59f)
            reflectiveQuadTo(11f, 9f)
            horizontalLineToRelative(2f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(15f, 11f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(2.6f)
            quadTo(17.55f, 16.8f, 17.53f, 16.63f)
            reflectiveQuadTo(17.5f, 16.25f)
            quadToRelative(0f, -1.15f, 0.8f, -1.95f)
            reflectiveQuadToRelative(1.95f, -0.8f)
            reflectiveQuadToRelative(1.95f, 0.8f)
            reflectiveQuadTo(23f, 16.25f)
            quadToRelative(0f, 0.95f, -0.56f, 1.68f)
            reflectiveQuadTo(21f, 18.9f)
            verticalLineTo(19f)
            quadToRelative(0f, 1.25f, -0.88f, 2.13f)
            reflectiveQuadTo(18f, 22f)
            close()
            moveTo(12f, 20f)
            close()
            moveTo(11f, 17f)
            horizontalLineToRelative(2f)
            horizontalLineTo(11f)
            close()
            moveTo(12f, 4.9f)
            close()
          }
        }
        .build()
    return _candle!!
  }

private var _candle: ImageVector? = null
