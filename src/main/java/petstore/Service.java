package petstore;

import static petstore.Method.*;

public enum Service {

    UploadImage(POST,"/pet/{petId}/uploadImage"),
    AddPet(POST,"/pet"),
    Update(PUT,"/pet"),
    UpdateWithForm(POST,"/pet/{petId}"),
    FindByStatus(GET,"/pet/findByStatus"),
    FindById(GET,"/pet/{petId}"),
    Delete(DELETE,"/pet/{petId}");

    private final String endPoint;
    private final Method method;

    public String getEndPoint() {
        return endPoint;
    }

    public String getMethod() {
        return method.name();
    }

    Service(Method method, String endPoint) {
        this.method=method;
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return getEndPoint();
    }

}
