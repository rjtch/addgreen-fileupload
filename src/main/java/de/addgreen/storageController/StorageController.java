package de.addgreen.storageController;

import de.addgreen.model.FileData;
import de.addgreen.storage.FileStorageService;
import de.addgreen.storageException.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

@Controller
@Slf4j
public class StorageController {
    private final FileStorageService fileStorageService;

    @Autowired
    public StorageController(FileStorageService storageService) {
        this.fileStorageService = storageService;
    }

    @GetMapping("/prices/{year}/{month}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getPriceFile(@PathVariable String fileName,
                                                    @PathVariable String year,
                                                    @PathVariable String month,
                                                          HttpServletRequest request,
                                                 HttpServletResponse response)  throws IOException{
        log.warn("Trying to download a set of prices from the file " + fileName + " on " + year + "/" + month);
        return getResourceResponseEntity(fileName, year, month, request, response);
    }

    @GetMapping("/stations/{year}/{month}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getStationFile(@PathVariable String fileName,
                                                            @PathVariable String year,
                                                            @PathVariable String month,
                                                            HttpServletRequest request,
                                                   HttpServletResponse response)  throws IOException{
        log.warn("Trying to download a set of stations from the file " + fileName + "on " + year + "/" + month);
        return getResourceResponseEntity(fileName, year, month, request, response);
    }

    private ResponseEntity<Resource> getResourceResponseEntity(@PathVariable String fileName,
                                                               @PathVariable String year,
                                                               @PathVariable String month,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) throws IOException {
        String filePath = MessageFormat.format("{0}/{1}/{2}", year, month, fileName);
        Resource resource = fileStorageService.loadAsResource(filePath);
        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            throw new IOException("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/csv";
        }

        log.info("Trying to download a set of infos from the file " + fileName + " inç "
                + year + "/" + month + " with Status " + response.getStatus());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +  resource.getFilename() + "\"")
                .body(resource);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    private FileData pathToFileData(Path path) {
        FileData fileData = new FileData();
        String filename = path.getFileName()
                .toString();
        fileData.setFilename(filename);
        fileData.setUrl(MvcUriComponentsBuilder.fromMethodName(StorageController.class, "serveFile", filename)
                .build()
                .toString());
        try {
            fileData.setSize(Files.size(path));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }

        return fileData;
    }
}
