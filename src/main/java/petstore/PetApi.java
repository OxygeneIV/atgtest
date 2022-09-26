package petstore;

import kong.unirest.GenericType;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import petstore.mappers.ApiResponse;
import petstore.mappers.Pet;
import petstore.mappers.Status;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PetApi {

    private final static String ROUTE_PARAM_PETID = "petId";

    // --------------------------------------------------------------  API endpoints
    public HttpResponse<Pet> AddPet(Pet pet) {
        HttpResponse<Pet> response =
                        createRequest(Service.AddPet).body(pet).
                        asObject(Pet.class);
        return response;
    }

    public HttpResponse<Pet> FindById(long id) {
        HttpResponse<Pet> response =
                        createRequest(Service.FindById).
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(id)).
                                asObject(Pet.class);
        return response;
    }

    public HttpResponse<ApiResponse> Delete(long id) {
        HttpResponse<ApiResponse> response =
                        createRequest(Service.Delete).
                                header("api_key","special-key").
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(id)).
                                asObject(ApiResponse.class);
        return response;
    }

    public HttpResponse<Pet> Update(Pet pet) {
        HttpResponse<Pet> response =
                        createRequest(Service.Update).
                                body(pet).
                                asObject(Pet.class);
        return response;
    }

    public HttpResponse<ApiResponse> UpdateWithFormData(long petId, String name, Status status) {
        HttpResponse<ApiResponse> response =
                        createRequest(Service.UpdateWithForm,null).  // "application/x-www-form-urlencoded"
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(petId)).
                                field("name",name).
                                field("status",status.toString()).
                                asObject(ApiResponse.class);
        return response;
    }

    public HttpResponse<List<Pet>> FindPetsByStatus(Status... status) {

        List<String> statusList = Arrays.stream(status).map(Enum::toString).collect(Collectors.toList());
        String commaSeparated=String.join(",",statusList);

        HttpResponse<List<Pet>> response =
                        createRequest(Service.FindByStatus).
                                queryString("status",commaSeparated).
                                asObject(new GenericType<>() {
                                });
        return response;
    }

    public HttpResponse<ApiResponse> UploadImage(long id, String metadata, File file) {

        HttpResponse<ApiResponse> response =
                        createRequest(Service.UploadImage,null). // Unirest fixes Content-type
                                routeParam(ROUTE_PARAM_PETID, String.valueOf(id)).
                                field("additionalMetadata",metadata).
                                field("file",file).
                                asObject(ApiResponse.class);
        return response;
    }

    // Default, use Content type application/json as we convert Objects to JSON through a mapper
    private HttpRequestWithBody createRequest(Service service) {
        return createRequest(service,"application/json");
    }

    private HttpRequestWithBody createRequest(Service service, String content_type) {

        // Setup request
        HttpRequestWithBody httpRequestWithBody = Unirest.request(service.getMethod(),service.getEndPoint());

        // Add headers
        httpRequestWithBody.header("accept", "application/json");

        if(content_type != null)
            httpRequestWithBody.header("Content-Type",content_type);

        return httpRequestWithBody;
    }
}
