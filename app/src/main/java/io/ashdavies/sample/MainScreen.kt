package io.ashdavies.sample

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

val cursiveTextStyle = TextStyle(
  fontFamily = FontFamily.Cursive,
  fontSize = 40.sp,
)

@Composable
@Preview(showBackground = true)
internal fun MainScreen(modifier: Modifier = Modifier) {
  var currentScreen by rememberSaveable { mutableStateOf<Screen>(LoginScreen) }
  val sessionService = remember { SessionService() }

  val goTo: (Screen) -> Unit = { nextScreen -> currentScreen = nextScreen }

  when (val screen = currentScreen) {
    is ErrorScreen -> ErrorView(screen.message, goTo)
    is LoggedInScreen -> LoggedInView(screen.username, goTo)
    is LoginScreen -> {
      val events = remember { MutableSharedFlow<LoginUiEvent>(extraBufferCapacity = 50) }
      val uiModel = LoginPresenter(sessionService).UiModel(events, goTo)
      LoginView(uiModel, events::tryEmit)
    }
  }
}

sealed class LoginUiModel {
  object Loading : LoginUiModel()

  object Content: LoginUiModel()
}

sealed class LoginUiEvent {
  data class Submit(val username: String, val password: String): LoginUiEvent()
}

class LoginPresenter(private val sessionService: SessionService) {
  @Composable
  fun UiModel(events: Flow<LoginUiEvent>, goTo: (Screen)->Unit): LoginUiModel {
    var login by remember { mutableStateOf<LoginUiEvent.Submit?>(null) }
    LaunchedEffect(events) {
      events.filterIsInstance<LoginUiEvent.Submit>().collect {
        login = it
      }
    }

    return if (login != null) {
      LaunchedEffect(login) {
        when (val result = sessionService.login(login!!.username, login!!.password)) {
          LoginResult.Success -> goTo(LoggedInScreen(login!!.username))
          is LoginResult.Failure -> goTo(ErrorScreen(result.throwable.message ?: "Failed to login"))
        }
      }
      LoginUiModel.Loading
    } else {
      LoginUiModel.Content
    }
  }
}

@Composable
private fun LoginView(model: LoginUiModel, onEvent: (LoginUiEvent)->Unit) {
  when (model) {
    is LoginUiModel.Loading -> ProgressView()
    is LoginUiModel.Content -> {
      LoginInputView(onSubmit = { login ->
        onEvent(LoginUiEvent.Submit(login.username, login.password) )
      })
    }
  }
}

@Composable
private fun MonolithicLoginView(
  sessionService: SessionService,
  goTo: (Screen) -> Unit,
) {
  val username = remember { mutableStateOf("") }
  val password = remember { mutableStateOf("") }
  var click by remember { mutableStateOf<Int?>(null) }

  if (click == null) {
    MonolithicLoginInputView(username, password, onSubmit = { click = (click ?: 0) + 1 })
  } else {
    LaunchedEffect(click) {
      when (val result = sessionService.login(username.value, password.value)) {
        LoginResult.Success -> goTo(LoggedInScreen(username.value))
        is LoginResult.Failure -> goTo(ErrorScreen(result.throwable.message ?: "Failed to login"))
      }
    }
    ProgressView()
  }
}

data class LoginInfo(val username: String, val password: String)

@Composable
private fun LoginInputView(
  onSubmit: (LoginInfo)->Unit,
) {
  val username = remember { mutableStateOf("") }
  val password = remember { mutableStateOf("") }

  Column(modifier = Modifier.padding(all = 48.dp)) {
    Text(style = cursiveTextStyle, text = "Login")
    RhythmSpacer()

    LabeledTextField(state = username, hidden = false, label = "Username")
    RhythmSpacer()
    LabeledTextField(state = password, hidden = true, label = "Password")
    RhythmSpacer()
    TextButton({ onSubmit(LoginInfo(username.value, password.value)) }, "Login")
  }
}

@Composable
private fun MonolithicLoginInputView(
  username: MutableState<String>,
  password: MutableState<String>,
  onSubmit: () -> Unit,
) {
  Column(modifier = Modifier.padding(all = 48.dp)) {
    Text(style = cursiveTextStyle, text = "Login")
    RhythmSpacer()

    LabeledTextField(state = username, hidden = false, label = "Username")
    RhythmSpacer()
    LabeledTextField(state = password, hidden = true, label = "Password")
    RhythmSpacer()
    TextButton(onSubmit, "Login")
  }
}

@Composable
private fun RhythmSpacer() {
  Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun TextButton(
  onClick: () -> Unit,
  text: String,
) {
  val height = 48.dp
  Button(
    content = { Text(text = text) },
    shape = RoundedCornerShape(height),
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .height(height)
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LabeledTextField(
  state: MutableState<String>,
  hidden: Boolean,
  label: String,
) {
  OutlinedTextField(
    onValueChange = { state.value = it },
    label = { Text(text = label) },
    value = state.value,
    keyboardOptions = if (hidden) KeyboardOptions(keyboardType = KeyboardType.Password)
    else KeyboardOptions.Default,
    visualTransformation = if (hidden) PasswordVisualTransformation()
    else VisualTransformation.None,
  )
}

@Composable
private fun ProgressView() {
  Box(Modifier.fillMaxSize()) {
    CircularProgressIndicator(
      Modifier.align(Alignment.Center),
    )
  }
}

@Composable
private fun LoggedInView(
  username: String,
  goTo: (Screen) -> Unit,
) {
  Column(modifier = Modifier.padding(all = 48.dp)) {
    Text(text = "Hello, $username!", Modifier.align(Alignment.CenterHorizontally))
    RhythmSpacer()
    TextButton(onClick = { goTo(LoginScreen) }, text = "Logout")
  }
}

@Composable
private fun ErrorView(
  message: String,
  goTo: (Screen) -> Unit,
) {
  Column(modifier = Modifier.padding(all = 48.dp)) {
    Text(message)
    RhythmSpacer()
    TextButton(onClick = { goTo(LoginScreen) }, text = "Try again?")
  }
}
