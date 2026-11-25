package rb.com.mbanking.presentation.kaspresso.test.products.accounts

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import rb.com.mbanking.R
import rb.com.mbanking.presentation.kaspresso.Environment
import rb.com.mbanking.presentation.kaspresso.assertAccountNumber
import rb.com.mbanking.presentation.kaspresso.assertCompleteDetails
import rb.com.mbanking.presentation.kaspresso.assertField
import rb.com.mbanking.presentation.kaspresso.base.BaseTestCase
import rb.com.mbanking.presentation.kaspresso.base.UserDataType
import rb.com.mbanking.presentation.kaspresso.checkAndClick
import rb.com.mbanking.presentation.kaspresso.chooseAccount
import rb.com.mbanking.presentation.kaspresso.chooseFromDropDown
import rb.com.mbanking.presentation.kaspresso.environment
import rb.com.mbanking.presentation.kaspresso.fillField
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithComma
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDot
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDotDollar
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDotEuro
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDotRuble
import rb.com.mbanking.presentation.kaspresso.generateRandomString
import rb.com.mbanking.presentation.kaspresso.getText
import rb.com.mbanking.presentation.kaspresso.pressRadioButton
import rb.com.mbanking.presentation.kaspresso.runner.testrail.CaseId
import rb.com.mbanking.presentation.kaspresso.scenarios.login.MainLoginMethodScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.ConfirmOperationScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.OperationCompleteAssertScenario
import rb.com.mbanking.presentation.kaspresso.screens.ActionsMenuScreen
import rb.com.mbanking.presentation.kaspresso.screens.DashboardScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationCompleteScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.RenameProductScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.accounts.AccountDetailScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.accounts.AccountsListScreen
import rb.com.mbanking.presentation.kaspresso.utils.QaService
import kotlin.random.Random.Default.nextInt

@LargeTest
@RunWith(AndroidJUnit4::class)
class CurrentAccounts : BaseTestCase() {

    /**
     * @see <a href="https://testrail/index.php?/cases/view/771">
     * Открытие текущего счета без пополнения в рублях
     * <a>
     */
    @Test
    @CaseId(771)
    fun openCurrentAccountWithoutReplenishRub() {
        launch(env = Environment.DEV).run {
            val clientId = QaService(environment).createNewClient()
            val sum = "0"
            scenario(MainLoginMethodScenario(clientId))

            step("Переходим к открытию текущего счета") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.ItemHeader> { withDescendant { withText(R.string.accounts_test) } } perform {
                        add.click()
                    }
                }

                AccountsListScreen {
                    accountsRecycler.childAt<AccountsListScreen.Account>(1) {
                        name.hasText("Текущий счет")
                        openBtn.click()
                    }
                }
            }

            step("Заполняем данные для открытия текущего счета") {
                OperationScreen {
                    assertField("Тип счета", "Текущий", isEnabled = false)
                    chooseFromDropDown("Валюта", "Российский рубль")
                    pressRadioButton("Пополнить счет сейчас", "Нет")
                    next.checkAndClick()
                }
            }

            scenario(
                ConfirmOperationScenario(
                    isShortCode = false,
                    details = mapOf(
                        "Тип счета" to "Текущий",
                        "Валюта" to "RUR",
                        "Наименование счета" to "Текущий",
                        "Согласие на продвижение услуг Банка" to "Согласие",
                        "Согласие на обработку персональных данных" to "Согласие"
                    )
                )
            )

