package com.adriandeleon.kmp.template.main.examples

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun ExamplesView(component: ExamplesComponent, modifier: Modifier = Modifier) {
    val modalSlot by component.modalSlot.subscribeAsState()

    Children(
        stack = component.stack,
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag(stringResource(R.string.tag_examples_screen)),
    ) { child ->
        when (val instance = child.instance) {
            is ExamplesComponent.Child.List -> ExamplesListView(component)
            is ExamplesComponent.Child.Detail -> ExamplesDetailView(component, instance.component)
        }
    }

    if (modalSlot.child?.instance is ExamplesComponent.ModalChild.Confirmation) {
        AlertDialog(
            onDismissRequest = component::dismissConfirmation,
            title = { Text(stringResource(R.string.examples_confirmation_title)) },
            text = { Text(stringResource(R.string.examples_confirmation_body)) },
            confirmButton = {
                TextButton(onClick = component::dismissConfirmation) {
                    Text(stringResource(R.string.examples_confirmation_close_button))
                }
            },
        )
    }
}

@Composable
private fun ExamplesListView(component: ExamplesComponent) {
    val state by component.state.subscribeAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.examples_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.examples_body),
            style = MaterialTheme.typography.bodyMedium,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = component::addItem,
                modifier = Modifier.weight(1f).testTag(stringResource(R.string.tag_examples_add_button)),
            ) {
                Text(stringResource(R.string.examples_add_item_button))
            }
            OutlinedButton(
                onClick = component::showConfirmation,
                modifier =
                    Modifier.weight(1f)
                        .testTag(stringResource(R.string.tag_examples_modal_button)),
            ) {
                Text(stringResource(R.string.examples_show_modal_button))
            }
        }

        state.itemIds.forEach { itemId ->
            SampleItemRow(
                component = component.itemComponent(itemId),
                isSelected = state.selectedItemId == itemId,
                onSelect = { component.selectItem(itemId) },
                onOpenDetail = { component.openDetail(itemId) },
                onRemove = { component.removeItem(itemId) },
            )
        }
    }
}

@Composable
private fun SampleItemRow(
    component: SampleItemComponent,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onOpenDetail: () -> Unit,
    onRemove: () -> Unit,
) {
    val state by component.state.subscribeAsState()

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .testTag(stringResource(R.string.tag_examples_item)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text =
                stringResource(
                    R.string.examples_item_title_format,
                    state.title,
                    state.count,
                ),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text =
                if (isSelected) {
                    stringResource(R.string.examples_item_selected)
                } else {
                    stringResource(R.string.examples_item_not_selected)
                },
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onSelect, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.examples_select_item_button))
            }
            OutlinedButton(onClick = component::increment, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.examples_increment_item_button))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onOpenDetail, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.examples_open_detail_button))
            }
            TextButton(onClick = onRemove, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.examples_remove_item_button))
            }
        }
    }
}

@Composable
private fun ExamplesDetailView(
    component: ExamplesComponent,
    detailComponent: ExamplesComponent.DetailComponent,
) {
    val sampleComponent = component.itemComponent(detailComponent.itemId)
    val sampleState by sampleComponent.state.subscribeAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = component::back) {
            Text(stringResource(R.string.examples_back_button))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.examples_detail_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text =
                stringResource(
                    R.string.examples_detail_body_format,
                    sampleState.title,
                    sampleState.count,
                ),
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = sampleComponent::increment) {
            Text(stringResource(R.string.examples_increment_item_button))
        }
    }
}

@Preview(name = "Examples - Light - EN", locale = "en")
@Preview(name = "Examples - Light - ES", locale = "es-r419")
@Preview(name = "Examples - Light - PT", locale = "pt-rBR")
@Preview(name = "Examples - Dark - EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Examples - Dark - ES", locale = "es-r419", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Examples - Dark - PT", locale = "pt-rBR", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExamplesPreview() {
    MaterialTheme { ExamplesView(PreviewExamplesComponent()) }
}
