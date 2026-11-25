package rb.com.mbanking.presentation.kaspresso.scenarios.login

import com.agoda.kakao.screen.Screen
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import rb.com.mbanking.presentation.kaspresso.screens.OnBoardingScreen

/**
 * Закрытие баннера после входа в авторизованную зоне
 */
class ClosePushBannerScenario : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        Screen.idle(1000L)
        compose {
            or(OnBoardingScreen.closePushButton) {
                isDisplayed()
            } then {
                step("Закрываем предложение подключить PUSH") {
                    OnBoardingScreen {
                        closePushButton.click()
                    }
                }
            }
            or(OnBoardingScreen.continuePushButton) {
                isDisplayed()
            } then {
                step("Закрываем предложение подключить PUSH") {
                    OnBoardingScreen {
                        continuePushButton.click()
                        closePushButton.click()
                    }
                }
            }
            or(OnBoardingScreen.continuePushButton) {
                doesNotExist()
            } then {
                testLogger.i("Нет предложений пуша")
            }
        }
    }
}
