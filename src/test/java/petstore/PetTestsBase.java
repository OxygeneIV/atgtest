package petstore;

import kong.unirest.JsonObjectMapper;
import kong.unirest.Unirest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import petstore.mappers.Category;
import petstore.mappers.Pet;
import petstore.mappers.Status;
import petstore.mappers.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class PetTestsBase {
    protected static final String URL = "https://petstore.swagger.io/v2";
    private static final Logger logger = LogManager.getLogger();

    private static final AtomicLong petshop_base_id = new AtomicLong(1101010100);

    static PetApi petApi;


    static
    {
        System.setProperty("log4j2.isThreadContextMapInheritable", "true");
        Unirest.config().defaultBaseUrl(URL);
        Unirest.config().setObjectMapper(new JsonObjectMapper());
        Unirest.config().connectTimeout(120000);
        Unirest.config().interceptor(new LoggingInterceptor());
        Unirest.config().instrumentWith(requestSummary -> {
            long startNanos = System.nanoTime();
            return (responseSummary, exception) -> logger.info("URL: {} Method: {}, Status: {}, time[ms]: {}",
                    requestSummary.getUrl(),
                    requestSummary.getHttpMethod(),
                    responseSummary!=null ? responseSummary.getStatus() : "-1",
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos));
        });
    }

    @BeforeAll
    static void suiteSetup()
    {
        petApi = new PetApi();
    }


    @BeforeEach
    void testSetup(TestInfo testInfo)
    {
        // Create testlogfile / test(thread)
        String testcase = testInfo.getDisplayName().replace("()","")+ ".txt";
        ThreadContext.put("ROUTINGKEY","./testresults/"+testcase);
    }

    static Supplier<Category> randomCategorySupplier = ()-> {
        int length = RandomUtils.nextInt(5,20);
        return new Category().setId(RandomUtils.nextLong()).setName(RandomStringUtils.randomAlphabetic(length));
    };

    static Supplier<Tag> randomTagSupplier = ()-> {
        int length = RandomUtils.nextInt(5,20);
        return new Tag().setId(RandomUtils.nextLong()).setName(RandomStringUtils.randomAlphabetic(length));
    };

    static Supplier<String> randomUrlSupplier = () -> {
        String baseUrl = "http://harry.atg.se/";
        int urlComponents = RandomUtils.nextInt(2, 5);
        List<String> subs = new ArrayList<>();
        for (int i = 0; i < urlComponents; i++) {
            String generatedString = RandomStringUtils.randomAlphanumeric(5, 10);
            subs.add(generatedString);
        }
        return baseUrl + String.join("/", subs);
    };

    static Pet createEmptyPetObject()
    {
        long id = petshop_base_id.addAndGet(1);
        Pet pet = new Pet();
        pet.setId(id);
        return pet;
    }

    static Pet createFullPetObject()
    {
        Pet pet = createEmptyPetObject();
        pet.addPhotoUrl(randomUrlSupplier.get());
        pet.setCategory(randomCategorySupplier.get());
        // Add 2 tags
        pet.addTag(randomTagSupplier.get()).addTag(randomTagSupplier.get());
        // Set init status
        pet.setStatus(Status.available);
        pet.setName(RandomStringUtils.randomAlphabetic(10,15));
        return pet;
    }
}
