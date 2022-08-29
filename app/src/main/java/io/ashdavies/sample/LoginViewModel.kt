package io.ashdavies.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
        password = password
      )
    }
  }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
  val viewState: LoginResult? = viewModel.viewState
}
