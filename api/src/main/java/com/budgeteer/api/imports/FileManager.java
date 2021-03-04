package com.budgeteer.api.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class FileManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Path root = Paths.get("imports");

    public FileManager() {
        init();
        // TODO: revisit with jar in mind
    }

    public void init() {
        try {
            Files.createDirectory(root);
        } catch (FileAlreadyExistsException ignored) {
            // we only want to create the directory once
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for imports!");
        }
    }
}
