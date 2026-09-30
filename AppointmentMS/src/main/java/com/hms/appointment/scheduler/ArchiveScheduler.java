package com.hms.appointment.scheduler;

import com.hms.appointment.entity.Prescription;
import com.hms.appointment.repository.PrescriptionRepository;
import com.hms.appointment.service.S3ArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveScheduler {

    private final PrescriptionRepository prescriptionRepository;
    private final S3ArchiveService s3ArchiveService;

    @Value("${archive.older-than-days:30}")
    private int olderThanDays;

    // Testing : "0 */1 * * * *" → 1 minute
    @Scheduled(cron = "0 0 2 1 * *")
//    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    @Async
    public void archiveOldPrescriptions() {

        LocalDate cutoffDate = LocalDate.now().minusDays(olderThanDays);
        log.info("Archive job start. Cutoff date: {}", cutoffDate);

        List<Prescription> oldPrescriptions =
                prescriptionRepository.findByArchivedFalseAndPrescriptionDateBefore(cutoffDate);

        int success = 0, failed = 0;

        for (Prescription prescription : oldPrescriptions) {
            try {
                String s3Key = s3ArchiveService.uploadToS3(prescription);
                prescription.setArchived(true);
                prescription.setS3Key(s3Key);
                prescriptionRepository.save(prescription);
                success++;
            } catch (Exception e) {
                log.error("Prescription {} archive fail: {}", prescription.getId(), e.getMessage());
                failed++;
            }
        }

        log.info("Archive job End. Success: {}, Failed: {}", success, failed);
    }
}