package io.ashdavies.sample

import io.reactivex.Single
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random

sealed class LoginResult {
  data class Failure(val throwable: Throwable): LoginResult()
  object Success : LoginResult()
}

interface SessionService {
  fun loginSingle(username: String, password: String): Single<LoginResult>

  suspend fun login(username: String, password: String): LoginResult
}

class SessionServiceImpl : SessionService{
  private val random = Random(System.currentTimeMillis())
  override fun loginSingle(username: String, password: String): Single<LoginResult> =
    Single.fromCallable {
      if (random.nextBoolean()) {
        LoginResult.Success
      } else {
        LoginResult.Failure(IllegalStateException("Random failure occurred"))
      }
    }
      .delay(2000, TimeUnit.MILLISECONDS)

  override suspend fun login(
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
