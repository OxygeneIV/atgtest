package petstore;

import kong.unirest.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LoggingInterceptor implements Interceptor {

    private static Logger logger = LogManager.getLogger();

        @Override
        public void onRequest(HttpRequest<?> request, Config config) {
            HttpRequestSummary httpRequestSummary = request.toSummary();
            logger.info("Request: {}",httpRequestSummary.asString() );
        }

        @Override
        public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
            logger.info("Response -  Status:{}  , Status Text:{}", response.getStatus(), response.getStatusText());
            response.ifFailure(r -> {

                Headers headers = r.getHeaders();
                List<Header> all = headers.all();
                for (Header h:all) {
                    logger.info("{}={}",h.getName(),h.getValue());
                }

                r.getParsingError().ifPresent(e -> {
                    logger.warn("Parsing Error...");
                    logger.warn("Original body: " + e.getOriginalBody());
                    logger.warn("Message: " + e.getMessage());
                    logger.warn("Cause: " + e.getCause());
                    logger.warn(e);
                });
            });
        }
}
