package petstore;

import kong.unirest.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import petstore.mappers.ApiResponse;
import petstore.mappers.Pet;
import petstore.mappers.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PetApi {
    private final static Logger logger = LogManager.getLogger();

    private final static String ROUTE_PARAM_PETID = "petId";

    // --------------------------------------------------------------  API endpoints
    public HttpResponse<Pet> AddPet(Pet pet) {
        HttpResponse<Pet> response =
                handleResponse(
                        createRequest(Service.AddPet).body(pet).
                        asObject(Pet.class));
        return response;
    }

    public HttpResponse<Pet> FindById(long id) {
        HttpResponse<Pet> response =
                handleResponse(
                        createRequest(Service.FindById).
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(id)).
                                asObject(Pet.class));
        return response;
    }

    public HttpResponse<ApiResponse> Delete(long id) {
        HttpResponse<ApiResponse> response =
                handleResponse(
                        createRequest(Service.Delete).
                                header("api_key","special-key").
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(id)).
                                asObject(ApiResponse.class));
        return response;
    }

    public HttpResponse<Pet> Update(Pet pet) {
        HttpResponse<Pet> response =
                handleResponse(
                        createRequest(Service.Update).
                                body(pet).
                                asObject(Pet.class));
        return response;
    }

    public HttpResponse<ApiResponse> UpdateWithFormData(long petId, String name, Status status) {
        HttpResponse<ApiResponse> response =
                handleResponse(
                        createRequest(Service.UpdateWithForm,"application/x-www-form-urlencoded").
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(petId)).
                                field("name",name).
                                field("status",status.toString()).
                                asObject(ApiResponse.class));
        return response;
    }

    public HttpResponse<List<Pet>> FindPetsByStatus(Status... status) {

        List<String> statusList = Arrays.stream(status).map(s -> s.toString()).collect(Collectors.toList());
        String commaSeparated=String.join(",",statusList);

        HttpResponse<List<Pet>> response =
                handleResponse(
                        createRequest(Service.FindByStatus).
                                queryString("status",commaSeparated).
                                asObject(new GenericType<>() {
                                }));
        return response;
    }

    public HttpResponse<ApiResponse> UploadImage(long id, String metadata, File file) {

        String fname = file.getName();
        HttpResponse<ApiResponse> response =
                handleResponse(
                        createRequest(Service.UploadImage,null).
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(id)).
                                field("additionalMetadata",metadata).
                                field("file",file).
                                asObject(ApiResponse.class));
        return response;
    }

    // Default, use Content type application/json to convert Objects to JSON through mapper
    private HttpRequestWithBody createRequest(Service service) {
        return createRequest(service,"application/json");
    }

    private HttpRequestWithBody createRequest(Service service, String content_type) {

        // Setup request
        HttpRequestWithBody httpRequestWithBody = Unirest.request(service.getMethod(),service.getEndPoint());

        // Add headers
        httpRequestWithBody.
                header("accept", "application/json");

        if(content_type != null)
            httpRequestWithBody.header("Content-Type",content_type);

        return httpRequestWithBody;
    }

    private <T> HttpResponse<T> handleResponse(HttpResponse<T> t) {
//        t.ifFailure(r -> {
//            logger.warn("Rest failure: Status:{} Text:{}", r.getStatus(), r.getStatusText());
//            Headers headers = r.getHeaders();
//            List<Header> all = headers.all();
//            for (Header h:all) {
//                logger.info("{}={}",h.getName(),h.getValue());
//            }
//
//            r.getParsingError().ifPresent(e -> {
//                logger.warn("Parsing Error...");
//                logger.warn("Original body: " + e.getOriginalBody());
//                logger.warn("Message: " + e.getMessage());
//                logger.warn("Cause: " + e.getCause());
//                logger.warn(e);
//            });
//        }).ifSuccess(r -> {
//            logger.info("Success: Status = {}", r.getStatus());
//        });
        return t;
    }
}
