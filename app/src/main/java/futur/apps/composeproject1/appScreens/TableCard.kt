package futur.apps.composeproject1.appScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import futur.apps.composeproject1.RoomDatabase.Items

@Composable
fun TableCard(item : Items) {
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
            Box(
                modifier = Modifier.padding(8.dp)
                .background(MaterialTheme.colorScheme.primary)
                    .clip(CircleShape),
                )
            { Text(
                text = item.id.toString(),
                modifier = Modifier
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            ) }

            Box(
                modifier = Modifier.padding(8.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            ) {
                Text(
                    text = item.en,
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
                    text = item.fr,
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

        }
    }
}
val verb : Verb = Verb(1,"test","test","test","test","test")
@Preview
@Composable
fun PreviewCard() {
    TableCard(verb)
}