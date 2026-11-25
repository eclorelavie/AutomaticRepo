package rb.com.mbanking.presentation.kaspresso.test.products.investments

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith
import rb.com.mbanking.R
import rb.com.mbanking.presentation.kaspresso.base.BaseTestCase
import rb.com.mbanking.presentation.kaspresso.base.UserDataType
import rb.com.mbanking.presentation.kaspresso.checkAndClick
import rb.com.mbanking.presentation.kaspresso.chooseAccount
import rb.com.mbanking.presentation.kaspresso.chooseFromDropDown
import rb.com.mbanking.presentation.kaspresso.fillField
import rb.com.mbanking.presentation.kaspresso.pressRadioButton
import rb.com.mbanking.presentation.kaspresso.scenarios.login.MainLoginMethodScenario
import rb.com.mbanking.presentation.kaspresso.screens.DashboardScreen
import rb.com.mbanking.presentation.kaspresso.runner.testrail.CaseId
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.ConfirmOperationScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.OperationCompleteAssertScenario
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationCompleteScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.funds.FundsDetailsScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.investments.PortfolioScreen

@SmallTest
@RunWith(AndroidJUnit4::class)
class SaleFunds : BaseTestCase() {

    /**
     * @see <a href="https://testrail/index.php?/cases/view/136065">
     * Инвестиции. Продажа ПИФ
     * </a>
     */
    @Test
    @CaseId(136065)
    fun saleFunds() {
        launch(UserDataType.Investment11).run {
            val enrollmentAccount = "40817810251006540638"
            val contractNumber = "ЛС №4142074 (паёв (штук): 146.55407)"

            scenario(MainLoginMethodScenario(getUserData("clientNum")))
            step("Нажимаем на Паевые фонды (ПИФ) на дэшборде") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Investment> { withDescendant { withText(R.string.investments_funds_title_test) } }
                        .perform {
                            title.click()
                        }
                }
            }

            step("Переходим на позицию ПИФа в портфеле") {
                PortfolioScreen {
                    recyclerView.childWith<PortfolioScreen.Item> { withDescendant { withText("РС – Облигации") } } perform {
                        fundTitle.click()
                    }
                }
            }

            step("Нажимем кнопку \"Продать\"") {
                FundsDetailsScreen {
                    pbSell.click()
                }
            }

            step("Заполняем данные операции") {
                OperationScreen {
                    chooseFromDropDown("Лицевой счет", contractNumber)
                    fillField("Количество паев (штук)", "1")
                    chooseAccount("Счет зачисления", enrollmentAccount)
                    pressRadioButton("", "Данные указаны верно")
                    next.checkAndClick()
                }
            }

            step("Подтверждение операции") {
                OperationScreen {
                    scenario(
                        ConfirmOperationScenario(
                            isShortCode = true,
                            details = mapOf(
                                "Лицевой счет" to contractNumber,
                                "Количество паев (штук)" to "1",
                                "Счет зачисления" to enrollmentAccount
                            )
                        )
                    )
                }
            }

            step("Проверка результата операции") {
                scenario(OperationCompleteAssertScenario("Продажа (погашение) паев через цифровые каналы", isVisibleRepeat = false,
                    isVisibleTemplate = false))
                OperationCompleteScreen {
                    operationFinalComment.hasText("Оформление сделки производится в течение 3х рабочих дней.")
                    next.checkAndClick()
                }
                DashboardScreen { myBank.isDisplayed() }
            }
        }
    }
}
