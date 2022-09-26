package atg.pageobjects;

import atg.utils.AtgFieldDecorator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePageObject implements WrapsElement, WrapsDriver{


    private WebDriver  wrappedDriver;

    private SearchContext searchContext;

    BasePageObject(WebDriver driver) {
        this(driver,driver);
    }


    BasePageObject(WebDriver driver,SearchContext searchContext) {
        this.wrappedDriver=driver;
        this.searchContext=searchContext;
        AtgFieldDecorator atgFieldDecorator = new AtgFieldDecorator(new AjaxElementLocatorFactory(searchContext, 5));
        PageFactory.initElements(atgFieldDecorator,this);
    }

    @Override
    public WebDriver getWrappedDriver() {
        return wrappedDriver;
    }

    @Override
    public WebElement getWrappedElement() {
        return (WebElement) searchContext;
    }

    protected List<WebElement> findElements(By by) {
        return searchContext.findElements(by);
    }

    protected WebElement findElement(By by) {
        return searchContext.findElement(by);
    }

    protected void scrollIntoView(WebElement webElement,Boolean up) {
        JavascriptExecutor js = (JavascriptExecutor) getWrappedDriver();
        js.executeScript("arguments[0].scrollIntoView(" + up + ");", webElement);
    }
}
