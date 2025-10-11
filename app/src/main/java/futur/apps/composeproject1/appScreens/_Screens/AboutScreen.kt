package futur.apps.composeproject1.appScreens._Screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import futur.apps.composeproject1.R

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 App Logo
        Image(
            painter = painterResource(id = R.drawable.app_icon), // TODO: replace with your logo
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 App Name
        Text(
            text = "General Knowledge Quiz", // TODO: change to your app name
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // 🔹 Version
        Text(
            text = "Version 1.0.0", // TODO: update version if needed
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Description
        Text(
            text = "A fun and educational quiz app that challenges your general knowledge across science, history, and more!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Divider(modifier = Modifier.fillMaxWidth(0.8f))

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Links Section
        AboutLinkItem(
            title = "Privacy Policy",
            subtitle = "Read our data protection policy",
            onClick = { openLink(context, "https://your-link-here.com/privacy") }
        )

        AboutLinkItem(
            title = "Rate the App",
            subtitle = "Leave your feedback on Play Store",
            onClick = { openLink(context, "https://play.google.com/store/apps/details?id=your.app.id") }
        )

        AboutLinkItem(
            title = "More Projects",
            subtitle = "Explore other apps by our team",
            onClick = { openLink(context, "https://codester.com/yourprofile") }
        )

        AboutLinkItem(
            title = "Contact Us",
            subtitle = "Send us your questions or feedback",
            onClick = { openLink(context, "mailto:yourmail@gmail.com") }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Divider(modifier = Modifier.fillMaxWidth(0.8f))

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Footer
        Text(
            text = "Developed by Anouar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AboutLinkItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(0.9f))
    }
}

fun openLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
