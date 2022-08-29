package io.ashdavies.sample

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random.Default.nextBoolean
import kotlin.time.Duration.Companion.milliseconds

internal class SessionService {
  fun login(
    username: String,
    password: String
  ): Flow<SessionState> = flow {
    check(username.isNotEmpty() && password.isNotEmpty()) { "Username or password is empty" }

    var progress = 0f

    while (progress < 1) {
      emit(SessionState.Loading(progress))
      delay(200.milliseconds)
      progress += 0.1f
    }

    if (nextBoolean()) {
      emit(SessionState.LoggedIn(username))
    } else {
      val throwable = IllegalStateException("Random failure occurred")
      emit(SessionState.Failure(throwable))
    }
  }
}
