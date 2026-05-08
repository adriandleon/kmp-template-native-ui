package com.adriandeleon.kmp.template.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.examples.ExamplesView
import com.adriandeleon.kmp.template.home.HomeView
import com.adriandeleon.kmp.template.posts.PostsView
import com.adriandeleon.kmp.template.settings.SettingsView
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
                    is MainComponent.PageComponent.Home ->
                        HomeView(pageComponent.component)
                    is MainComponent.PageComponent.Examples ->
                        ExamplesView(pageComponent.component)
                    is MainComponent.PageComponent.Posts ->
                        PostsView(pageComponent.component)
                    is MainComponent.PageComponent.Settings ->
                        SettingsView(pageComponent.component)
                }
            }
        }
    }
}

@Composable
private fun MainComponent.Page.tabLabel(): String =
    when (this) {
        MainComponent.Page.Home -> stringResource(R.string.main_home_tab)
        MainComponent.Page.Examples -> stringResource(R.string.main_examples_tab)
        MainComponent.Page.Posts -> stringResource(R.string.main_posts_tab)
        MainComponent.Page.Settings -> stringResource(R.string.main_settings_tab)
    }

private fun MainComponent.Page.iconRes(): Int =
    when (this) {
        MainComponent.Page.Home -> R.drawable.ic_tab_home
        MainComponent.Page.Examples -> R.drawable.ic_tab_examples
        MainComponent.Page.Posts -> R.drawable.ic_tab_posts
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
