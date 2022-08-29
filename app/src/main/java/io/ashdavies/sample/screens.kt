package io.ashdavies.sample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Screen : Parcelable

@Parcelize
object LoginScreen : Screen

@Parcelize
data class LoggedInScreen(
  val username: String,
) : Screen

@Parcelize
data class ErrorScreen(
  val message: String,
) : Screen
