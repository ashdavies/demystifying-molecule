package io.ashdavies.sample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal sealed class SessionState : Parcelable {

    data class LoggedOut(
        val username: String? = null,
        val password: String? = null,
    ) : SessionState()

    data class Loading(
        val progress: Float,
    ) : SessionState()

    data class LoggedIn(
        val username: String
    ) : SessionState()

    data class Failure(
        val cause: Throwable,
    ) : SessionState()
}
