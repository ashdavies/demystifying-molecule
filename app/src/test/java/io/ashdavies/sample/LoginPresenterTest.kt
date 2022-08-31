package io.ashdavies.sample

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.MonotonicFrameClock
import app.cash.molecule.RecompositionClock
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import io.ashdavies.sample.FakeSessionService.LoginAttempt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.junit.Test
import kotlin.test.assertEquals

internal class LoginPresenterTest {
  @Test
  fun withContextClock() = runBlocking {
    val goTos = Channel<Screen>(UNLIMITED)
    val sessionService = FakeSessionService()
    val events = MutableSharedFlow<LoginUiEvent>()
    val username = "username"
    val password = "password"
    val clock = BroadcastFrameClock()

    val presenter = LoginPresenter(sessionService, goTos::trySend)
    withContext(clock) {
      moleculeFlow(RecompositionClock.ContextClock) {
        presenter.UiModel(events)
      }.test {
        // Fire up initial LaunchedEffects (if any)
        yield()
        assertEquals(LoginUiModel.Content, awaitItem())
        events.emit(LoginUiEvent.Submit(username, password))
        // push event into composition, wait for recomposer to request
        // a new frame
        yield(); yield();
        clock.sendFrame(0)

        assertEquals(LoginUiModel.Loading, awaitItem())
        assertEquals(LoginAttempt(username, password), sessionService.loginAttempts.awaitValue())
        sessionService.loginResults.trySend(LoginResult.Success)

        assertEquals(LoggedInScreen(username), goTos.awaitValue())
      }
    }
  }

  @Test
  fun works() = runBlocking {
    val goTos = Channel<Screen>(UNLIMITED)
    val sessionService = FakeSessionService()
    val events = MutableSharedFlow<LoginUiEvent>()
    val username = "username"
    val password = "password"

    val presenter = LoginPresenter(sessionService, goTos::trySend)
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

      assertEquals(LoggedInScreen(username), goTos.awaitValue())
    }
  }
}
