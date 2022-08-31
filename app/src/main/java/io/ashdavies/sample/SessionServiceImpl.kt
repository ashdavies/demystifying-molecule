package io.ashdavies.sample

import io.reactivex.Single
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Not really that useful but used for demonstrating state combination
 */
sealed interface SessionStatus {
  object Ready : SessionStatus
  object Active : SessionStatus
  object Ended : SessionStatus
}

sealed class LoginResult {
  data class Failure(val throwable: Throwable) : LoginResult()
  object Success : LoginResult()
}

interface SessionService {
  fun sessionStatus(): Flow<SessionStatus>

  fun loginSingle(username: String, password: String): Single<LoginResult>

  suspend fun login(username: String, password: String): LoginResult
}

class SessionServiceImpl : SessionService {
  private val status = MutableStateFlow<SessionStatus>(SessionStatus.Ready)
  private val random = Random(System.currentTimeMillis())

  override fun sessionStatus(): Flow<SessionStatus> = status

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
