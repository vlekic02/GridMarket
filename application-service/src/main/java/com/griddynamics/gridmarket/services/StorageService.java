package com.griddynamics.gridmarket.services;

import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  Path save(MultipartFile file, String fileName, long userId);

  void delete(Path path);

  void deleteByUser(long userId);
}
