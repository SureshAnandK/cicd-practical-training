package ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

class LoginPage {
    private final Page page;
    private final String baseUrl;

    LoginPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    Locator username() { return page.locator("#user-name"); }
    Locator password() { return page.locator("#password"); }
    Locator loginButton() { return page.locator("#login-button"); }
    Locator error() { return page.locator("[data-test='error']"); }

    void open() {
        page.navigate(baseUrl);
    }

    void login(String user, String pass) {
        username().fill(user);
        password().fill(pass);
        loginButton().click();
    }
}
