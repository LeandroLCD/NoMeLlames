package cl.blipblipcode.prefixsapp.specialbottombar.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class SpecialBottom {

    data class Badge(
        val backgroundColor: Color = Color.White,
        val textColor: Color = Color.Black,
        val text: String? = null
    ) : SpecialBottom()

    data class Id(val key: String) : SpecialBottom()

    sealed interface Icon{
        data class Drawable(
            @DrawableRes val icon: Int,
            @DrawableRes val activatedIcon: Int
        ) : Icon
        data class Vector(
            val icon: ImageVector,
            val activatedIcon: ImageVector
        ) : Icon
    }

    data class Item(
        val icon: Icon,
        val tag: String,
        val id: Id,
        val badge: Badge? = null
    ) : SpecialBottom()

    data class Theme(
        val backgroundColor: Color = Color.White,
        val selectedColor: Color = Color.Black,
        val unselectedColor: Color = Color.Gray,
        val shadowColor: Color = selectedColor,
        val spread: Dp = 8.dp,
        val blurRadius: Dp = 16.dp,
        val borderRadius: Dp = 32.dp,
        val startAnimation: Boolean = true,
        @DrawableRes val iconPlus: Int? = null,
        val backGroundSecondaryColor: Color = backgroundColor,
    ) : SpecialBottom()
}

