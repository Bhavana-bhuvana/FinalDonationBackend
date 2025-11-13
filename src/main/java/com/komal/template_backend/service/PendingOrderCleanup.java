package com.komal.template_backend.service;
import com.komal.template_backend.repo.DonationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PendingOrderCleanup {

    @Autowired
    private DonationRepo donationRepo;

    // Run every 5 minutes to remove PENDING orders older than 10 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void removeStalePendingOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        int deletedCount = donationRepo.deleteByStatusAndDonationDateBefore("PENDING", cutoff);
        if (deletedCount > 0) {
            System.out.println("🧹 Cleaned up " + deletedCount + " stale pending orders");
        }
    }
}



