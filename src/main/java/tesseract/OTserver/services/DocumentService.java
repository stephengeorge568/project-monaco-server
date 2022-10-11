package tesseract.OTserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tesseract.OTserver.mappers.FileMapper;
import tesseract.OTserver.objects.Document;
import tesseract.OTserver.objects.OpenDocumentRequest;
import tesseract.OTserver.objects.CreateDocumentRequest;
import tesseract.OTserver.objects.GetDocumentResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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

    @Autowired
    private OtService otService;

    public GetDocumentResponse getDocumentById(Long id, String password) throws IOException {

        GetDocumentResponse response = fileMapper.getDocumentById(id);

        if (!validatePassword(password, response.getPassword_hash())) throw new IOException("Wrong password");

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

    public void saveDocumentModel(Long id, String password) throws IOException {
        GetDocumentResponse response = this.getDocumentById(id, password);

        String model = response.getModel();

        if (this.otService.getDocuments().get(id).isHasChanged()) {
            String filepath = env.getProperty("document.directory.path") + id + '.' + response.getFiletype();

            FileWriter writer = new FileWriter(filepath, false);
            writer.write(model);
            this.otService.getDocuments().get(id).setHasChanged(false);
            writer.close();
        }
    }


    public void openAndAuthenticateDocument(OpenDocumentRequest request) throws IOException {
        // Get document from file system and database
        GetDocumentResponse getDocumentResponse = getDocumentById(request.getId(), request.getPassword());

        // Ensure no document in list shares request's id
        if (this.otService.isDocumentPresent(getDocumentResponse.getId())) throw new IOException("Document cannot open because it is already open.");

        validatePassword(request.getPassword(), getDocumentResponse.getPassword_hash());

        // Create document object in list of documents that are open for transformation
        Document document = new Document(getDocumentResponse.getId());
        this.otService.getDocuments().put(document.getId(), document);
    }

    public boolean validatePassword(String givenPassword, String storedHashedPassword) {
        return passwordEncoder.matches(givenPassword, storedHashedPassword);
    }

}
