package io.ashdavies.sample

import io.reactivex.Single
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED

class FakeSessionService : SessionService {
  data class LoginAttempt(val username: String, val password: String)

  val loginResults = Channel<LoginResult>(UNLIMITED)
  val loginAttempts = Channel<LoginAttempt>(UNLIMITED)

  override fun loginSingle(
    username: String,
    password: String
  ): Single<LoginResult> {
    TODO("Not yet implemented")
  }

  override suspend fun login(
    username: String,
    password: String
  ): LoginResult {
    loginAttempts.trySend(LoginAttempt(username, password))

    return loginResults.awaitValue()
  }
}
