package tesseract.OTserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor=Exception.class)
    public Long createDocument(CreateDocumentRequest request) throws IOException {
        request.setPassword_hash(passwordEncoder.encode(request.getPassword_hash()));

        String documentDirectoryPath = env.getProperty("document.directory.path");

        this.fileMapper.createDocument(request);

        Long id = request.getId();

        // Get document model
        String filepath = documentDirectoryPath + id + '.' + request.getFiletype();
        File file = new File(filepath);
        // TODO ERROR HANDLING
        file.createNewFile();
        return id;
    }

    public boolean authenticateDocument(AuthenticateRequest request) throws IOException {
        GetDocumentResponse document = getDocumentById(request.getId());
        return validatePassword(request.getPassword(), document.getPassword_hash());
    }

    public boolean validatePassword(String givenPassword, String storedHashedPassword) {
        return passwordEncoder.encode(givenPassword).equals(storedHashedPassword);
    }

}
