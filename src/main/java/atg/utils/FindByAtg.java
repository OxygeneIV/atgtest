package atg.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.support.AbstractFindByBuilder;
import org.openqa.selenium.support.PageFactoryFinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@PageFactoryFinder(FindByAtg.FindByBuilder.class)
public @interface FindByAtg {

    String testId() default "";

    String testIdStartsWith() default "";

    String testIdEndsWith() default "";

    public static class FindByBuilder extends AbstractFindByBuilder {
        public FindByBuilder() {
        }

        public By buildIt(Object annotation, Field field) {
            FindByAtg findBy = (FindByAtg) annotation;
            By ans = this.buildByFromShortFindBy(findBy);
            return ans;
        }

        protected By buildByFromShortFindBy(FindByAtg findBy) {

            String selector;

            if(!"".equals(findBy.testId())) {
                 selector = String.format("[data-test-id='%s']", findBy.testId());
            } else if (!"".equals(findBy.testIdStartsWith())) {
                 selector = String.format("[data-test-id^='%s']", findBy.testIdStartsWith());
            }else if (!"".equals(findBy.testIdEndsWith())) {
                selector = String.format("[data-test-id$='%s']", findBy.testIdEndsWith());
            }
            else
            {
                throw new RuntimeException("Error in ATG FindBY");
            }

            By atg = By.cssSelector(selector);
            return atg;
        }

    }
}
