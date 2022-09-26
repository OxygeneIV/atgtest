package atg.utils;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class AtgFieldDecorator extends DefaultFieldDecorator {

    public AtgFieldDecorator(ElementLocatorFactory factory) {
        super(factory);
    }

    public Object decorate(ClassLoader loader, Field field) {
        if (!WebElement.class.isAssignableFrom(field.getType()) && !this.isDecoratableList(field)) {
            return null;
        } else {
            ElementLocator locator = this.factory.createLocator(field);
            if (locator == null) {
                return null;
            } else if (WebElement.class.isAssignableFrom(field.getType())) {
                return this.proxyForLocator(loader, locator);
            } else {
                return List.class.isAssignableFrom(field.getType()) ? this.proxyForListLocator(loader, locator) : null;
            }
        }
    }

    @Override
    protected boolean isDecoratableList(Field field) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        // Type erasure in Java isn't complete. Attempt to discover the generic
        // type of the list.
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

        if (!WebElement.class.equals(listType)) {
            return false;
        }

        if (field.getAnnotation(FindBy.class) == null &&
                field.getAnnotation(FindBys.class) == null &&
                field.getAnnotation(FindAll.class) == null &&
                field.getAnnotation(FindByAtg.class) == null) {
            return false;
        }

        return true;
    }
}
