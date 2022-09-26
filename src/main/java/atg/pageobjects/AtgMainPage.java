package atg.pageobjects;

import atg.utils.FindByAtg;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class AtgMainPage extends BasePageObject{

    @FindBy(css="#MENU_PLAY_button")
    WebElement sideMenuTrigger;

    // Tricky, quick solution here
    @FindBy(css= "#sidemenu_anch>div,#MENU_PLAY")
    WebElement verticalMenu;

    @FindBy(css = ".ot-sdk-container")
    WebElement cookiesModal;

    @FindBy(css="[testid='header-verticallink-horse']")
    WebElement horseButton;


    public AtgMainPage(WebDriver driver)
    {
        super(driver);
    }

    public SideMenuPageObject openSideMenu()
    {
        // Open if closed
        String domAttr = sideMenuTrigger.getDomAttribute("aria-expanded");

        if("false".equals(domAttr))
        {
            sideMenuTrigger.click();
        }

        return new SideMenuPageObject(getWrappedDriver(),this.verticalMenu);
    }

    public AtgMainPage acceptCookies()
    {
        AcceptCookiesModal modal = new AcceptCookiesModal(getWrappedDriver(),cookiesModal);
        modal.accept();
        WebDriverWait wait = new WebDriverWait(getWrappedDriver(),Duration.ofSeconds(10));
        Boolean until = wait.until(ExpectedConditions.invisibilityOf(modal.getWrappedElement()));
        return this;
    }


    public AtgMainPage selectGameType(GameType gameType) {
        switch (gameType)
        {
            case Horses:
                horseButton.click();
                break;
            case Sports:
            case Casino:
                throw new RuntimeException("Gametype not implemented: " + gameType);
            default:
                throw new IllegalStateException("Unexpected value: " + gameType);
        }
        return this;
    }
}
