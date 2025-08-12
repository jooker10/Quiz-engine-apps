package futur.apps.composeproject1.appScreens.Scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val expanded = remember {
        mutableStateOf(false)
    }
    TopAppBar(
        modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            .height(48.dp),
        title = {Text(
            "MyApp",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxHeight()
                .wrapContentHeight(align = Alignment.CenterVertically)
        )},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = { IconButton(onClick = {}) {
            Icon(painter = painterResource(id = R.drawable.outline_menu), contentDescription = "navigation icon")
        } } ,
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
            IconButton(onClick = {} ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }

            Box {
                IconButton(onClick = {expanded.value = true}) {
                    Icon(Icons.Default.MoreVert,"more")
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest =  {expanded.value = false}
                )
                {
                    DropdownMenuItem(
                        text = {Text("Refresh")},
                        onClick = {}
                        )
                    DropdownMenuItem(
                        text = {Text("Build")},
                        onClick = {}
                    )
                }
            }

        }


    )
}