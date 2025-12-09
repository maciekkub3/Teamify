import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import androidx.core.net.toUri

@Composable
fun ProfileImage(
    name: String?,
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    if (!profileImageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = profileImageUrl.toUri(),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(CircleShape)

        )
    } else {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name?.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
