package petstore;


import kong.unirest.HttpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import petstore.mappers.ApiResponse;
import petstore.mappers.Pet;
import petstore.mappers.Status;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PetTests extends PetTestsBase {


    // Pet data for each test to use (unique ID's through Atomic Long in base test class)
    Pet emptyTestPet; // No data added
    Pet fullTestPet;  // Data added


    // Random input data for each test
    // id is increased by 1
    @BeforeEach
    void testSetup() {
        emptyTestPet = createEmptyPetObject();
        fullTestPet = createFullPetObject();
    }


    @Test
    void addPetTest() {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(emptyTestPet);
        assertThat(petHttpResponse.isSuccess()).as("Add Pet failed").isTrue();

        // Fetch body and check
        Pet returned = petHttpResponse.getBody();
        assertThat(returned).usingRecursiveComparison().isEqualTo(emptyTestPet);
    }


    @Test
    void addPetWithDataTest() {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(fullTestPet);
        assertThat(petHttpResponse.isSuccess()).as("Add Pet failed").isTrue();

        // Fetch body and check
        Pet responseBody = petHttpResponse.getBody();
        assertThat(responseBody).usingRecursiveComparison().isEqualTo(fullTestPet);

        // Fetch byId
        petHttpResponse = petApi.FindById(fullTestPet.getId());
        Pet theAdded = petHttpResponse.getBody();
        assertThat(theAdded).usingRecursiveComparison().isEqualTo(fullTestPet);

    }


    @Test
    void updatePetTest() {
        // Add Pet
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(fullTestPet);
        assertThat(petHttpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // Modify fields using a new Pet but remember to transfer the original id;
        Pet updatedPet = createFullPetObject();
        updatedPet.setStatus(Status.sold);  // Change the status too
        updatedPet.setId(fullTestPet.getId());

        // Update with this Pet data
        petHttpResponse = petApi.Update(updatedPet);
        assertThat(petHttpResponse.isSuccess()).as("Update Pet failed").isTrue();

        // Check response
        Pet responseBody = petHttpResponse.getBody();
        assertThat(responseBody).usingRecursiveComparison().isEqualTo(updatedPet);

        // Also fetch it
        petHttpResponse = petApi.FindById(fullTestPet.getId());
        Pet fetched = petHttpResponse.getBody();
        assertThat(fetched).usingRecursiveComparison().isEqualTo(updatedPet);

    }

    @Test
    void updateWithFormPetTest() {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(fullTestPet);
        assertThat(petHttpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // AddPet tested above, accept Status OK
        Pet originalPet = petHttpResponse.getBody();

        String newName = RandomStringUtils.randomAlphabetic(15);
        Status newStatus = Status.sold;

        // Update
        HttpResponse<ApiResponse> httpResponse = petApi.UpdateWithFormData(fullTestPet.getId(), newName, newStatus);
        assertThat(httpResponse.isSuccess()).as("Update Pet With Form failed").isTrue();
        ApiResponse apiResponse = httpResponse.getBody();
        assertThat(apiResponse.getCode()).as("API response not 200").isEqualTo(200);

        // Get the Pet to check
        petHttpResponse = petApi.FindById(originalPet.getId());
        Pet modified = petHttpResponse.getBody();

        // Everything should be same apart from "name" and "status"....
        assertThat(modified).usingRecursiveComparison().ignoringFields("name", "status").isEqualTo(originalPet);

        // Check the modified....
        assertThat(modified.getStatus()).isEqualTo(newStatus);
        assertThat(modified.getName()).isEqualTo(newName);

    }

    @Test
    void findPetByIdTest() {
        // Add
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        assertThat(httpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // Find it
        httpResponse = petApi.FindById(fullTestPet.getId());
        assertThat(httpResponse.isSuccess()).as("FindById call failed").isTrue();
        Pet myPet = httpResponse.getBody();

        assertThat(myPet).usingRecursiveComparison().isEqualTo(fullTestPet);
    }

    @Test
    void findPetByStatusTest() {
        // Add
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        assertThat(httpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // Find my pet in the status query
        HttpResponse<List<Pet>> listHttpResponse = petApi.FindPetsByStatus(fullTestPet.getStatus());
        assertThat(httpResponse.isSuccess()).as("FindPetsByStatus call failed").isTrue();

        List<Pet> pets = listHttpResponse.getBody();

        Pet myPet = pets.stream().filter(p -> p.getId() == fullTestPet.getId()).findFirst().orElse(null);
        assertThat(myPet).usingRecursiveComparison().isEqualTo(fullTestPet);

        // Do not find my pet in another status search
        listHttpResponse = petApi.FindPetsByStatus(Status.sold);
        assertThat(httpResponse.isSuccess()).as("FindPetsByStatus call failed").isTrue();
        pets = listHttpResponse.getBody();

        long count = pets.stream().filter(p -> p.getId() == fullTestPet.getId()).count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findPetsByMultipleStatusTest() {
        // Create additional Testdata for an extra Pet in this test, but status=pending
        Pet extraPet = createFullPetObject();
        extraPet.setStatus(Status.pending);

        // Add standard test Pet
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        assertThat(httpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // Add the extra Pet
        httpResponse = petApi.AddPet(extraPet);
        assertThat(httpResponse.isSuccess()).as("Add Extra Pet failed").isTrue();

        // Get pets with different statuses (Status strings joined in petApi)
        HttpResponse<List<Pet>> listHttpResponse = petApi.FindPetsByStatus(extraPet.getStatus(), fullTestPet.getStatus());
        assertThat(httpResponse.isSuccess()).as("FindPetsByStatus call failed").isTrue();

        // Lots of pets...
        List<Pet> pets = listHttpResponse.getBody();

        // Filter...
        Pet myPet1 = pets.stream().filter(p -> p.getId() == fullTestPet.getId()).findFirst().orElse(null);
        assertThat(myPet1).usingRecursiveComparison().isEqualTo(fullTestPet);

        Pet myPet2 = pets.stream().filter(p -> p.getId() == extraPet.getId()).findFirst().orElse(null);
        assertThat(myPet2).usingRecursiveComparison().isEqualTo(extraPet);
    }

    @Test
    void deletePetTest() {
        // Add
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        assertThat(httpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // Find it
        httpResponse = petApi.FindById(fullTestPet.getId());
        assertThat(httpResponse.isSuccess()).as("FindById call failed").isTrue();

        // Eliminate it
        HttpResponse<ApiResponse> delete = petApi.Delete(fullTestPet.getId());
        assertThat(delete.isSuccess()).as("FindById call failed").isTrue();
        ApiResponse apiResponse = delete.getBody();
        assertThat(apiResponse.getCode()).isEqualTo(200);

        // Find it should fail && 404
        httpResponse = petApi.FindById(fullTestPet.getId());
        assertThat(httpResponse.isSuccess()).as("FindById call succeeded").isFalse();
        assertThat(httpResponse.getStatus()).as("Wrong status").isEqualTo(404);

        // Delete again should fail && 404
        delete = petApi.Delete(fullTestPet.getId());
        assertThat(delete.isSuccess()).as("FindById call succeeded").isFalse();
        assertThat(delete.getStatus()).as("Wrong status").isEqualTo(404);

    }

    @Test
    void uploadImageTest() {

        // Get Image & size
        String resourceFile = "harry.jpg";
        java.net.URL resource = getClass().getClassLoader().getResource(resourceFile);

        if(resource==null)
            throw new RuntimeException("Resource File not found for use in test: "+resourceFile);

        File file;
        long bytes;


        file = new File(resource.getPath());
        bytes = file.length();

        // Add pet
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        assertThat(httpResponse.isSuccess()).as("Add Full Pet failed").isTrue();

        // Upload
        String metadata = RandomStringUtils.randomAlphanumeric(10, 30);
        HttpResponse<ApiResponse> response = petApi.UploadImage(fullTestPet.getId(), metadata, file);
        assertThat(response.isSuccess()).as("UploadImage call failed").isTrue();

        // Check
        ApiResponse apiResponse = response.getBody();
        assertThat(apiResponse.getCode()).isEqualTo(200);
        String msg = apiResponse.getMessage();
        assertThat(msg).contains("additionalMetadata: " + metadata);
        assertThat(msg).contains(resourceFile);
        assertThat(msg).contains(bytes + " bytes");
    }
}
