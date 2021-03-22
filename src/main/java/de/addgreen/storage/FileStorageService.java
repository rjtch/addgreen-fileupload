package de.addgreen.storage;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileStorageService {
    Path load(String filename);

    Resource loadAsResource(String filename);

    List<Path>loadAll();

    void init();

    List<String> listFilesUsingFileWalk(String dir) throws IOException;
}
