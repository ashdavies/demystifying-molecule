package io.ashdavies.sample

import app.cash.molecule.RecompositionClock
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import io.ashdavies.sample.FakeSessionService.LoginAttempt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoginPresenterTest {
  @Test
  fun immediate() = runBlocking {
    val goTos = Channel<Screen>(UNLIMITED)
    val sessionService = FakeSessionService()
    val events = MutableSharedFlow<LoginUiEvent>()
    val username = "username"
    val password = "password"

    val presenter = LoginPresenter(sessionService, goTos::trySend)
    println("1")
    moleculeFlow(RecompositionClock.Immediate) {
      presenter.UiModel(events)
    }.test {
      // Fire up initial LaunchedEffects (if any)
      yield()
      assertEquals(LoginUiModel.Content, awaitItem())
      events.emit(LoginUiEvent.Submit(username, password))

      assertEquals(LoginUiModel.Loading, awaitItem())
      assertEquals(LoginAttempt(username, password), sessionService.loginAttempts.awaitValue())
      sessionService.loginResults.trySend(LoginResult.Success)

      println("7")
      assertEquals(LoggedInScreen(username), goTos.awaitValue())
      println("8")
    }
  }
}
