package io.ashdavies.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.launch

/**
 * ViewModel to demonstrate traditional architecture view state modelling
 */
class LoginViewModel(private val service: SessionService) : ViewModel() {

  var viewState by mutableStateOf<LoginResult?>(null)
    private set

  fun login(username: String, password: String) {
    viewModelScope.launch {
      viewState = service.login(
        username = username,
        password = password,
      )
    }
  }
}

class MoleculeViewModel(private val service: SessionService) : ViewModel() {
  val viewState = viewModelScope.launchMolecule(RecompositionClock.ContextClock) {
    val serviceResult by produceState<LoginResult?>(null) {
      value = service.login(TODO(/* username */), TODO(/* password */))
    }
    serviceResult
  }
}

@Composable
fun LoginScreen(viewModel: MoleculeViewModel = viewModel()) {
  val viewState: LoginResult? by viewModel.viewState.collectAsState()
  /* ... */
}
