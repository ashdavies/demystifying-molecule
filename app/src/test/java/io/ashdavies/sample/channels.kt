package io.ashdavies.sample

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout

suspend fun <T> Channel<T>.awaitValue() = withTimeout(1000) {
  receive()
}
