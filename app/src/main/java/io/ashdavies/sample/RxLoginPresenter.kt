package io.ashdavies.sample

import io.ashdavies.sample.LoginResult.Failure
import io.ashdavies.sample.LoginResult.Success
import io.ashdavies.sample.LoginUiEvent.Submit
import io.ashdavies.sample.LoginUiModel.Loading
import io.reactivex.Observable

class RxLoginPresenter(
  private val sessionService: SessionService,
  private val goTo: (Screen) -> Unit,
) {
  fun present(events: Observable<LoginUiEvent>): Observable<LoginUiModel> {
    return events.flatMap<LoginUiModel> { event ->
      when (event) {
        is Submit -> {
          sessionService.loginSingle(event.username, event.password)
            .toObservable()
            .map { result ->
              when (result) {
                is Success -> {
                  goTo(LoggedInScreen(event.username))
                }
                is Failure -> {
                  goTo(ErrorScreen(result.throwable?.message ?: "Something went wrong"))
                }
              }
              Loading
            }
            .startWith(Loading)
        }
      }
    }
      .startWith(LoginUiModel.Content)
  }
}
