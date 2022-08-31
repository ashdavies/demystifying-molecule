package io.ashdavies.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
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

class MoleculeViewModel(private val presenter: LoginPresenter) : ViewModel() {
  fun present(events: Flow<LoginUiEvent>): StateFlow<LoginUiModel> =
    viewModelScope.launchMolecule(RecompositionClock.ContextClock) {
      presenter.UiModel(events)
    }
}

@Composable
fun LoginScreen(viewModel: MoleculeViewModel = viewModel()) {
  val events = remember { MutableSharedFlow<LoginUiEvent>() }
  val viewState: LoginUiModel? by viewModel
    .present(events)
    .collectAsState()

  /* ... */
}
