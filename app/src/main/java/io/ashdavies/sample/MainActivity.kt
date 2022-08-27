package io.ashdavies.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.ashdavies.sample.ui.theme.DemystifyingMoleculeTheme

internal abstract class ComposeActivity(private val content: @Composable () -> Unit) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(content = content)
    }
}

internal class MainActivity : ComposeActivity({
    DemystifyingMoleculeTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            content = { Greeting("Android") },
            modifier = Modifier.fillMaxSize(),
        )
    }
})

@Composable
private fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
@Preview(showBackground = true)
private fun DefaultPreview() {
    DemystifyingMoleculeTheme {
        Greeting("Android")
    }
}
