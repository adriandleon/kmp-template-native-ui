package com.adriandeleon.kmp.template.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.main.examples.ExamplesView
import com.adriandeleon.kmp.template.theme.TemplateTheme
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun MainView(component: MainComponent, modifier: Modifier = Modifier) {
    val uiState by component.uiState.subscribeAsState()

    Scaffold(
        modifier = modifier.fillMaxSize().testTag(stringResource(R.string.tag_main_screen)),
        bottomBar = {
            NavigationBar {
                MainComponent.Page.entries.forEach { page ->
                    NavigationBarItem(
                        selected = uiState.selectedPage == page,
                        onClick = { component.selectPage(page) },
                        icon = {
                            Icon(
                                painter = painterResource(page.iconRes()),
                                contentDescription = null,
                            )
                        },
                        label = { Text(page.tabLabel()) },
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ChildPages(
                pages = component.pages,
                onPageSelected = component::selectPage,
                modifier = Modifier.fillMaxSize(),
            ) { _, pageComponent ->
                when (pageComponent) {
                    is MainComponent.PageComponent.Examples ->
                        ExamplesView(pageComponent.component)
                    is MainComponent.PageComponent.Generic ->
                        MainPageContent(pageComponent.page)
                }
            }
        }
    }
}

@Composable
private fun MainPageContent(page: MainComponent.Page) {
    Box(
        modifier =
            Modifier.fillMaxSize()
                .padding(24.dp)
                .testTag(stringResource(R.string.tag_main_page_content)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = page.title(), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = page.body(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun MainComponent.Page.tabLabel(): String =
    when (this) {
        MainComponent.Page.Home -> stringResource(R.string.main_home_tab)
        MainComponent.Page.Examples -> stringResource(R.string.main_examples_tab)
        MainComponent.Page.Adaptive -> stringResource(R.string.main_adaptive_tab)
        MainComponent.Page.Settings -> stringResource(R.string.main_settings_tab)
    }

@Composable
private fun MainComponent.Page.title(): String =
    when (this) {
        MainComponent.Page.Home -> stringResource(R.string.main_home_title)
        MainComponent.Page.Examples -> stringResource(R.string.main_examples_title)
        MainComponent.Page.Adaptive -> stringResource(R.string.main_adaptive_title)
        MainComponent.Page.Settings -> stringResource(R.string.main_settings_title)
    }

@Composable
private fun MainComponent.Page.body(): String =
    when (this) {
        MainComponent.Page.Home -> stringResource(R.string.main_home_body)
        MainComponent.Page.Examples -> stringResource(R.string.main_examples_body)
        MainComponent.Page.Adaptive -> stringResource(R.string.main_adaptive_body)
        MainComponent.Page.Settings -> stringResource(R.string.main_settings_body)
    }

private fun MainComponent.Page.iconRes(): Int =
    when (this) {
        MainComponent.Page.Home -> R.drawable.ic_tab_home
        MainComponent.Page.Examples -> R.drawable.ic_tab_examples
        MainComponent.Page.Adaptive -> R.drawable.ic_tab_adaptive
        MainComponent.Page.Settings -> R.drawable.ic_tab_settings
    }

@Preview(name = "Main - Light - EN", locale = "en")
@Preview(name = "Main - Light - ES", locale = "es-r419")
@Preview(name = "Main - Light - PT", locale = "pt-rBR")
@Preview(name = "Main - Dark - EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Main - Dark - ES", locale = "es-r419", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Main - Dark - PT", locale = "pt-rBR", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainPreview() {
    TemplateTheme { MainView(PreviewMainComponent()) }
}
