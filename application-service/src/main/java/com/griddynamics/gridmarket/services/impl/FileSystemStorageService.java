package com.griddynamics.gridmarket.services.impl;

import com.griddynamics.gridmarket.services.StorageService;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

@Service
public class FileSystemStorageService implements StorageService {

  private static final Path ROOT_DIRECTORY = Path.of("applications");

}
