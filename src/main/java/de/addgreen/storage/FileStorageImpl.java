package de.addgreen.storage;

import de.addgreen.storageException.StorageException;
import de.addgreen.storageException.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileStorageImpl implements FileStorageService{
    private final Path rootLocation;
    private final String pricesLocation = "prices";
    private final String stationsLocation = "stations";

    @Autowired
    public FileStorageImpl(StorageProperties properties) {
        properties.setLocation(properties.getLocation());
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public Path load(String filename) {
        if (filename.contains(pricesLocation)) {
            return rootLocation.resolve(pricesLocation).resolve(filename);
        } else if (filename.contains(stationsLocation)) {
            return rootLocation.resolve(stationsLocation).resolve(filename);
        }
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not find file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not find file: " + filename, e);
        }
    }

    @Override
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        }
            catch (IOException e) {
                throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public List<String> listFilesUsingFileWalk(String dir) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            List<String> collect = stream
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            return collect;
        }
    }

    @Override
    public List<Path> loadAll() {
        try {
            Path root = Paths.get(rootLocation.toString());
            if (Files.exists(root)) {
                return Files.walk(root, 2)
                        .filter(path -> !path.equals(root))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        }
    }
}
