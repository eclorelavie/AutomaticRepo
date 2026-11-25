package rb.com.mbanking.presentation.kaspresso.test.payments

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith
import rb.com.mbanking.presentation.kaspresso.assertCompleteDetails
import rb.com.mbanking.presentation.kaspresso.base.BaseTestCase
import rb.com.mbanking.presentation.kaspresso.base.UserDataType
import rb.com.mbanking.presentation.kaspresso.checkAndClick
import rb.com.mbanking.presentation.kaspresso.chooseAccount
import rb.com.mbanking.presentation.kaspresso.chooseFromDropDown
import rb.com.mbanking.presentation.kaspresso.fillField
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithComma
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDot
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDotRuble
import rb.com.mbanking.presentation.kaspresso.scenarios.login.MainLoginMethodScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.ConfirmOperationScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.OperationCompleteAssertScenario
import rb.com.mbanking.presentation.kaspresso.screens.DashboardScreen
import rb.com.mbanking.presentation.kaspresso.screens.TabBarAuthScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationCompleteScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.PaymentsAndTransfersScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.SearchScreen
import rb.com.mbanking.presentation.kaspresso.runner.testrail.CaseId

@LargeTest
@RunWith(AndroidJUnit4::class)
class HousingService : BaseTestCase() {

