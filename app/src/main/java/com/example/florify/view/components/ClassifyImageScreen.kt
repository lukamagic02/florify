package com.example.florify.view.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.florify.R
import com.example.florify.model.Classification
import com.example.florify.model.MyImageClassifier
import com.example.florify.viewmodel.ClassificationState
import com.example.florify.viewmodel.ImageViewModel

@Composable
fun ClassifyImageScreen(
    context: Context,
    viewModel: ImageViewModel,
    navController: NavHostController
) {

    val image by viewModel.image.collectAsStateWithLifecycle()
    val classificationState by viewModel.classificationState.collectAsStateWithLifecycle()

    // Perform classification when image changes
    LaunchedEffect(image) {
        image?.let {
            viewModel.setClassificationState(ClassificationState.Loading)
            try {
                val classifier = MyImageClassifier(context)
                val results = classifier.classify(it)
                viewModel.setClassificationState(ClassificationState.Success(results))
            } catch (e: Exception) {
                viewModel.setClassificationState(
                    ClassificationState.Error(e.message ?: context.getString(R.string.error_generic))
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = classificationState) {
            is ClassificationState.Loading -> {
                LoadingView()
            }

            is ClassificationState.Success -> {
                ResultsView(
                    image = image,
                    results = state.results,
                    onBackClick = {
                        viewModel.storeImage(null)
                        viewModel.setClassificationState(ClassificationState.Idle)
                        navController.navigate("pick_image")
                    }
                )
            }

            is ClassificationState.Error -> {
                ErrorView(
                    message = state.message,
                    onBackClick = {
                        viewModel.storeImage(null)
                        viewModel.setClassificationState(ClassificationState.Idle)
                        navController.navigate("pick_image")
                    }
                )
            }

            else -> {
                // Idle state
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.classifying_image),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ResultsView(
    image: android.graphics.Bitmap?,
    results: List<Classification>,
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Image display
        image?.let {
            Card(
                modifier = Modifier.padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Image(
                    modifier = Modifier
                        .heightIn(max = 250.dp)
                        .widthIn(max = 250.dp)
                        .padding(8.dp),
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(R.string.captured_image_desc)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results title
        Text(
            text = stringResource(R.string.classification_results),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ranked result cards
        results.take(3).forEachIndexed { index, classification ->
            ResultCard(
                classification = classification,
                rank = index + 1
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back button
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.action_back))
        }
    }
}

@Composable
fun ResultCard(
    classification: Classification,
    rank: Int
) {
    val (elevation, titleStyle, badgeColor, badgeIcon) = when (rank) {
        1 -> Tuple4(
            8.dp,
            MaterialTheme.typography.titleLarge,
            MaterialTheme.colorScheme.primary,
            Icons.Filled.EmojiEvents
        )
        2 -> Tuple4(
            4.dp,
            MaterialTheme.typography.titleMedium,
            MaterialTheme.colorScheme.secondary,
            Icons.Filled.WorkspacePremium
        )
        else -> Tuple4(
            2.dp,
            MaterialTheme.typography.titleSmall,
            MaterialTheme.colorScheme.tertiary,
            Icons.Filled.Star
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge icon
            Icon(
                imageVector = badgeIcon,
                contentDescription = when (rank) {
                    1 -> stringResource(R.string.rank_first)
                    2 -> stringResource(R.string.rank_second)
                    else -> stringResource(R.string.rank_third)
                },
                tint = badgeColor,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Flower name
                Text(
                    text = classification.category,
                    style = titleStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Confidence percentage
                Text(
                    text = String.format("%.1f%%", classification.score * 100),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Visual progress bar
                LinearProgressIndicator(
                    progress = classification.score.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = badgeColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBackClick) {
            Text(stringResource(R.string.action_back))
        }
    }
}

// Helper data class for destructuring
private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)