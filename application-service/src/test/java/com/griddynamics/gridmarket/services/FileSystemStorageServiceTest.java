package com.griddynamics.gridmarket.services;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.services.impl.FileSystemStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

class FileSystemStorageServiceTest {

  private StorageService storageService;

  @BeforeEach
  void setup() {
    storageService = new FileSystemStorageService("applicationTest");
  }

  @AfterEach
  void cleanup() throws IOException {
    FileSystemUtils.deleteRecursively(Path.of("applicationTest"));
  }

  @Test
  void shouldCorrectlyStoreFile() {
    MultipartFile file = new MockMultipartFile(
        "Test", "Test.txt", "", "Some text".getBytes());
    storageService.save(file, file.getOriginalFilename(), 1);
    assertTrue(Files.exists(Path.of("applicationTest/1/Test.txt")));
  }
}
