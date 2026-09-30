package com.example.profile.service;

import com.example.profile.dto.FileResponseDTO;
import com.example.profile.entity.FileData;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileResponseDTO upload(MultipartFile file, Long oldFileId);
    FileData getFile(Long id);
}
