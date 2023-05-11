package job.metrics;

import io.opentelemetry.api.common.Attributes;

import java.util.Random;

public class App {
    public static void main(String[] args)
            throws InterruptedException {

        System.out.println("doing some job things...");
        Thread.sleep(1000);
        var rnd = new Random();
        var recordCount = rnd.nextInt(100000) + 10000;
        var inserts = rnd.nextInt(500);
        var deletes = rnd.nextInt(100);
        var updates = rnd.nextInt(1000);
        var skipped = rnd.nextInt(10) > 8 ? rnd.nextInt(50000) + 50000 : rnd.nextInt(500);
        var status = rnd.nextInt(2) == 1 ? 10 : 20;

        var attributes = Attributes
                .builder()
                .put("job-id", "alpha-omega")
                .put("source", "a")
                .put("target", "b")
                .build();

        System.out.println("Finished doing some job things!");

        System.out.println("Reporting status & metrics...");
        MyAppCounters.setStatus(status);
        MyAppCounters.setProcessedRecords(recordCount, attributes);
        MyAppCounters.setInsertedRecords(inserts, attributes);
        MyAppCounters.setDeletedRecords(deletes, attributes);
        MyAppCounters.setUpdatedRecords(updates, attributes);
        MyAppCounters.setSkippedRecords(skipped, attributes);
        System.out.println("Finished reporting status & metrics!");

        // this sleep shows, that no metrics get "logged" to this point
        Thread.sleep(3000);

        // NOW metrics will be flushed:
        OpenTelemetryConfiguration.shutdown();

        Thread.sleep(1000);
        System.out.println("Finished!");
    }
}

