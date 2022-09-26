package atg.pageobjects;

import atg.utils.FindByAtg;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class BettingPage extends BasePageObject{

    BettingPage(WebDriver driver) {
        super(driver);
    }

    @FindByAtg(testId = "calendar-next-day-button")
    WebElement nextDayBtn;
    @FindByAtg(testId = "calendar-previous-day-button")
    WebElement prevDayBtn;



    // need the link for click...
    @FindBy(xpath=".//a[.//*[@data-test-id='calendar-menu-gameType-V4']]")
    WebElement v4Button;

    @FindByAtg(testId = "calendar-menu-gameType-V4")
    WebElement v4ButtonSelected;
    @FindByAtg(testId = "play-game-coupon")
    WebElement betButton;

    @FindByAtg(testId = "new-coupon")
    WebElement newCouponButton;

    @FindBy(css = ".modal-new-coupon")
    WebElement newCouponModal;

    @FindByAtg(testIdStartsWith = "coupon-race-")
    List<WebElement> raceBettingArea;

    public Boolean isV4Selected()
    {
        String domAttribute = v4ButtonSelected.getDomAttribute("data-test-active");
        return "true".equals(domAttribute);
    }

    public RaceCoupon getRaceCoupon(int raceNumber)
    {
        WebElement myRace = raceBettingArea.get(raceNumber-1);
        RaceCoupon raceCoupon = new RaceCoupon(getWrappedDriver(),myRace);
        raceCoupon.setRaceNumber(raceNumber);
        return raceCoupon;
    }

    // Empty by default
    public BettingPage createNewCoupon() {
        newCouponButton.click();
        NewCouponModal modal = new NewCouponModal(getWrappedDriver(),newCouponModal);
        modal.emptyTheCoupon();
        return this;
    }

    public void bet()
    {
        betButton.click();
    }


    public BettingPage backDays(int days)
    {
      for (int i=0;i<days;i++)
      {
          prevDayBtn.click();
      }
      return this;
    }

    public BettingPage findRace() {

        int tries=20;
        while(tries>0) {
            if(v4Button.isEnabled()) {
                v4Button.click();
                if (raceBettingArea.size() > 0) {
                    break;
                }
             }
            nextDayBtn.click();
            tries--;
        }
        if(tries == 0)
            throw new RuntimeException("Failed to find competition");

        return this;
    }
}
