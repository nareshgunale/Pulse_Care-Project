package com.hms.appointment.service.serviceImp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.entity.AppointmentRecord;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.exception.HMSException;
import com.hms.appointment.service.S3ArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ArchiveServiceImp implements S3ArchiveService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private String buildS3Key(Prescription prescription) {
        LocalDate date = prescription.getPrescriptionDate();
        return String.format("prescriptions/%d/%02d/prescription-%d.json",
                date.getYear(),
                date.getMonthValue(),
                prescription.getId());
    }

    public String uploadToS3(Prescription prescription) {
        try {
            PrescriptionDTO dto = prescription.toDTO();
            dto.setArchived(true);
            dto.setS3Key(buildS3Key(prescription));

            String key = buildS3Key(prescription);
            String json = objectMapper.writeValueAsString(dto);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType("application/json")
                            .build(),
                    RequestBody.fromString(json)
            );
            return key;

        } catch (Exception e) {
            log.error("S3 upload failed for prescription {}: {}", prescription.getId(), e.getMessage());
            throw new HMSException("S3_UPLOAD_FAILED");
        }
    }

    public PrescriptionDTO fetchFromS3(String s3Key) {
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .build()
            );
            return objectMapper.readValue(
                    response.asUtf8String(),
                    PrescriptionDTO.class
            );

        } catch (Exception e) {
            log.error("S3 fetch failed for key {}: {}", s3Key, e.getMessage());
            throw new HMSException("S3_FETCH_FAILED");
        }
    }

    public void deleteFromS3(String s3Key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build()
        );
        log.info(" Delete form S3: {}", s3Key);
    }
}