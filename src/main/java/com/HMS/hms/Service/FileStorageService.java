package com.HMS.hms.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // To generate unique file names

@Service
public class FileStorageService {

    private final Path fileStorageLocation; // The root directory for uploads

    // Constructor: Spring injects the value from application.properties
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // Create the upload directory if it does not exist
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException | SecurityException ex) { // Multi-catch for both possible exceptions
            throw new RuntimeException("Could not create the upload directory: " + uploadDir, ex);
        }
    }

    /**
     * Stores an uploaded MultipartFile to the configured directory.
     * Generates a unique file name to prevent conflicts.
     * @param file The MultipartFile to store.
     * @return The unique file name (e.g., UUID_originalFileName.ext) under which it was stored.
     * @throws IOException if the file cannot be stored.
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name to prevent directory traversal attacks
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        // Generate a unique file name using UUID to prevent overwrites
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName.substring(originalFileName.lastIndexOf(".") + 1); // UUID_extension
        // Or UUID.randomUUID().toString() + "_" + originalFileName; // UUID_originalfilename.ext if you want full name

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new IOException("Filename contains invalid path sequence " + fileName);
            }

            // Resolve the target path for the file
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            // Copy file to the target location (replaces existing file with same name if it exists)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName; // Return the unique file name to be saved in the database
        } catch (IOException ex) {
            throw new IOException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    /**
     * Resolves the full path for a stored file, useful for serving or accessing.
     * @param fileName The unique file name.
     * @return The absolute Path to the file.
     */
    public Path loadFileAsResource(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }
}