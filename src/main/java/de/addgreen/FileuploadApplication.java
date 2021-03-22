package de.addgreen;

import de.addgreen.storage.FileStorageService;
import de.addgreen.storage.StorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FileuploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileuploadApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FileStorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
