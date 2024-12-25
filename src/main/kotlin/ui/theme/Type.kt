package ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val defaultText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Center,
)

val infoText = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val pageTitleText = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    color = fontPrimaryColor
)

val groupTitleText = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
)

val itemKeyText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left,
    color = fontPrimaryColor
)

val itemValueText = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
    color = fontPrimaryColor
)