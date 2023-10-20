package com.github.fatihsokmen.wallet.presentation.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.fatihsokmen.wallet.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val uiRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun GIVEN_screen_WHEN_fiat_price_inputted_THEN_correct_eth_amount_should_display() {
        // Eth price is 1500
        uiRule.onNodeWithTag("user-input").performTextInput("750")
        uiRule.waitUntilDoesNotExist(hasText("0 ETH"))
        uiRule.onNodeWithTag("exp-amount-text").assertTextContains("0.5 ETH")
        // Append another 0
        uiRule.onNodeWithTag("user-input").performTextInput("0")
        uiRule.waitUntilDoesNotExist(hasText("0.5 ETH"))
        uiRule.onNodeWithTag("exp-amount-text").assertTextContains("5 ETH")
    }

    @Test
    fun GIVEN_home_screen_WHEN_user_clicks_input_mode_THEN_currency_chip_hides() {
        // Eth price is 1500
        uiRule.onNodeWithTag("user-input").performTextInput("750")
        uiRule.waitUntilDoesNotExist(hasText("0 ETH"))
        uiRule.onNodeWithTag("currency-chip").assertExists()
        uiRule.onNodeWithTag("input-mode").performClick()
        uiRule.onNodeWithTag("currency-chip").assertDoesNotExist()
    }

    @Test
    fun GIVEN_home_screen_WHEN_currency_chip_clicks_THEN_bottom_sheet_pops_up_and_selects_euro() {
        // Eth price is 1500 and default currency is USD
        uiRule.onNodeWithTag("user-input").performTextInput("1500")
        uiRule.waitUntilDoesNotExist(hasText("0 ETH"))
        uiRule.onNodeWithTag("currency-chip").performClick()
        uiRule.waitUntilAtLeastOneExists(hasText("Displayed Currencies"))
        uiRule.onNodeWithTag("EUR-option").performClick()
        uiRule.waitUntilDoesNotExist(hasText("$"))
        uiRule.onNode(hasText("€")).assertIsDisplayed()
    }

    /**
     * GBP selection  raises exception to test unhappy path
     */
    @Test
    fun GIVEN_home_screen_WHEN_api_raises_exception_THEN_shows_a_snackbar() {
        // Eth price is 1500 and default currency is USD
        uiRule.onNodeWithTag("user-input").performTextInput("1500")
        uiRule.waitUntilDoesNotExist(hasText("0 ETH"))
        uiRule.onNodeWithTag("currency-chip").performClick()
        uiRule.waitUntilAtLeastOneExists(hasText("Displayed Currencies"))
        uiRule.onNodeWithTag("GBP-option").performClick()
        uiRule.waitUntilAtLeastOneExists(hasText("An error occurred"))
        uiRule.onNodeWithTag("snackbar").assertIsDisplayed()
    }
}