package com.griddynamics.gridmarket.services.impl;

import com.griddynamics.gridmarket.exceptions.InternalServerException;
import com.griddynamics.gridmarket.services.StorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

  private static final Path ROOT_DIRECTORY = Path.of("applications");
  private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageService.class);

  @Override
  public Path save(MultipartFile file, String fileName, long userId) {
    Path userDirectory = ROOT_DIRECTORY.resolve(String.valueOf(userId));
    if (!Files.exists(userDirectory)) {
      try {
        Files.createDirectories(userDirectory);
      } catch (IOException exception) {
        LOGGER.error("Failed to create user directory inside local filesystem !", exception);
        throw new InternalServerException("File failed to save, try again");
      }
    }
    Path filePath = userDirectory.resolve(fileName);
    try {
      file.transferTo(filePath);
    } catch (IOException exception) {
      LOGGER.error("Failed to write file to disk !", exception);
      throw new InternalServerException("File failed to save, try again");
    }
    return filePath;
  }
}
