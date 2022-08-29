package io.ashdavies.sample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import app.cash.molecule.RecompositionClock
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainScreenTest {

  @Test
  fun immediate() = runTest {
    val events = moleculeFlow(RecompositionClock.Immediate) {
      val count by produceState(0) {
        while (true) {
          delay(timeMillis = 100)
          value++
        }
      }
      count
    }

    events.test {
      assertEquals(0, awaitItem())

      advanceTimeBy(100)
      assertEquals(1, awaitItem())

      advanceTimeBy(300)
      assertEquals(2, awaitItem())
      assertEquals(3, awaitItem())
      assertEquals(5, awaitItem())
    }
  }
}
