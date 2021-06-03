package de.addgreen.storageController;

import de.addgreen.model.FileData;
import de.addgreen.storage.FileStorageService;
import de.addgreen.storageException.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;

@Controller
@Slf4j
public class StorageController {
    private final FileStorageService fileStorageService;

    @Autowired
    public StorageController(FileStorageService storageService) {
        this.fileStorageService = storageService;
    }

    @GetMapping("/prices")
    @ResponseBody
    public ResponseEntity<Resource> getPricesFile( @RequestParam(name = "date")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response)  throws IOException{
        return getResourceResponseEntity(date, request, response);
    }

    @GetMapping("/stations")
    @ResponseBody
    public ResponseEntity<Resource> getStationsFile(@RequestParam(name = "date")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response)  throws IOException{
        return getResourceResponseEntity(date, request, response);
    }

    private ResponseEntity<Resource> getResourceResponseEntity(@RequestParam(name = "date")
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) throws IOException {
        String year = StringUtils.leftPad(String.valueOf(date.getYear()), 2, "0");
        String  month = StringUtils.leftPad(String.valueOf(date.getMonthValue()), 2, "0");
        String day = StringUtils.leftPad(String.valueOf(date.getDayOfMonth()), 2, "0");

        String fileName = "";
        if (request.getRequestURI().contains("prices")) {
            fileName = MessageFormat.format("{0}-prices.csv",day);
        } else if (request.getRequestURI().contains("stations")) {
            fileName = MessageFormat.format("{0}-stations.csv",day);
        } else {
            throw new IOException("Path doesn't match");
        }

        String filePath = MessageFormat.format("{0}/{1}/{2}", year, month, fileName);
        Resource resource = fileStorageService.loadAsResource(filePath);
        String contentType = "";

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            throw new IOException("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/csv";
        }

        log.info("Trying to download the file " + fileName + " with Status " +  response.getStatus());
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
