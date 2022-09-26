package atg.pageobjects;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class AcceptCookiesModal extends BasePageObject{

    @FindBy(css="#onetrust-accept-btn-handler")
    WebElement acceptButton;

    public AcceptCookiesModal(WebDriver driver, SearchContext searchContext)
    {
       super(driver,searchContext);
    }

    public void accept()
    {
        acceptButton.click();
    }

}
