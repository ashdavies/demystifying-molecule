package io.ashdavies.sample

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.random.Random.Default.nextBoolean
import kotlin.time.Duration.Companion.milliseconds

sealed class LoginResult {
  data class Failure(val throwable: Throwable): LoginResult()
  object Success : LoginResult()
}

internal class SessionService {
  private val random = Random(System.currentTimeMillis())
  suspend fun login(
    username: String,
    password: String
  ): LoginResult {
    check(username.isNotEmpty() && password.isNotEmpty()) { "Username or password is empty" }

    delay(2000)

    return if (random.nextBoolean()) {
      LoginResult.Success
    } else {
      LoginResult.Failure(IllegalStateException("Random failure occurred"))
    }
  }
}
