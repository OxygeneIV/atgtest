package atg.pageobjects;

import atg.utils.FindByAtg;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RaceCoupon extends BasePageObject{

    private Integer raceNumber;

    public Integer getRaceNumber() {
        return raceNumber;
    }

    public RaceCoupon setRaceNumber(Integer raceNumber) {
        this.raceNumber = raceNumber;
        return this;
    }

    RaceCoupon(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    @FindByAtg(testIdStartsWith = "coupon-button-leg-")
    List<WebElement> startNumberButtons;


    @FindByAtg(testIdEndsWith = "-toggle-all")
    WebElement allHorsesButton;

    private By startButtonBuilder(Integer horseNumber)
    {
        String startButtonAttribute = String.format("[data-test-id='coupon-button-leg-%s-start-%s']", getRaceNumber(), horseNumber);
        By by = By.cssSelector(startButtonAttribute);
        return by;
    }

    public RaceCoupon betOnRandomHorses(Integer numberOfHorses)
    {
        List<Integer> validHorses = getValidHorses();
        Collections.shuffle(validHorses);

        List<Integer> harryList = validHorses.subList(0, numberOfHorses);
        for (Integer horseNumber:harryList
             ) {
            betOnHorse(horseNumber);
        }
        return this;
    }

    public RaceCoupon betOnHorse(Integer horseNumber)
    {
        List<Integer> validHorses = getValidHorses();

        if(!validHorses.contains(horseNumber))
        {
            return this;
        }

        // Create locator at runtime
        By by = startButtonBuilder(horseNumber);
        WebElement me = getWrappedElement();
        boolean displayed = me.isDisplayed();
        WebElement element2 = me.findElement(by);
        WebElement element1 = by.findElement(getWrappedElement());
        WebElement element = this.findElement(by);
        element.click();
        return this;
    }

    public RaceCoupon betOnAllHorses()
    {
        allHorsesButton.click();
        return this;
    }


    private List<Integer> getValidHorses()
    {
        // Conditions
        // Not disabled or scratched
        List<WebElement> buttons = new ArrayList<>(startNumberButtons);

        Boolean removedA = buttons.removeIf(btn->btn.getDomAttribute("disabled")!=null);
        Boolean removedB = buttons.removeIf(btn->"true".equals(btn.getDomAttribute("data-test-scratched")));

        // Collect numbers
        List<Integer> startNumbers = buttons.stream().
                map(btn -> Integer.parseInt(btn.getDomAttribute("data-start-number"))).
                collect(Collectors.toList());

        return startNumbers;
    }

}
