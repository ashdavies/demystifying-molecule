package io.ashdavies.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
@Preview(showBackground = true)
internal fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(20.dp)
    ) {
        var _sessionState by rememberSaveable { mutableStateOf<SessionState>(SessionState.LoggedOut()) }
        val sessionService = remember { SessionService() }
        val coroutineScope = rememberCoroutineScope()

        when (val sessionState = _sessionState) {
            is SessionState.Failure -> FailureScreen(sessionState.cause.message ?: defaultFailureMessage())
            is SessionState.Loading -> ProgressIndicator(sessionState.progress)
            is SessionState.LoggedIn -> LoggedInScreen(sessionState.username)
            is SessionState.LoggedOut -> LoginScreen(
                onValueChange = { username, password -> _sessionState = SessionState.LoggedOut(username, password) },
                state = sessionState,
            ) {
                val username = requireNotNull(sessionState.username) { "Username should not be null" }
                val password = requireNotNull(sessionState.password) { "Password should not be null" }

                sessionService
                    .login(username, password)
                    .onEach { _sessionState = it }
                    .launchIn(coroutineScope)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LoginScreen(
    state: SessionState.LoggedOut,
    onValueChange: (username: String?, password: String?) -> Unit,
    onLoginClick: () -> Unit,
) {
    Text(
        style = TextStyle(
            fontFamily = FontFamily.Cursive,
            fontSize = 40.sp,
        ),
        text = "Login"
    )

    Spacer(modifier = Modifier.height(20.dp))

    OutlinedTextField(
        onValueChange = { onValueChange(it, state.password) },
        label = { Text(text = "Username") },
        value = state.username ?: "",
    )

    Spacer(modifier = Modifier.height(20.dp))

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = { onValueChange(state.username, it) },
        label = { Text(text = "Password") },
        value = state.password ?: "",
    )

    Spacer(modifier = Modifier.height(20.dp))

    Box(modifier = Modifier.padding(start = 40.dp)) {
        Button(
            content = { Text(text = "Login") },
            shape = RoundedCornerShape(50.dp),
            onClick = { onLoginClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}

@Composable
private fun ProgressIndicator(progress: Float, modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.fillMaxSize(),
        progress = progress,
    )
}

@Composable
private fun LoggedInScreen(username: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $username",
        modifier = modifier,
    )
}

@Composable
private fun FailureScreen(message: String, modifier: Modifier = Modifier) {
    Text(message, modifier)
}

private fun defaultFailureMessage() = "Something went wrong, that's all we know"
