package at.fhooe.sail.cas.ui.composables.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.viewmodel.features.PoiFeature
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/** amber used for the star rating, like on common map apps */
private val RatingAmber = Color(0xFFF9AB00)

/**
 * Bottom sheet with details of the selected POI (google-maps style):
 * icon, name, category, star rating and a short description.
 * Shown instead of the walk recorder while a POI is selected.
 */
@Composable
fun PoiDetailSheet(
    modifier: Modifier = Modifier,
    poi: PoiFeature,
    onClose: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // decorative drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Image(
                    bitmap = poi.icon.asImageBitmap(),
                    contentDescription = poi.category,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = poi.name.ifBlank { poi.id },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = prettyCategory(poi.category),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RatingRow(rating = poi.rating, reviewCount = mockReviewCount(poi.id))
                }
                Icon(
                    painter = painterResource(R.drawable.close_24dp),
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = poi.description.ifBlank { "Point of interest in Hagenberg." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RatingRow(rating: Float, reviewCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = String.format(Locale.getDefault(), "%.1f", rating),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(6.dp))
        val filledStars: Int = rating.roundToInt().coerceIn(0, 5)
        repeat(5) { i ->
            Icon(
                painter = painterResource(R.drawable.star_24dp),
                contentDescription = null,
                tint = if (i < filledStars) RatingAmber else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "($reviewCount)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** "place_of_worship" -> "Place of worship" */
private fun prettyCategory(category: String): String =
    category.ifBlank { "poi" }
        .replace('_', ' ')
        .replaceFirstChar { it.uppercase() }

/** stable mock review count derived from the POI id */
private fun mockReviewCount(id: String): Int = abs(id.hashCode()) % 240 + 12

@Preview
@Composable
private fun PoiDetailSheetPreview() {
    CASProjectTheme {
        PoiDetailSheet(
            poi = PoiFeature(
                id = "gpkg-42",
                name = "Schlosskirche",
                category = "place_of_worship",
                description = "Historic church with regular services and quiet surroundings.",
                rating = 4.6f
            )
        )
    }
}
