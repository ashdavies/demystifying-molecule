package io.ashdavies.sample

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

class CoLoginPresenter(val service: SessionServiceImpl, val goTo: (Screen) -> Unit) {
  suspend fun present(events: Flow<LoginUiEvent>, emit: (LoginUiModel) -> Unit) {
    emit(LoginUiModel.Content)
    val loginEvent = events.filterIsInstance<LoginUiEvent.Submit>().first()
    emit(LoginUiModel.Loading)

    val result = service.login(loginEvent.username, loginEvent.password)

    when (result) {
      is LoginResult.Success -> goTo(LoggedInScreen(loginEvent.username))
      is LoginResult.Failure -> goTo(ErrorScreen(result.throwable?.message ?: "Hmm"))
    }
  }
}

interface ConnectivityManager {
  fun isActive(): Flow<Boolean>
}

class BigCombinePresenter(
  private val connectivity: ConnectivityManager,
  private val session: SessionService,
  private val goTo: (Screen) -> Unit,
) {
  suspend fun present(events: Flow<LoginUiEvent>, emit: (LoginUiModel) -> Unit) {
    combine(connectivity.isActive(), session.sessionStatus(), events) { isActive, status, event ->
      if (isActive) {
        emit(LoginUiModel.Content)
        val loginEvent = events.filterIsInstance<LoginUiEvent.Submit>().first()
        emit(LoginUiModel.Loading)

        if (status == SessionStatus.Active) {
          /** ... */
        }

      } else { /** ... */ }
    }
  }
}
