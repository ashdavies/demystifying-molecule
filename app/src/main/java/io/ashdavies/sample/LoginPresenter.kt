package io.ashdavies.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.ashdavies.sample.LoginResult.Failure
import io.ashdavies.sample.LoginResult.Success
import io.ashdavies.sample.LoginUiEvent.Submit
import io.ashdavies.sample.LoginUiModel.Content
import io.ashdavies.sample.LoginUiModel.Loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance

class LoginPresenter(
  private val sessionService: SessionService,
  val goTo: (Screen) -> Unit,
) {
  @Composable
  fun UiModel(events: Flow<LoginUiEvent>): LoginUiModel {
    var login by remember { mutableStateOf<Submit?>(null) }
    LaunchedEffect(events) {
      events.filterIsInstance<Submit>().collect {
        login = it
      }
    }

    return if (login != null) {
      LaunchedEffect(login) {
        when (val result = sessionService.login(login!!.username, login!!.password)) {
          Success -> goTo(LoggedInScreen(login!!.username))
          is Failure -> goTo(ErrorScreen(result.throwable.message ?: "Failed to login"))
        }
      }
      Loading
    } else {
      Content
    }
  }
}
