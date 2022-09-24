package petstore;


import kong.unirest.HttpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import petstore.mappers.ApiResponse;
import petstore.mappers.Pet;
import petstore.mappers.Status;
import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PetTests extends PetTestsBase{


    // Pets for each test to use (unique ID's through Atomic Long)
    Pet emptyTestPet; // No data added
    Pet fullTestPet;  // Data added


    // Random input data for each test
    // id is increased by 1
    @BeforeEach
    void testSetup()
    {
        emptyTestPet = createEmptyPetObject();
        fullTestPet = createFullPetObject();
    }


    @Test
    void addPetTest()
    {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(emptyTestPet);
        Assertions.assertTrue(petHttpResponse.isSuccess(), "Add Pet failed");

        // Fetch body and check
        Pet returned = petHttpResponse.getBody();
        assertThat(returned).usingRecursiveComparison().isEqualTo(emptyTestPet);
    }


    @Test
    void addFullPetTest()
    {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(petHttpResponse.isSuccess(), "Add Pet failed");

        // Fetch body and check
        Pet returned = petHttpResponse.getBody();
        assertThat(returned).usingRecursiveComparison().isEqualTo(fullTestPet);

        // Fetch byId
        petHttpResponse = petApi.FindById(fullTestPet.getId());
        Pet modified = petHttpResponse.getBody();
    }


    @Test
    void updatePetTest()
    {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(petHttpResponse.isSuccess(), "Add Full Pet failed");
        // AddPet tested above, assume passed

        // Modify fields using a new Pet but remember to change status + id to original;
        Pet updatedPet = createFullPetObject();
        updatedPet.setStatus(Status.sold);
        updatedPet.setId(fullTestPet.getId());

        // Update
        petHttpResponse = petApi.Update(updatedPet);
        Assertions.assertTrue(petHttpResponse.isSuccess(), "Update Pet failed");
        Pet modified = petHttpResponse.getBody();
        assertThat(modified).usingRecursiveComparison().isEqualTo(updatedPet);
    }

    @Test
    void updateWithFormPetTest()
    {
        // Add
        HttpResponse<Pet> petHttpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(petHttpResponse.isSuccess(), "Add Full Pet failed");

        // AddPet tested above, accept Status OK
        Pet originalPet = petHttpResponse.getBody();

        String newName = RandomStringUtils.randomAlphabetic(15);
        Status newStatus = Status.sold;

        // Update
        HttpResponse<ApiResponse> httpResponse = petApi.UpdateWithFormData(fullTestPet.getId(),newName,newStatus);
        Assertions.assertTrue(httpResponse.isSuccess(), "Update Pet With Form failed");
        ApiResponse apiResponse = httpResponse.getBody();
        Assertions.assertEquals(200,apiResponse.getCode(),"API response not 200");

        // Get the Pet to check
        petHttpResponse = petApi.FindById(originalPet.getId());
        Pet modified = petHttpResponse.getBody();

        // Everything but name and status....
        assertThat(modified).usingRecursiveComparison().ignoringFields("name","status").isEqualTo(originalPet);

        // Check the modified....
        assertThat(modified.getStatus()).isEqualTo(newStatus);
        assertThat(modified.getName()).isEqualTo(newName);

    }

    @Test
    void findPetByIdTest()
    {
        // Add
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(httpResponse.isSuccess(), "Add Full Pet failed");

        // Find it
        httpResponse = petApi.FindById(fullTestPet.getId());
        Assertions.assertTrue(httpResponse.isSuccess(), "FindById call failed");
        Pet myPet = httpResponse.getBody();

        assertThat(myPet).usingRecursiveComparison().isEqualTo(fullTestPet);
    }

    @Test
    void findPetByStatusTest()
    {
        // Add
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(httpResponse.isSuccess(), "Add Full Pet failed");

        // Find my pet in the status query
        HttpResponse<List<Pet>> listHttpResponse = petApi.FindPetsByStatus(fullTestPet.getStatus());
        Assertions.assertTrue(httpResponse.isSuccess(), "FindPetsByStatus call failed");

        List<Pet> pets = listHttpResponse.getBody();

        Pet myPet = pets.stream().filter(p->p.getId()== fullTestPet.getId()).findFirst().get();
        assertThat(myPet).usingRecursiveComparison().isEqualTo(fullTestPet);

        // Do not find my pet in another status search
        listHttpResponse = petApi.FindPetsByStatus(Status.sold);
        Assertions.assertTrue(httpResponse.isSuccess(), "FindPetsByStatus call failed");
        pets = listHttpResponse.getBody();

        long count = pets.stream().filter(p -> p.getId() == fullTestPet.getId()).count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findPetsByMultipleStatusTest()
    {
        // Create additional Testdata for an extra Pet in this test, but status=pending
        Pet extraPet = createFullPetObject();
        extraPet.setStatus(Status.pending);

        // Add standard test Pet
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(httpResponse.isSuccess(), "Add Full Pet failed");

        // Add the extra Pet
        httpResponse = petApi.AddPet(extraPet);
        Assertions.assertTrue(httpResponse.isSuccess(), "Add Extra Pet failed");

        // Get pets with different statuses (Status strings joined in petApi)
        HttpResponse<List<Pet>> listHttpResponse = petApi.FindPetsByStatus(extraPet.getStatus(),fullTestPet.getStatus());
        Assertions.assertTrue(httpResponse.isSuccess(), "FindPetsByStatus call failed");

        // Lots of pets...
        List<Pet> pets = listHttpResponse.getBody();

        // Filter...
        Pet myPet1 = pets.stream().filter(p->p.getId()== fullTestPet.getId()).findFirst().get();
        assertThat(myPet1).usingRecursiveComparison().isEqualTo(fullTestPet);

        Pet myPet2 = pets.stream().filter(p->p.getId()==extraPet.getId()).findFirst().get();
        assertThat(myPet2).usingRecursiveComparison().isEqualTo(extraPet);
    }

    @Test
    void deletePetTest()
    {
        // Add
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(httpResponse.isSuccess(), "Add Full Pet failed");

        // Find it
        httpResponse = petApi.FindById(fullTestPet.getId());
        Assertions.assertTrue(httpResponse.isSuccess(), "FindById call failed");

        // Eliminate it
        HttpResponse<ApiResponse> delete = petApi.Delete(fullTestPet.getId());
        Assertions.assertTrue(delete.isSuccess(), "FindById call failed");
        assertThat(delete.getBody().getCode()).isEqualTo(200);

        // Find it should fail && 404
        httpResponse = petApi.FindById(fullTestPet.getId());
        Assertions.assertFalse(httpResponse.isSuccess(), "FindById call succeeded");
        Assertions.assertEquals(404,httpResponse.getStatus(), "Wrong status");

        // Delete again should fail && 404
        delete = petApi.Delete(fullTestPet.getId());
        Assertions.assertFalse(httpResponse.isSuccess(), "Delete call succeeded");
        Assertions.assertEquals(404,httpResponse.getStatus(), "Wrong status");

    }

    @Test
    void uploadImageTest()
    {
        // Get Image & size
        String resourceFile = "harry.jpg";
        java.net.URL resource = getClass().getClassLoader().getResource(resourceFile);
        File file = new File(resource.getPath());
        Long bytes = file.length();

        // Add pet
        HttpResponse<Pet> httpResponse = petApi.AddPet(fullTestPet);
        Assertions.assertTrue(httpResponse.isSuccess(), "Add Full Pet failed");

        // Upload
        String metadata = RandomStringUtils.randomAlphanumeric(10,30);
        HttpResponse<ApiResponse> response = petApi.UploadImage(fullTestPet.getId(), metadata, file);
        Assertions.assertTrue(response.isSuccess(), "UploadImage call failed");

        // Check
        ApiResponse apiResponse = response.getBody();
        assertThat(apiResponse.getCode()).isEqualTo(200);
        String msg = apiResponse.getMessage();
        assertThat(msg).contains("additionalMetadata: "+ metadata);
        assertThat(msg).contains(resourceFile);
        assertThat(msg).contains(bytes + " bytes");
    }
}
