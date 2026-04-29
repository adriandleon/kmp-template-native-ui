package com.adriandeleon.kmp.template.posts

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun PostsView(component: PostsComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    when (val s = state) {
        is PostsUiState.Loading -> PostsLoadingContent(modifier)
        is PostsUiState.Content -> PostsListContent(s.posts, modifier)
        is PostsUiState.Error -> PostsErrorContent(s.message, component::onRetry, modifier)
    }
}

@Composable
private fun PostsLoadingContent(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.tag_posts_loading)),
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PostsListContent(posts: List<PostUiModel>, modifier: Modifier = Modifier) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.tag_posts_list)),
    ) {
        items(posts, key = { it.id }) { post ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${stringResource(R.string.tag_posts_item)}_${post.id}"),
            ) {
                Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                Text(text = post.body, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PostsErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.tag_posts_error)),
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag(stringResource(R.string.tag_posts_retry_button)),
        ) {
            Text(stringResource(R.string.posts_retry_button))
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(name = "Posts Loading – Light – EN", locale = "en")
@Preview(name = "Posts Loading – Light – ES", locale = "es")
@Preview(name = "Posts Loading – Light – PT", locale = "pt-rBR")
@Preview(name = "Posts Loading – Dark – EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostsLoadingPreview() {
    MaterialTheme {
        PostsView(PreviewPostsComponent().also { it.setState(PostsUiState.Loading) })
    }
}

@Preview(name = "Posts Content – Light – EN", locale = "en")
@Preview(name = "Posts Content – Light – ES", locale = "es")
@Preview(name = "Posts Content – Light – PT", locale = "pt-rBR")
@Preview(name = "Posts Content – Dark – EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostsContentPreview() {
    MaterialTheme { PostsView(PreviewPostsComponent()) }
}

@Preview(name = "Posts Error – Light – EN", locale = "en")
@Preview(name = "Posts Error – Light – ES", locale = "es")
@Preview(name = "Posts Error – Light – PT", locale = "pt-rBR")
@Preview(name = "Posts Error – Dark – EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostsErrorPreview() {
    MaterialTheme {
        PostsView(PreviewPostsComponent().also {
            it.setState(PostsUiState.Error("Something went wrong."))
        })
    }
}
