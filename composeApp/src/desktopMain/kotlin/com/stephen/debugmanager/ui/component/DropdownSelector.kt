package  com.stephen.debugmanager.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_expand
import com.stephen.debugmanager.utils.DoubleClickUtils
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropdownSelector(
    options: Map<String, String>,
    selectedType: String,
    modifier: Modifier,
    onSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val rotateAnimation by animateFloatAsState(if (expanded) 180f else 0f, label = "expand or collapse")

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable {
                    if (!DoubleClickUtils.isFastDoubleClick())
                        expanded = expanded.not()
                }
                .padding(vertical = 5.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CenterText(
                text = "${options[selectedType]}",
                modifier = Modifier.weight(1f).padding(vertical = 10.dp, horizontal = 5.dp)
            )

            Image(
                modifier = Modifier.size(16.dp).rotate(rotateAnimation),
                painter = painterResource(Res.drawable.ic_expand),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = "Dropdown"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                if (!DoubleClickUtils.isFastDoubleClick())
                    expanded = false
            },
            properties = PopupProperties(usePlatformDefaultWidth = false),
            modifier = modifier.background(MaterialTheme.colorScheme.surface).width(IntrinsicSize.Min)
        ) {
            options.forEach {
                CenterText(it.value, modifier = Modifier.fillMaxWidth(1f).clickable {
                    onSelected(it.key)
                    expanded = false
                }.padding(vertical = 10.dp))
            }
        }
    }
}
