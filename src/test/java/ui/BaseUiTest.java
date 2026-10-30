package ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class BaseUiTest {
    static final String BASE_URL =
            System.getenv().getOrDefault("BASE_URL", "https://www.saucedemo.com");
    private static final Path SCREENSHOTS = Paths.get("target", "screenshots");
    private static final Path TRACES = Paths.get("target", "traces");

    private static Playwright playwright;
    private static Browser browser;

    BrowserContext context;
    Page page;
    LoginPage loginPage;
    private boolean failed;

    /** Runs before @AfterEach, so the page is still open when a failing test is captured. */
    @RegisterExtension
    final TestExecutionExceptionHandler onFailure = (ctx, throwable) -> {
        failed = true;
        Files.createDirectories(SCREENSHOTS);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(SCREENSHOTS.resolve(ctx.getRequiredTestMethod().getName() + ".png"))
                .setFullPage(true));
        throw throwable;
    };

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openPage() {
        failed = false;
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        page = context.newPage();
        page.setDefaultTimeout(10_000);
        loginPage = new LoginPage(page, BASE_URL);
        loginPage.open();
    }

    @AfterEach
    void closePage(TestInfo info) throws Exception {
        if (failed) {
            Files.createDirectories(TRACES);
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(TRACES.resolve(info.getTestMethod().orElseThrow().getName() + ".zip")));
        } else {
            context.tracing().stop();
        }
        context.close();
    }
}
