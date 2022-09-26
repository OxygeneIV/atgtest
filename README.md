Requirement: Maven installed
Project developed using Java 11

// ------------------------ PetStore tests

mvn -Dtest=PetTests test

JUnit5 is used, configured to run all tests in parallell.
An AtomicLong is used to assure unique ID's

Results are written to subfolder "testresults", showing the calls to the endpoints.
Each test (thread) writes to a separate log file.

// ------------------------ ATG test

mvn -Dtest=AtgTests test

A few PageObjects have been created.

A more proper implementation should be implemented for the Framework,
where Page components are real Classes instead of just a WebElement.
E.g. Button, TextInput etc. shall be created, wrapping the WebElement.
Also a custom FieldDecorator and Locatorfactory should be adopted to fit ATG.

An ATG-customized "@FindByAtg" has been implemented finding elements with attribute "data-test-id".
A field decorator "AtgFieldDecorator" is applied to allow @FindByAtg to be used by the Selenium PageFactory helper
classes.
The default AjaxElementLocatorFactory is used.

WebDriverManager is used for simplifying managing of the chromedriver to the project.
If it fails, replace with regular driver in the Test Base class

Additional option to run maximized window -D MAX=1 


