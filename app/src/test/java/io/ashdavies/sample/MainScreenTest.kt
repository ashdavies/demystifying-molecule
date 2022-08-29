package io.ashdavies.sample

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    moleculeFlow(RecompositionClock.Immediate) {
      var count by remember { mutableStateOf(0) }
      LaunchedEffect(Unit) {
        while (true) {
          delay(100)
          count++
        }
      }
      count
    }.test {
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
