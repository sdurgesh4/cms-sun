package com.inturn.suncomputer.installment.scheduler;

import com.inturn.suncomputer.installment.service.InstallmentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InstallmentScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    InstallmentScheduler.class
            );

    private final InstallmentService installmentService;

    public InstallmentScheduler(
            InstallmentService installmentService
    ) {

        this.installmentService =
                installmentService;
    }


    /*
     * Runs every morning at 8:00 AM IST.
     */

    @Scheduled(
            cron = "0 0 8 * * *",
            zone = "Asia/Kolkata"
    )
    public void markOverdueInstallments() {

        log.info(
                "Starting overdue installment check..."
        );

        try {

            installmentService
                    .updateOverdueInstallments();

            log.info(
                    "Overdue installment check completed successfully."
            );

        } catch (Exception exception) {

            log.error(
                    "Error while updating overdue installments.",
                    exception
            );
        }
    }
}