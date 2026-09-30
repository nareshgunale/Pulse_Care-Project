package com.example.profile.service.serviceImp;

import com.example.profile.dto.FileResponseDTO;
import com.example.profile.entity.FileData;
import com.example.profile.repository.FileRepository;
import com.example.profile.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileServiceImp implements FileService {

    private final FileRepository fileRepository;

    @Override
    public FileResponseDTO upload(MultipartFile file, Long oldFileId) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is required");
            }
            if (!"image/png".equals(file.getContentType())) {
                throw new RuntimeException("Only PNG image allowed");
            }
            FileData fileData;
            // existing image update
            if (oldFileId != null) {
                fileData = fileRepository.findById(oldFileId)
                        .orElse(new FileData());
            } else {
                fileData = new FileData();
            }
            fileData.setName(file.getOriginalFilename());
            fileData.setType(file.getContentType());
            fileData.setData(file.getBytes());
            FileData savedFile = fileRepository.save(fileData);
            return new FileResponseDTO(savedFile.getId());
        } catch (Exception e) {
            throw new RuntimeException(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "File upload failed"
            );
        }
    }

    @Override
    public FileData getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("File not found"));
    }
}


