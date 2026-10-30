package ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("ui")
class LoginSmokeTest extends BaseUiTest {

    @Test
    @DisplayName("UI-001: Login page loads successfully")
    void loginPageLoads() {
        assertThat(page).hasTitle("Swag Labs");
        assertThat(loginPage.username()).isVisible();
        assertThat(loginPage.password()).isVisible();
        assertThat(loginPage.loginButton()).isVisible();
    }

    @Test
    @DisplayName("UI-002: Invalid credentials display an error")
    void invalidCredentialsShowError() {
        loginPage.login("invalid_user", "wrong_password");
        assertThat(loginPage.error()).containsText("Username and password do not match");
    }

    @Test
    @DisplayName("UI-003: Empty fields show required-field validation")
    void emptyFieldsShowRequiredError() {
        loginPage.loginButton().click();
        assertThat(loginPage.error()).containsText("Username is required");
    }
}
