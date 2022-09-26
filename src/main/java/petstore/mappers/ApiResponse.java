package petstore.mappers;

public class ApiResponse {
    private int code;
    private String type = null;
    private String message = null;

    public int getCode() {
        return code;
    }

    public ApiResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public ApiResponse setType(String type) {
        this.type = type;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiResponse setMessage(String message) {
        this.message = message;
        return this;
    }


}
