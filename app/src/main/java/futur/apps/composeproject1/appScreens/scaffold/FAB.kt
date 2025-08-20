package futur.apps.composeproject1.appScreens.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun FAB() {
    FloatingActionButton(
        onClick = { /* Handle FAB click */ },
        elevation = FloatingActionButtonDefaults.elevation(
            8.dp),
        shape = FloatingActionButtonDefaults.smallShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape),
        content = { Icon(Icons.Default.Add, contentDescription = "Add") }
    )
}