package com.adriandeleon.template.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriandeleon.template.logger.domain.Logger
import org.koin.compose.koinInject

@Composable
fun HomeView(
    component: HomeComponent,
    modifier: Modifier = Modifier,
    logger: Logger = koinInject(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(32.dp),
    ) {
        Text(component.title, fontSize = 24.sp)

        Button(
            onClick = { logger.info { "Button clicked" } },
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Log Message")
        }
    }
}
