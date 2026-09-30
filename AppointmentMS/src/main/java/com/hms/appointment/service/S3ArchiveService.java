package com.hms.appointment.service;

import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.entity.Prescription;

public interface S3ArchiveService {

    String uploadToS3(Prescription prescription);

     PrescriptionDTO fetchFromS3(String s3Key);
     void deleteFromS3(String s3Key);
}
