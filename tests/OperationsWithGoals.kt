package rb.com.mbanking.presentation.kaspresso.test.products.goals

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import junit.framework.AssertionFailedError
import org.junit.Test
import org.junit.runner.RunWith
import rb.com.mbanking.R
import rb.com.mbanking.presentation.kaspresso.assertAccountNumber
import rb.com.mbanking.presentation.kaspresso.assertCompleteDetails
import rb.com.mbanking.presentation.kaspresso.base.BaseTestCase
import rb.com.mbanking.presentation.kaspresso.base.UserDataType
import rb.com.mbanking.presentation.kaspresso.checkAndClick
import rb.com.mbanking.presentation.kaspresso.checkDeprecatedToast
import rb.com.mbanking.presentation.kaspresso.chooseAccount
import rb.com.mbanking.presentation.kaspresso.fillField
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithComma
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDot
import rb.com.mbanking.presentation.kaspresso.formatAsSumWithDotRuble
import rb.com.mbanking.presentation.kaspresso.generateRandomString
import rb.com.mbanking.presentation.kaspresso.getText
import rb.com.mbanking.presentation.kaspresso.inputCode
import rb.com.mbanking.presentation.kaspresso.scenarios.login.MainLoginMethodScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.ConfirmOperationScenario
import rb.com.mbanking.presentation.kaspresso.scenarios.operations.OperationCompleteAssertScenario
import rb.com.mbanking.presentation.kaspresso.screens.ActionsMenuScreen
import rb.com.mbanking.presentation.kaspresso.screens.DashboardScreen
import rb.com.mbanking.presentation.kaspresso.screens.cards.CardOperationScreen
import rb.com.mbanking.presentation.kaspresso.screens.cards.NewCardScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationCompleteScreen
import rb.com.mbanking.presentation.kaspresso.screens.operations.OperationScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.RenameProductScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.goals.GoalDetailsScreen
import rb.com.mbanking.presentation.kaspresso.screens.products.goals.GoalsParamsScreen
import kotlin.random.Random.Default.nextInt
import rb.com.mbanking.presentation.kaspresso.runner.testrail.CaseId

@LargeTest
@RunWith(AndroidJUnit4::class)
class OperationsWithGoals : BaseTestCase() {

