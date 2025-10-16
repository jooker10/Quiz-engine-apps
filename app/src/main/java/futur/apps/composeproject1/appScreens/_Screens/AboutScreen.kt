package futur.apps.composeproject1.appScreens._Screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import futur.apps.composeproject1.R

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 App Logo
        Surface(
            shape = CircleShape,
            tonalElevation = 4.dp,
            modifier = Modifier.size(120.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App Logo",
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 App Name
        Text(
            text = "General Knowledge Quiz",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        // 🔹 Version
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Description
        Text(
            text = "Challenge yourself with fun quizzes across science, history, and culture! Improve your knowledge while earning points and tracking progress.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.8f),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Links Section
        AboutLinkCard(
            icon = Icons.Default.PrivacyTip,
            title = "Privacy Policy",
            subtitle = "Read our data protection policy",
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { openLink(context, "https://your-link-here.com/privacy") }
        )

        AboutLinkCard(
            icon = Icons.Default.StarRate,
            title = "Rate the App",
            subtitle = "Leave your feedback on Play Store",
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { openLink(context, "https://play.google.com/store/apps/details?id=your.app.id") }
        )

        AboutLinkCard(
            icon = Icons.Default.Link,
            title = "More Projects",
            subtitle = "Explore other apps by our team",
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { openLink(context, "https://codester.com/yourprofile") }
        )

        AboutLinkCard(
            icon = Icons.Default.Email,
            title = "Contact Us",
            subtitle = "Send us your questions or feedback",
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { openLink(context, "mailto:yourmail@gmail.com") }
        )

        Spacer(modifier = Modifier.height(40.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.8f),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Footer
        Text(
            text = "Developed with ❤️ by SpaceCode",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ---------------- LINK CARD ----------------
@Composable
fun AboutLinkCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    containerColor: Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

// ---------------- LINK HANDLER ----------------
fun openLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}
