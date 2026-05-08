package com.adriandeleon.kmp.template.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.theme.TemplateTheme
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun HomeView(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    val uiState by component.uiState.subscribeAsState()

    if (uiState.isReady) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .testTag(stringResource(R.string.tag_home_screen)),
        ) {
            Text(
                text = stringResource(R.string.home_screen_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.home_screen_body),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "Home - Light - EN", locale = "en")
@Preview(name = "Home - Light - ES", locale = "es-r419")
@Preview(name = "Home - Light - PT", locale = "pt-rBR")
@Preview(name = "Home - Dark - EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Home - Dark - ES", locale = "es-r419", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Home - Dark - PT", locale = "pt-rBR", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomePreview() {
    TemplateTheme { HomeView(PreviewHomeComponent()) }
}
