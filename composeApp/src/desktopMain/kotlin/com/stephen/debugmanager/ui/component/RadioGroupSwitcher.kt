package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RadioGroupSwitcher(
    nameValueMap: Map<StringResource, Int>,
    currentTheme: Int,
    modifier: Modifier = Modifier,
    onThemeChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface)
            .padding(5.dp)
    ) {
        nameValueMap.forEach {
            val itemNameResource = it.key
            val itemValue = it.value
            CenterText(
                text = stringResource(itemNameResource),
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (currentTheme == itemValue) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable {
                        onThemeChange(itemValue)
                    }
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            )
        }
    }
}
