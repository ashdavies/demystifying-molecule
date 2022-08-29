package io.ashdavies.sample

import app.cash.molecule.RecompositionClock
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainScreenTest {

  @Test
  fun counting() = runTest {
    moleculeFlow(clock = RecompositionClock.Immediate) { 42 }.test {
      assertEquals(42, awaitItem())
      awaitComplete()
    }
  }
}
