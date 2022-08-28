package io.ashdavies.sample

internal sealed interface SessionState {

    data class LoggedOut(
        val username: String? = null,
        val password: String? = null,
    ) : SessionState

    data class Loading(
        val progress: Float,
    ) : SessionState

    data class LoggedIn(
        val username: String
    ) : SessionState

    data class Failure(
        val cause: Throwable,
    ) : SessionState
}
