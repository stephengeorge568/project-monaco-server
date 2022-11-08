package tesseract.OTserver.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tesseract.OTserver.exceptions.*;
import tesseract.OTserver.mappers.FileMapper;
import tesseract.OTserver.objects.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentService {

    private static final Logger logger = LogManager.getLogger(DocumentService.class);

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    @Autowired
    private OtService otService;

    public GetDocumentResponse getDocumentById(Long id, String password) throws IOException {

        // Get document metadata
        GetDocumentResponse response = fileMapper.getDocumentById(id);
        if (response == null) throw new DocumentNotFoundException(id);

        validatePassword(password, response.getPassword_hash(), id);

        String documentDirectoryPath = env.getProperty("document.directory.path");

        // Get document model
        String filepath = documentDirectoryPath + response.getId() + '.' + response.getFiletype();
        File file = new File(filepath);
        if (file.exists()) {
            Path path = Path.of(filepath);
            response.setModel(Files.readString(path));
        } else {
            logger.error("Failed to open and read file [{}].", filepath);
            throw new DocumentNotFoundInFilesystemException(id);
        }

        // Set password_hash to null, no reason to send this back to client
        response.setPassword_hash(null);

        return response;
    }

    public GetDocumentMetaResponse getDocumentById(Long id) throws IOException {

        // Get document metadata
        GetDocumentMetaResponse response = fileMapper.getDocumentMetaById(id);
        if (response == null) throw new DocumentNotFoundException(id);

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

        try {
            if (!file.createNewFile()) throw new DocumentException("Failed to create file on filesystem for document " + request.getFilename());
        } catch (IOException e) {
            throw new DocumentException("Failed to create file on filesystem for document " + request.getFilename());
        }

        return id;
    }

    public void saveDocumentModel(Long id, String password) {
        try {
            GetDocumentResponse response = this.getDocumentById(id, password);

            String model = this.otService.getDocuments().get(id).getModel();

            if (this.otService.getDocuments().get(id).isHasChanged()) {
                String filepath = env.getProperty("document.directory.path") + id + '.' + response.getFiletype();
                logger.info(filepath);
                FileWriter writer = new FileWriter(filepath, false);
                writer.write(model);
                this.otService.getDocuments().get(id).setHasChanged(false);
                writer.close();
            }
        } catch (Exception e) {
            throw new DocumentException("Failed to write to document " + id + ".");
        }

    }


    public void openAndAuthenticateDocument(OpenDocumentRequest request) throws IOException {
        // Get document from file system and database
        // This also validates password.
        // TODO use filemapper call instead of calling service method?
        GetDocumentResponse getDocumentResponse = getDocumentById(request.getId(), request.getPassword());

        // Ensure no document in list shares request's id
        if (!this.otService.isDocumentPresent(getDocumentResponse.getId())) {
            // Create document object in list of documents that are open for transformation
            Document document = new Document(getDocumentResponse.getId());
            this.otService.getDocuments().put(document.getId(), document);
        }

    }

    public void validatePassword(String givenPassword, String storedHashedPassword, Long id) {
        if (!passwordEncoder.matches(givenPassword, storedHashedPassword)) throw new DocumentWrongPasswordException(id);
    }

}