    /**
     * @see <a href="https://testrail/index.php?/cases/view/993">
     * Пополнение выбранной цели
     * </a>
     */
    @Test
    @CaseId(993)
    fun replenishGoalFromContextActions() {
        launch(UserDataType.Payments).run {
            val replenishmentAmount = nextInt(1, 500).toString()
            var balance = 0

            scenario(MainLoginMethodScenario(getUserData("alias")))
            step("Переходим к детальной информации по цели ${getUserData("goalName")}") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(getUserData("goalName"))
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        this.balance.isDisplayed()
                        balance = getText(this.balance).split(".").first().replace(" ", "").toInt()
                        testLogger.i("Текущий баланс цели ${getUserData("goalName")} = $balance")
                        name.click()
                    }
                }
            }

            step("Переходим к пополнению цели") {
                GoalDetailsScreen {
                    fab.click()
                }

                ActionsMenuScreen {
                    actionsRecycler.childAt<ActionsMenuScreen.Item>(1) {
                        caption.hasText("Пополнить")
                        caption.click()
                    }
                }
            }

            step("Вводим данные для пополнения цели") {
                OperationScreen {
                    chooseAccount("Счет списания", getUserData("accountNum"))
                    assertAccountNumber("Счет зачисления", getUserData("goalAccNum"))
                    fillField("Сумма", replenishmentAmount)
                    next.checkAndClick()
                }
            }

            scenario(
                ConfirmOperationScenario(
                    isShortCode = false,
                    details = mapOf(
                        "Счет списания" to getUserData("accountNum"),
                        "Счет зачисления" to getUserData("goalAccNum"),
                        "Сумма" to replenishmentAmount.formatAsSumWithDot()
                    )
                )
            )

            step("Проверка результатов операции") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(
                            operationName = "Между своими счетами ",
                            sum = replenishmentAmount.formatAsSumWithDotRuble()
                        )
                    )
                    assertCompleteDetails(
                        "Счет списания" to getUserData("accountNum"),
                        "Счет зачисления" to getUserData("goalAccNum"),
                        "Валюта суммы" to "RUR",
                        "Сумма" to replenishmentAmount.formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
            }

            step("Проверяем, что цель пополнилась на $replenishmentAmount") {
                DashboardScreen {
                    refreshDashboard(scrollToTop = true)
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(getUserData("goalName"))
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        this.balance.hasText((replenishmentAmount.toInt() + balance).toString().formatAsSumWithDotRuble())
                    }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/566">
     * Пополнение выбранной цели с Дэшборда
     * </a>
     */
    @Test
    @CaseId(566)
    fun replenishGoalFromDashboard() {
        launch(UserDataType.Payments).run {
            val replenishmentAmount = nextInt(1, 500).toString()
            var balance = 0

            scenario(MainLoginMethodScenario(getUserData("alias")))
            step("Переходим к пополнению цели ${getUserData("goalName")} через дэшборд") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(getUserData("goalName"))
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        this.balance.isDisplayed()
                        balance = getText(this.balance).split(".").first().replace(" ", "").toInt()
                        testLogger.i("Текущий баланс цели ${getUserData("goalName")} = $balance")
                        menu.click()
                    }
                    menuList.childAt<DashboardScreen.ListMenuItem>(0) {
                        caption.hasText("Пополнить")
                        caption.click()
                    }
                }
            }

            step("Вводим данные для пополнения цели") {
                OperationScreen {
                    chooseAccount("Счет списания", getUserData("accountNum"))
                    assertAccountNumber("Счет зачисления", getUserData("goalAccNum"))
                    fillField("Сумма", replenishmentAmount)
                    next.checkAndClick()
                }
            }

            scenario(
                ConfirmOperationScenario(
                    isShortCode = false,
                    details = mapOf(
                        "Счет списания" to getUserData("accountNum"),
                        "Счет зачисления" to getUserData("goalAccNum"),
                        "Сумма" to replenishmentAmount.formatAsSumWithDot()
                    )
                )
            )

            step("Проверка результатов операции") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(
                            operationName = "Между своими счетами",
                            sum = replenishmentAmount.formatAsSumWithDotRuble()
                        )
                    )
                    assertCompleteDetails(
                        "Счет списания" to getUserData("accountNum"),
                        "Счет зачисления" to getUserData("goalAccNum"),
                        "Валюта суммы" to "RUR",
                        "Сумма" to replenishmentAmount.formatAsSumWithComma()
                    )
                    next.checkAndClick()
                }
            }

            step("Проверяем, что цель пополнилась на $replenishmentAmount") {
                DashboardScreen {
                    refreshDashboard(scrollToTop = true)
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(getUserData("goalName"))
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        this.balance.hasText((replenishmentAmount.toInt() + balance).toString().formatAsSumWithDotRuble())
                    }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/9199">
     * Пополнение выбранной цели с дашборда картой другого банка
     * </a>
     */
    @Test
    @CaseId(9199)
    fun replenishGoalFromDashboardCardAnotherBank() {
        launch(UserDataType.Payments).run {
            val replenishmentAmount = nextInt(100, 500).toString()
            var balance = 0

            scenario(MainLoginMethodScenario(getUserData("alias")))
            step("Переходим к пополнению цели ${getUserData("goalName")} через дэшборд с карты другого банка") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(getUserData("goalName"))
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        this.balance.isDisplayed()
                        balance = getText(this.balance).split(".").first().replace(" ", "").toInt()
                        testLogger.i("Текущий баланс цели ${getUserData("goalName")} = $balance")
                        menu.click()
                    }
                    menuList.childAt<DashboardScreen.ListMenuItem>(1) {
                        caption.hasText("Пополнить c карты другого банка")
                        caption.click()
                    }
                }
            }

            step("Вводим карту другого банка ${getUserData("anotherBankCard")}") {
                CardOperationScreen {
                    newSenderCard.click()
                }
                NewCardScreen {
                    inputNewCardData(getUserData("anotherBankCard"), "12", "20", "572")
                }
            }

            step("Заполняем сумму операции перевода значением '$replenishmentAmount'") {
                fillField("Сумма перевода", replenishmentAmount)
                flakySafely(timeoutMs = 20_000L, allowedExceptions = mutableSetOf(AssertionFailedError::class.java)) {
                    OperationScreen {
                        next.checkAndClick()
                    }
                }
            }

            step("Вводим код подтверждения ${getUserData("confirmCodeNSPK")}") {
                inputCode()
            }

            step("Проверяем результат операции перевода") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(
                            operationName = "С карты другого банка",
                            sum = replenishmentAmount.formatAsSumWithDotRuble()
                        )
                    )
                    assertCompleteDetails(
                        mapOf(
                            "Сумма перевода" to replenishmentAmount.formatAsSumWithComma(),
                            "Номер счета получателя" to getUserData("goalAccNum")
                        )
                    )
                    next.checkAndClick()
                }
            }

            step("Проверяем, что цель пополнилась на $replenishmentAmount") {
                DashboardScreen {
                    refreshDashboard(scrollToTop = true)
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(getUserData("goalName"))
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        this.balance.hasText((replenishmentAmount.toInt() + balance).toString().formatAsSumWithDotRuble())
                    }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/9178">
     * Переименование цели
     * </a>
     */
    @Test
    @CaseId(9178)
    fun goalRename() {
        launch(UserDataType.Goals3).run {
            var currentName = ""
            var newName = "GoalName: " + generateRandomString(6)

            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Переходим к детальной информации по цели") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Goal> { withDescendant { withText("0%") } } perform {
                        name.isVisible()
                        currentName = getText(name)
                        testLogger.i("Текущий название цели - '$currentName'")
                    }
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(currentName)
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        name.isCompletelyDisplayed()
                        name.click()
                    }
                }
            }

            step("Переименование цели с текущим именем $currentName") {
                GoalDetailsScreen {
                    fab.click()
                }
                ActionsMenuScreen {
                    actionsRecycler.childAt<ActionsMenuScreen.Item>(0) {
                        caption.hasText(R.string.rename_test)
                        caption.click()
                    }
                }
                RenameProductScreen {
                    this.newName.hasText(currentName)
                    this.newName.replaceText(newName)
                    rename.click()
                }
                GoalDetailsScreen {
                    checkDeprecatedToast("Переименование выполнено")
                    pressBack()
                }
            }

            step("Проверка отображения цели с новым именем $newName") {
                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Goal> { withDescendant { withText(newName) } } perform { name.isVisible() }
                }
            }
        }
    }

    /**
     * @see <a href="https://testrail/index.php?/cases/view/996">
     * Изменение поставленной цели
     * </a>
     */
    @Test
    @CaseId(996)
    fun changeGoal() {
        launch(UserDataType.Goals4).run {
            var currentGoal = ""
            var newGoal = ""

            scenario(MainLoginMethodScenario(getUserData("alias")))

            step("Переходим к детальной информации по цели") {
                DashboardScreen {
                    dashboardRecycler.childWith<DashboardScreen.Goal> { withDescendant { withText("0%") } } perform {
                        name.isVisible()
                        currentGoal = getText(name)
                        testLogger.i("Текущая цель - '$currentGoal'")
                    }
                    dashboardRecycler.childWith<DashboardScreen.Goal> {
                        withDescendant {
                            withText(currentGoal)
                            withParent {
                                withSibling {
                                    withDescendant { withId(R.id.total) }
                                }
                            }
                        }
                    } perform {
                        name.isCompletelyDisplayed()
                        name.click()
                    }
                }
            }

            step("Изменение шаблона выбраной цели $currentGoal на $newGoal") {
                GoalDetailsScreen {
                    fab.click()
                }
                ActionsMenuScreen {
                    actions2Recycler.isDisplayed()
                    actions2Recycler.childAt<ActionsMenuScreen.Item>(0) {
                        caption.hasText("Изменить цель")
                        caption.click()
                    }
                }
                GoalsParamsScreen {
                    selectedGoal.hasText(currentGoal)
                    newGoal = if (currentGoal == "Автомобиль") "Свадьба" else "Автомобиль"
                    selectGoalTemplate(newGoal)
                }
                OperationScreen { next.checkAndClick() }
            }

            scenario(
                ConfirmOperationScenario(
                    isShortCode = true,
                    details = mapOf(
                        "Название цели" to newGoal
                    )
                )
            )

            step("Подтверждение операции изменения шаблона цели") {
                OperationCompleteScreen {
                    scenario(
                        OperationCompleteAssertScenario(
                            operationName = "Изменение цели",
                            isVisibleRepeat = false,
                            isVisibleTemplate = false,
                            isVisibleReceipt = false
                        )
                    )
                    assertCompleteDetails(
                        "Название цели" to newGoal
                    )
                    next.checkAndClick()
                }
            }

            step("Проверка отображения новой цели на дэшборде") {
                DashboardScreen {
                    refreshDashboard()
                    dashboardRecycler.childWith<DashboardScreen.Goal> { withDescendant { withText(newGoal) } } perform { name.isVisible() }
                }
            }
        }
    }
}
