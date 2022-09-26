package atg.pageobjects;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class NewCouponModal extends BasePageObject {

    @FindBy(css = "button[class*='NewCouponDialog']")
    private WebElement emptyButton;

    public NewCouponModal(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void emptyTheCoupon() {
        emptyButton.click();
    }
}
