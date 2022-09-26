package atg.pageobjects;

import atg.utils.FindByAtg;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class SideMenuPageObject extends BasePageObject {

    @FindByAtg(testId = "horse-left-menu-sub-menu-toggle-allaspel-new")
    WebElement expandAllGamesMenu;

    @FindByAtg(testId = "horse-left-menu-sub-menu-item-v4")
    WebElement v4MenuItem;

    public SideMenuPageObject expandAllGames() {
        expandAllGamesMenu.click();
        return this;
    }

    public BettingPage openV4() {
        this.scrollIntoView(v4MenuItem, true);

        WebDriverWait webDriverWait = new WebDriverWait(getWrappedDriver(), Duration.ofSeconds(10));

        Boolean until = webDriverWait.until(
                ExpectedConditions.and(
                        ExpectedConditions.visibilityOf(v4MenuItem),
                        ExpectedConditions.elementToBeClickable(v4MenuItem)));

        if (until) {
            v4MenuItem.click();
        } else {
            throw new RuntimeException("Failed to get V4 menu item");
        }

        BettingPage bettingPage = new BettingPage(getWrappedDriver());

        // Check we got the page, we just scrolled, could be messed up...
        Boolean v4Selected = bettingPage.isV4Selected();
        if (!v4Selected) throw new RuntimeException("Wrong Game selected!");

        return bettingPage;
    }

    public SideMenuPageObject(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

}
