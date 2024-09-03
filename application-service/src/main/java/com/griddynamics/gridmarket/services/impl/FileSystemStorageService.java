package com.griddynamics.gridmarket.services.impl;

import com.griddynamics.gridmarket.exceptions.InternalServerException;
import com.griddynamics.gridmarket.services.StorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageService.class);

  private final Path rootDirectory;

  public FileSystemStorageService(@Value("applications") String rootDirectory) {
    this.rootDirectory = Path.of(rootDirectory);
  }

  @Override
  public Path save(MultipartFile file, String fileName, long userId) {
    Path userDirectory = rootDirectory.resolve(String.valueOf(userId));
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

  @Override
  public void delete(Path path) {
    try {
      Files.delete(path);
    } catch (IOException exception) {
      LOGGER.error("Failed to delete file from disk !", exception);
    }
  }

  @Override
  public void deleteByUser(long userId) {
    try {
      FileSystemUtils.deleteRecursively(rootDirectory.resolve(String.valueOf(userId)));
    } catch (IOException exception) {
      LOGGER.error("Failed to delete user applications folder !", exception);
    }
  }

  @Override
  public FileSystemResource getFileByPath(String path) {
    Path filePath = Path.of(path);
    if (!Files.exists(filePath)) {
      LOGGER.error("File path exists in database but not on filesystem ! Path: {}", path);
      throw new InternalServerException("File doesn't exist on file system, contact administrator");
    }
    return new FileSystemResource(filePath);
  }
}
