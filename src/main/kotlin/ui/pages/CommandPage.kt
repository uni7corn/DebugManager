package ui.pages

import MainStateHolder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.koin.core.context.GlobalContext
import ui.component.BasePage
import ui.component.CenterText
import ui.component.CommonButton
import ui.component.WrappedEditText
import ui.theme.groupBackGroundColor
import ui.theme.groupTitleText

@Composable
fun CommandPage() {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    var adbCommand by remember { mutableStateOf("") }

    BasePage("命令模式") {
        Column(
            modifier = Modifier.fillMaxWidth(1f).padding(bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp)).background(groupBackGroundColor)
                .padding(10.dp)
        ) {
            CenterText(
                "ADB",
                modifier = Modifier.padding(bottom = 10.dp),
                style = groupTitleText
            )
            Row {
                WrappedEditText(
                    value = adbCommand,
                    tipText = "输入adb命令",
                    onValueChange = { adbCommand = it },
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                )
                CommonButton(
                    "执行", onClick = {
                        mainStateHolder.executeAdbCommand(adbCommand)
                    },
                    modifier = Modifier.padding(10.dp).width(150.dp)
                )
            }
        }
    }
}
