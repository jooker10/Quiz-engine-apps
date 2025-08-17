package futur.apps.composeproject1.appScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import futur.apps.composeproject1.utils.CategoryType

@Composable
fun TableCard(category : CategoryType, onSpeakClick : () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        )
    ) {
        Column() {
            Row(
                modifier = Modifier.padding(8.dp)
                .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
                )
            { Text(
                text = category.id.toString(),
               modifier = Modifier.padding(8.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
                IconButton(
                    onClick = {onSpeakClick()}
                    ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "play icon")
                }
            }

            Box(
                modifier = Modifier.padding(8.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            ) {
                Text(
                    text = category.en,
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Box(
                modifier = Modifier.padding(8.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            ) {
                Text(
                    text = category.fr,
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

        }
    }
}
