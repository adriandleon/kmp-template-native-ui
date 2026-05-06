package com.adriandeleon.kmp.template.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.theme.TemplateTheme
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingView(component: OnboardingComponent, modifier: Modifier = Modifier) {
    val uiState by component.uiState.subscribeAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.pageCount })

    LaunchedEffect(uiState.selectedIndex) {
        if (pagerState.currentPage != uiState.selectedIndex) {
            pagerState.animateScrollToPage(uiState.selectedIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page != component.uiState.value.selectedIndex) {
                    component.selectPage(page)
                }
            }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag(stringResource(R.string.tag_onboarding_screen)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(
            onClick = component::skip,
            modifier =
                Modifier.align(Alignment.End)
                    .testTag(stringResource(R.string.tag_onboarding_skip_button)),
        ) {
            Text(stringResource(R.string.onboarding_skip_button))
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { pageIndex ->
            OnboardingPageContent(
                page = OnboardingComponent.Page.entries[pageIndex],
                modifier = Modifier.fillMaxSize(),
            )
        }

        OnboardingControls(uiState = uiState, component = component)
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingComponent.Page,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = page.title(),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag(stringResource(R.string.tag_onboarding_title)),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = page.body(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag(stringResource(R.string.tag_onboarding_body)),
        )
    }
}

@Composable
private fun OnboardingControls(
    uiState: OnboardingComponent.UiState,
    component: OnboardingComponent,
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier =
                Modifier.fillMaxWidth()
                    .testTag(stringResource(R.string.tag_onboarding_page_indicator)),
        ) {
            repeat(uiState.pageCount) { index ->
                val color =
                    if (index == uiState.selectedIndex) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                Surface(
                    color = color,
                    modifier =
                        Modifier.padding(horizontal = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape),
                    content = {},
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = component::previous,
                enabled = uiState.canGoPrevious,
                modifier =
                    Modifier.weight(1f)
                        .testTag(stringResource(R.string.tag_onboarding_previous_button)),
            ) {
                Text(stringResource(R.string.onboarding_previous_button))
            }
            Button(
                onClick = component::finish,
                modifier =
                    Modifier.weight(1f)
                        .testTag(stringResource(R.string.tag_onboarding_next_button)),
            ) {
                Text(
                    stringResource(
                        if (uiState.isLastPage) {
                            R.string.onboarding_finish_button
                        } else {
                            R.string.onboarding_next_button
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun OnboardingComponent.Page.title(): String =
    when (this) {
        OnboardingComponent.Page.Welcome -> stringResource(R.string.onboarding_welcome_title)
        OnboardingComponent.Page.Organize -> stringResource(R.string.onboarding_organize_title)
        OnboardingComponent.Page.Customize -> stringResource(R.string.onboarding_customize_title)
    }

@Composable
private fun OnboardingComponent.Page.body(): String =
    when (this) {
        OnboardingComponent.Page.Welcome -> stringResource(R.string.onboarding_welcome_body)
        OnboardingComponent.Page.Organize -> stringResource(R.string.onboarding_organize_body)
        OnboardingComponent.Page.Customize -> stringResource(R.string.onboarding_customize_body)
    }

@Preview(name = "Onboarding - Light - EN", locale = "en")
@Preview(name = "Onboarding - Light - ES", locale = "es-r419")
@Preview(name = "Onboarding - Light - PT", locale = "pt-rBR")
@Preview(name = "Onboarding - Dark - EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Onboarding - Dark - ES", locale = "es-r419", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Onboarding - Dark - PT", locale = "pt-rBR", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OnboardingPreview() {
    TemplateTheme { OnboardingView(PreviewOnboardingComponent()) }
}
