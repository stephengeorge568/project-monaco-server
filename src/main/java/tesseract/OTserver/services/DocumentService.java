package tesseract.OTserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tesseract.OTserver.mappers.FileMapper;
import tesseract.OTserver.objects.AuthenticateRequest;
import tesseract.OTserver.objects.CreateDocumentRequest;
import tesseract.OTserver.objects.GetDocumentResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

//    @Value("${document.directory.path}")
//    private String documentDirectoryPath;

    public GetDocumentResponse getDocumentById(Long id) throws IOException {

        GetDocumentResponse response = fileMapper.getDocumentById(id);

        String documentDirectoryPath = env.getProperty("document.directory.path");

        // Get document model
        String filepath = documentDirectoryPath + response.getId() + '.' + response.getFiletype();
        File file = new File(filepath);
        if (file.exists()) {
            Path path = Path.of(filepath);
            response.setModel(Files.readString(path));
        } else {
            throw new FileNotFoundException("File: " + filepath + " does not exist.");
        }

        // Set password_hash to null
        response.setPassword_hash(null);

        return response;
    }

    public Long createDocument(CreateDocumentRequest request) {
        request.setPassword_hash(passwordEncoder.encode(request.getPassword_hash()));
        return this.fileMapper.createDocument(request);
    }

    public boolean authenticateDocument(AuthenticateRequest request) throws IOException {
        GetDocumentResponse document = getDocumentById(request.getId());
        return validatePassword(request.getPassword(), document.getPassword_hash());
    }

    public boolean validatePassword(String givenPassword, String storedHashedPassword) {
        return passwordEncoder.encode(givenPassword).equals(storedHashedPassword);
    }

}
