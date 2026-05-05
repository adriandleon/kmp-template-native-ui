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

        PanelsShowcaseView(
            component = component,
            state = state,
            mode = state.panelsMode,
            hasDetails = state.hasPanelDetails,
            hasExtra = state.hasPanelExtra,
        )

        GenericNavigationShowcaseView(component = component, state = state)

        DeepLinkShowcaseView(component = component, state = state)
    }
}

@Composable
private fun PanelsShowcaseView(
    component: ExamplesComponent,
    state: ExamplesComponent.UiState,
    mode: ExamplesComponent.PanelMode,
    hasDetails: Boolean,
    hasExtra: Boolean,
) {
    val selectedItemId = state.selectedItemId ?: state.itemIds.firstOrNull()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.examples_panels_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.examples_panels_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.examples_panels_mode_format, mode.name),
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { component.setPanelsMode(ExamplesComponent.PanelMode.SINGLE) },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_panels_single_mode))
            }
            OutlinedButton(
                onClick = { component.setPanelsMode(ExamplesComponent.PanelMode.DUAL) },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_panels_dual_mode))
            }
            OutlinedButton(
                onClick = { component.setPanelsMode(ExamplesComponent.PanelMode.TRIPLE) },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_panels_triple_mode))
            }
        }

        Text(
            text = stringResource(R.string.examples_panels_main_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.examples_panels_main_body),
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { selectedItemId?.let(component::openPanelDetails) },
                enabled = selectedItemId != null,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_panels_open_details_button))
            }
            OutlinedButton(
                onClick = { selectedItemId?.let(component::openPanelExtra) },
                enabled = selectedItemId != null,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_panels_open_extra_button))
            }
        }

        Text(
            text =
                if (hasDetails) {
                    stringResource(
                        R.string.examples_panels_details_body_format,
                        state.panelItemId.orEmpty(),
                    )
                } else {
                    stringResource(R.string.examples_panels_details_empty)
                },
            style = MaterialTheme.typography.bodySmall,
        )
        if (hasDetails) {
            TextButton(onClick = component::dismissPanelDetails) {
                Text(stringResource(R.string.examples_panels_dismiss_details_button))
            }
        }

        Text(
            text =
                if (hasExtra) {
                    stringResource(
                        R.string.examples_panels_extra_body_format,
                        state.panelItemId.orEmpty(),
                    )
                } else {
                    stringResource(R.string.examples_panels_extra_empty)
                },
            style = MaterialTheme.typography.bodySmall,
        )
        if (hasExtra) {
            TextButton(onClick = component::dismissPanelExtra) {
                Text(stringResource(R.string.examples_panels_dismiss_extra_button))
            }
        }
    }
}

@Composable
private fun GenericNavigationShowcaseView(
    component: ExamplesComponent,
    state: ExamplesComponent.UiState,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.examples_generic_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.examples_generic_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text =
                stringResource(
                    R.string.examples_generic_active_format,
                    state.activeWorkspacePaneId.orEmpty(),
                ),
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.workspacePaneIds.forEach { paneId ->
                OutlinedButton(
                    onClick = { component.activateWorkspacePane(paneId) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(paneId)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = component::addWorkspacePane,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_generic_add_button))
            }
            OutlinedButton(
                onClick = { state.activeWorkspacePaneId?.let(component::closeWorkspacePane) },
                enabled = state.activeWorkspacePaneId != null && state.workspacePaneIds.size > 1,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_generic_close_button))
            }
        }
    }
}

@Composable
private fun DeepLinkShowcaseView(
    component: ExamplesComponent,
    state: ExamplesComponent.UiState,
) {
    val lastDeepLinkPath = state.lastDeepLinkPath

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.examples_deeplink_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.examples_deeplink_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { component.handleDeepLink("template://examples/item/sample-3") },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_deeplink_item_button))
            }
            OutlinedButton(
                onClick = { component.handleDeepLink("template://examples/panel/sample-2") },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_deeplink_panel_button))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { component.handleDeepLink("template://examples/workspace/pane-2") },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_deeplink_workspace_button))
            }
            TextButton(
                onClick = { component.handleDeepLink("template://examples/confirmation") },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.examples_deeplink_modal_button))
            }
        }
        Text(
            text =
                if (lastDeepLinkPath == null) {
                    stringResource(R.string.examples_deeplink_empty)
                } else {
                    stringResource(
                        R.string.examples_deeplink_status_format,
                        lastDeepLinkPath,
                        state.lastDeepLinkHandled.toString(),
                    )
                },
            style = MaterialTheme.typography.bodySmall,
        )
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
