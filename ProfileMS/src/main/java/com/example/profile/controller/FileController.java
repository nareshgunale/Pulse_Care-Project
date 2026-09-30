package com.example.profile.controller;

import com.example.profile.dto.FileResponseDTO;
import com.example.profile.entity.FileData;
import com.example.profile.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponseDTO> upload(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(required = false) Long oldFileId) throws IOException {
        return ResponseEntity.ok(fileService.upload(file, oldFileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        FileData fileData = fileService.getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileData.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileData.getName() + "\"")
                .body(fileData.getData());
    }
}