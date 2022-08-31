package io.ashdavies.sample

import io.ashdavies.sample.LoginResult.Failure
import io.ashdavies.sample.LoginResult.Success
import io.ashdavies.sample.LoginUiEvent.Submit
import io.ashdavies.sample.LoginUiModel.Loading
import io.reactivex.Observable

class RxLoginPresenter(val service: SessionServiceImpl, val goTo: (Screen) -> Unit) {
  fun present(events: Observable<LoginUiEvent>): Observable<LoginUiModel> =
    events.flatMap<LoginUiModel> { event ->
      when (event) {
        is Submit -> service.loginSingle(event.username, event.password).toObservable().map { result ->
          when (result) {
            is Failure ->
              goTo(ErrorScreen(result.throwable?.message ?: "Something went wrong"))

            is Success ->
              goTo(LoggedInScreen(event.username))
          }
          Loading
        }.startWith(Loading)
      }
    }.startWith(LoginUiModel.Content)
}
