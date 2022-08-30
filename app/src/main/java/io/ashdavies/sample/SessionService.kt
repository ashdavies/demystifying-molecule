package io.ashdavies.sample

import io.reactivex.Single
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.Random.Default.nextBoolean
import kotlin.time.Duration.Companion.milliseconds

sealed class LoginResult {
  data class Failure(val throwable: Throwable): LoginResult()
  object Success : LoginResult()
}

class SessionService {
  private val random = Random(System.currentTimeMillis())
  fun loginSingle(username: String, password: String): Single<LoginResult> =
    Single.fromCallable {
      if (random.nextBoolean()) {
        LoginResult.Success
      } else {
        LoginResult.Failure(IllegalStateException("Random failure occurred"))
      }
    }
      .delay(2000, TimeUnit.MILLISECONDS)

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
