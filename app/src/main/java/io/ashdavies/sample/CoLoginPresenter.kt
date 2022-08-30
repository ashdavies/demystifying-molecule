package io.ashdavies.sample

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

class CoLoginPresenter(
  private val sessionService: SessionService,
  private val goTo: (Screen) -> Unit,
  ) {
  suspend fun present(events: Flow<LoginUiEvent>, emit: (LoginUiModel)->Unit) {
    emit(LoginUiModel.Content)
    val loginEvent = events.filterIsInstance<LoginUiEvent.Submit>().first()
    emit(LoginUiModel.Loading)

    val result = sessionService.login(loginEvent.username, loginEvent.password)

    when (result) {
      is LoginResult.Success -> goTo(LoggedInScreen(loginEvent.username))
      is LoginResult.Failure -> goTo(ErrorScreen(result.throwable?.message ?: "Hmm"))
    }
  }
}
