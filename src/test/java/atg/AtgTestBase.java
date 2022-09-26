package atg;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

public class AtgTestBase {

    protected WebDriver driver;

    @BeforeEach
    void setupBrowser()
    {

        driver = WebDriverManager.chromedriver().create();

        if(System.getProperty("MAX","0").equals("1"))
           driver.manage().window().maximize();

        // Navigate
        driver.get("https://www.atg.se");
    }

    @AfterEach
    void teardown()
    {
            // Sleep for a while
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
