package tesseract.OTserver.objects;

public class AuthenticateRequest {

    private Long id;

    private String password;

    public AuthenticateRequest(Long id, String password) {
        this.id = id;
        this.password = password;
    }

    public AuthenticateRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