            step("Проверка результата операции") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(
                            operationName = "Открытие накопительного или текущего счета",
                            isVisibleRepeat = false,
                            isVisibleTemplate = false
                        )
                    )
                    assertCompleteDetails(
                        mapOf(
                            "Тип счета" to "Текущий",
                            "Валюта" to "RUR"
                        )
                    )
                    next.checkAndClick()
                }
            }

            step("Проверяем отображение открытого текущего счета") {
                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Item> { withDescendant { withText("Текущий") } } perform {
                        balance.hasText(sum.formatAsSumWithDotRuble())
                    }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/770">
     * Открытие текущего счета с пополнением в рублях
     * <a>
     */
    @Test
    @CaseId(770)
    fun openCurrentAccountWithReplenishRub() {
        launch(env = Environment.DEV).run {
            val clientId = QaService(environment).createNewClient(
                withCurrentAccount = true,
                withAccountReplenishment = true
            )
            val sum = nextInt(100, 900).toString()
            scenario(MainLoginMethodScenario(clientId))

            step("Переходим к открытию текущего счета") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.ItemHeader> { withDescendant { withText(R.string.accounts_test) } } perform {
                        add.click()
                    }
                }

                AccountsListScreen {
                    accountsRecycler.childAt<AccountsListScreen.Account>(1) {
                        name.hasText("Текущий счет")
                        openBtn.click()
                    }
                }
            }

            step("Заполняем данные для открытия текущего счета") {
                OperationScreen {
                    assertField("Тип счета", "Текущий", isEnabled = false)
                    chooseFromDropDown("Валюта", "Российский рубль")
                    pressRadioButton("Пополнить счет сейчас", "Да")
                    chooseAccount("Со счета", "Стромынка")
                    fillField("На сумму", sum)
                    next.checkAndClick()
                }
            }

            scenario(
                ConfirmOperationScenario(
                    isShortCode = false,
                    details = mapOf(
                        "Тип счета" to "Текущий",
                        "Валюта" to "RUR",
                        "Наименование счета" to "Текущий",
                        "На сумму" to sum.formatAsSumWithDot(),
                        "Согласие на продвижение услуг Банка" to "Согласие",
                        "Согласие на обработку персональных данных" to "Согласие"
                    )
                )
            )

            step("Проверка результата операции") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(
                            operationName = "Открытие накопительного или текущего счета",
                            isVisibleRepeat = false,
                            isVisibleTemplate = false
                        )
                    )
                    assertCompleteDetails(
                        mapOf(
                            "Тип счета" to "Текущий",
                            "Валюта" to "RUR"
                        )
                    )
                    next.checkAndClick()
                }
            }

            step("Проверяем отображение открытого текущего счета на сумму $sum") {
                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Item> {
                        withDescendant { withText(sum.formatAsSumWithDotRuble()) }
                    } perform {
                        name.hasText("Текущий")
                    }
                }
            }
        }
    }


    /**
     * @see <a href="https://testrail/index.php?/cases/view/772">
     * Пополнение текущего счета со своего счета
     * </a>
     */
    @Test
    @CaseId(772)
    fun replenishCurrentAccountFromDashboard() {
        launch(
            UserDataType.Accounts5,
            Environment.DEV
        ).run {
            val sum = nextInt(100, 900)
            var balance = 0.0
            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Поиск счета для пополнения") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Item> { withDescendant { withText(getUserData("rubleAcc2Name")) } } perform {
                        balance = getText(this.balance).dropLast(2).replace(" ", "").toDouble()
                        menu.click()
                    }
                    menuList.childAt<DashboardScreen.ListMenuItem>(0) {
                        caption.hasText("Пополнить")
                        caption.isVisible()
                        caption.click()
                    }
                }
            }

            step("Пополняем счет") {
                OperationScreen {
                    chooseAccount("Счет списания", getUserData("rubleAccNum"))
                    assertAccountNumber("Счет зачисления", getUserData("rubleAcc2Num"))
                    fillField("Сумма", sum.toString())
                    next.checkAndClick()
                }
            }

            step("Проверка данных на экране подтверждения") {
                scenario(
                    ConfirmOperationScenario(
                        isShortCode = false,
                        details = mapOf(
                            "Счет списания" to getUserData("rubleAccNum"),
                            "Счет зачисления" to getUserData("rubleAcc2Num"),
                            "Валюта суммы" to "RUR",
                            "Сумма" to sum.toString().formatAsSumWithDot()
                        )
                    )
                )
            }

            step("Проверка результатов операции") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(operationName = "Между своими счетами ПСБ", sum = sum.toString().formatAsSumWithDotRuble())
                    )
                    assertCompleteDetails(
                        "Счет списания" to getUserData("rubleAccNum"),
                        "Счет зачисления" to getUserData("rubleAcc2Num"),
                        "Валюта суммы" to "RUR",
                        "Сумма" to sum.toString().formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
            }

            step("Проверяем, что баланс изменился на сумму перевода $sum") {
                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Item> { withDescendant { withText(getUserData("rubleAcc2Name")) } } perform {
                        assertEquals(
                            "Баланс отличен от ожидаемого",
                            balance + sum,
                            getText(this.balance).dropLast(2).replace(" ", "").toDouble()
                        )
                    }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/5306">
     * Переименование счета
     * </a>
     */
    @Test
    @CaseId(5306)
    fun renameAccount() {
        launch(env = Environment.DEV).run {
            val newName = generateRandomString(10)
            val clientId = QaService(environment).createNewClient(withCurrentAccount = true)

            scenario(MainLoginMethodScenario(clientId))
            step("Переходим в имеющийся у клиента текущий счет") {
                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Item> { withDescendant { withText("Текущий") } } perform {
                        name.click()
                    }
                }
            }

            step("Изменяем название текущего счета на $newName") {
                AccountDetailScreen {
                    fab.click()
                }

                ActionsMenuScreen {
                    actionsRecycler.childAt<ActionsMenuScreen.Item>(0) {
                        caption.hasText(R.string.rename_test)
                        caption.click()
                    }
                }

                RenameProductScreen {
                    this.newName.hasText("Текущий")
                    this.newName.replaceText(newName)
                    rename.click()
                }
            }

            step("Проверка отображения счета с новым именем $newName") {
                AccountDetailScreen {
                    pressBack()
                }

                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Item> {
                        withDescendant {
                            withText(newName)
                        }
                    } perform {
                        name.hasText(newName)
                    }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/774">
     * Атрибуты текущего счета
     * </a>
     */
    @Test
    @CaseId(774)
    fun currentAccountAttributes() {
        launch(
            UserDataType.Accounts5,
            Environment.DEV
        ).run {
            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Проверяем отображение типов пополнения для текущих счетов") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Item> { withDescendant { withText(getUserData("rubleAcc2Name")) } } perform {
                        menu.click()
                    }

                    val operations = listOf("Пополнить", "Пополнить c карты другого банка")

                    for ((index, value) in operations.withIndex()) {
                        menuList.childAt<DashboardScreen.ListMenuItem>(index) {
                            caption.hasText(value)
                        }
                    }

                    menuList.childAt<DashboardScreen.ListMenuItem>(0) {
                        caption.click()
                    }
                    pressBack()
                }
            }

            step("Переходим в текущий счет") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Item> { withDescendant { withText(getUserData("rubleAcc2Name")) } } perform {
                        name.click()
                    }
                }
            }

            step("Проверяем отображение всех доступных действий с текущим счетом") {
                AccountDetailScreen {
                    fab.click()
                }

                ActionsMenuScreen {
                    val operations = mapOf(
                        actionsRecycler to listOf("Переименовать", "Пополнить", "Пополнить с карты другого банка", "Оплатить или перевести"),
                        papersRecycler to listOf("Справка о наличии счета", "Справка о состоянии счета", "Справка о сумме оборотов по счету"),
                        actions2Recycler to listOf("Загрузить выписку", "Реквизиты счета", "Подробности", "Закрыть счет")
                    )

                    for (operation in operations) {
                        for ((index, value) in operation.value.withIndex()) {
                            operation.key.childAt<ActionsMenuScreen.Item>(index) {
                                caption.hasText(value)
                            }
                        }
                    }
                }
            }
        }
    }
}