    /**
     * @see <a href="https://testrail/index.php?/cases/view/628">
     * Оплата квартплаты
     * </a>
     */
    @Test
    @CaseId(628)
    fun housingServicePayment() {
        launch(UserDataType.Payments).run {
            val sum = "1"
            val contractNumber = "1234567"

            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Переход на экран поиска") {
                TabBarAuthScreen { payments.click() }
                PaymentsAndTransfersScreen { searchIcon.click() }
            }

            step("Подтверждаем доступ к контактам") {
                device.permissions.allowViaDialog()
            }

            step("Переход в операцию оплаты БЭСТ Сервис") {
                SearchScreen {
                    searchInput.replaceText("бэст сервис'>")
                    results {
                        childAt<SearchScreen.Item>(1) {
                            name {
                                hasText("БЭСТ Сервис")
                                click()
                            }
                        }
                    }
                }
            }

            step("Заполняем данные операции") {
                OperationScreen {
                    chooseAccount("Источник средств", getUserData("accountNum"))
                    fillField("Номер договора", contractNumber)
                    next.checkAndClick()
                    fillField("Сумма", sum)
                    next.checkAndClick()
                }
            }

            step("Подтверждение операции") {
                OperationScreen {
                    scenario(
                        ConfirmOperationScenario(
                            isShortCode = true,
                            details = mapOf(
                                "Источник средств" to getUserData("accountNum"),
                                "Номер договора" to contractNumber,
                                "Сумма" to sum.formatAsSumWithDot()
                            )
                        )
                    )
                }
            }

            step("Проверка результата операции") {
                scenario(OperationCompleteAssertScenario(operationName = "БЭСТ Сервис", sum = sum.formatAsSumWithDotRuble()))
                OperationCompleteScreen {
                    assertCompleteDetails(
                        "Источник средств" to getUserData("accountNum"),
                        "Номер договора" to contractNumber,
                        "Сумма" to sum.formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
                DashboardScreen { myBank.isDisplayed() }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/631">
     * Оплата электроэнергии
     * </a>
     */
    @Test
    @CaseId(631)
    fun electricityPayment() {
        launch(UserDataType.Payments).run {
            val sum = "1"
            val contractNumber = "1234567"

            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Переход на экран поиска") {
                TabBarAuthScreen { payments.click() }
                PaymentsAndTransfersScreen { searchIcon.click() }
            }

            step("Подтверждаем доступ к контактам") {
                device.permissions.allowViaDialog()
            }

            step("Переход в операцию оплаты Волгоградэнергосбыт") {
                SearchScreen {
                    searchInput.replaceText("Волгоградэнергосбыт")
                    results {
                        childAt<SearchScreen.Item>(1) {
                            name {
                                hasText("Волгоградэнергосбыт")
                                click()
                            }
                        }
                    }
                }
            }

            step("Заполняем данные операции") {
                OperationScreen {
                    chooseAccount("Источник средств", getUserData("accountNum"))
                    fillField("Номер лицевого счета", contractNumber)
                    chooseFromDropDown("Тарифность", "Обычный счетчик")
                    next.checkAndClick()
                    fillField("Период оплаты", "03.2020")
                    next.checkAndClick()
                    fillField("Сумма", sum)
                    next.checkAndClick()
                }
            }

            step("Подтверждение операции") {
                OperationScreen {
                    scenario(
                        ConfirmOperationScenario(
                            isShortCode = true,
                            details = mapOf(
                                "Источник средств" to getUserData("accountNum"),
                                "Номер лицевого счета" to contractNumber,
                                "Тарифность" to "Обычный счетчик",
                                "Период оплаты" to "03.2020",
                                "Сумма" to sum.formatAsSumWithDot()
                            )
                        )
                    )
                }
            }

            step("Проверка результата операции") {
                scenario(OperationCompleteAssertScenario(operationName = "Волгоградэнергосбыт", sum = sum.formatAsSumWithDotRuble()))
                OperationCompleteScreen {
                    assertCompleteDetails(
                        "Источник средств" to getUserData("accountNum"),
                        "Номер лицевого счета" to contractNumber,
                        "Тарифность" to "Обычный счетчик",
                        "Период оплаты" to "03.2020",
                        "Сумма" to sum.formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
                DashboardScreen { myBank.isDisplayed() }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/629">
     * Оплата теплоснабжения
     * </a>
     */
    @Test
    @CaseId(629)
    fun heatSupplyPayment() {
        launch(UserDataType.Payments).run {
            val sum = "1"
            val contractNumber = "123456"

            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Переход на экран поиска") {
                TabBarAuthScreen { payments.click() }
                PaymentsAndTransfersScreen { searchIcon.click() }
            }

            step("Подтверждаем доступ к контактам") {
                device.permissions.allowViaDialog()
            }

            step("Переход в операцию оплаты теплоснабжения>") {
                SearchScreen {
                    searchInput.replaceText("бурятия тгк-14")
                    results {
                        childAt<SearchScreen.Item>(1) {
                            name {
                                hasText("Бурятия ТГК-14")
                                click()
                            }
                        }
                    }
                }
            }

            step("Заполняем данные операции") {
                OperationScreen {
                    chooseAccount("Источник средств", getUserData("accountNum"))
                    fillField("Номер лицевого счета", contractNumber)
                    fillField("Сумма", sum)
                    next.checkAndClick()
                }
            }

            step("Подтверждение операции") {
                OperationScreen {
                    scenario(
                        ConfirmOperationScenario(
                            isShortCode = true,
                            details = mapOf(
                                "Источник средств" to getUserData("accountNum"),
                                "Номер лицевого счета" to contractNumber,
                                "Сумма" to sum.formatAsSumWithDot()
                            )
                        )
                    )
                }
            }

            step("Проверка результата операции") {
                scenario(OperationCompleteAssertScenario(operationName = "Бурятия ТГК-14", sum = sum.formatAsSumWithDotRuble()))
                OperationCompleteScreen {
                    assertCompleteDetails(
                        "Источник средств" to getUserData("accountNum"),
                        "Номер лицевого счета" to contractNumber,
                        "Сумма" to sum.formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
                DashboardScreen { myBank.isDisplayed() }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/630">
     * Оплата газоснабжения
     * </a>
     */
    @Test
    @CaseId(630)
    fun gasSupplyPayment() {
        launch(UserDataType.Payments).run {
            val sum = "1"
            val contractNumber = "12345"

            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Переход на экран поиска") {
                TabBarAuthScreen { payments.click() }
                PaymentsAndTransfersScreen { searchIcon.click() }
            }

            step("Подтверждаем доступ к контактам") {
                device.permissions.allowViaDialog()
            }

            step("Переход в операцию оплаты газоснабжения>") {
                SearchScreen {
                    searchInput.replaceText("Няндомамежрайгаз")
                    results {
                        childAt<SearchScreen.Item>(1) {
                            name {
                                hasText("Няндомамежрайгаз")
                                click()
                            }
                        }
                    }
                }
            }

            step("Заполняем данные операции") {
                OperationScreen {
                    chooseAccount("Источник средств", getUserData("accountNum"))
                    fillField("Номер лицевого счета", contractNumber)
                    next.click()
                    fillField("Сумма", sum)
                    next.checkAndClick()
                }
            }

            step("Подтверждение операции") {
                OperationScreen {
                    scenario(
                        ConfirmOperationScenario(
                            isShortCode = true,
                            details = mapOf(
                                "Источник средств" to getUserData("accountNum"),
                                "Номер лицевого счета" to contractNumber,
                                "Сумма" to sum.formatAsSumWithDot()
                            )
                        )
                    )
                }
            }

            step("Проверка результата операции") {
                scenario(OperationCompleteAssertScenario(operationName = "Няндомамежрайгаз", sum = sum.formatAsSumWithDotRuble()))
                OperationCompleteScreen {
                    assertCompleteDetails(
                        "Источник средств" to getUserData("accountNum"),
                        "Номер лицевого счета" to contractNumber,
                        "Сумма" to sum.formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
                DashboardScreen { myBank.isDisplayed() }
            }
        }
    }
}
