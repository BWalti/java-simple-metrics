package job.metrics;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.ObservableDoubleGauge;

public class MyAppCounters {
    private static final MyAppCounters Instance = new MyAppCounters();

    private final LongCounter processed;
    private final LongCounter inserts;
    private final LongCounter deletions;
    private final LongCounter updates;
    private final LongCounter skipped;
    private final ObservableDoubleGauge status;
    private long statusValue;

    private MyAppCounters() {
        var meter = OpenTelemetryConfiguration.getMeter();

        // Build counter e.g. LongCounter
        // see also: https://opentelemetry.io/docs/instrumentation/java/manual/#metrics
        this.processed = meter
                .counterBuilder("job_processed_records")
                .setDescription("Processed records")
                .setUnit("1")
                .build();

        this.inserts = meter
                .counterBuilder("job_inserted_records")
                .setDescription("Inserted records")
                .setUnit("1")
                .build();

        this.deletions = meter
                .counterBuilder("job_deleted_records")
                .setDescription("Deleted records")
                .setUnit("1")
                .build();

        this.updates = meter
                .counterBuilder("job_updated_records")
                .setDescription("Updated records")
                .setUnit("1")
                .build();

        this.skipped = meter
                .counterBuilder("job_skipped_records")
                .setDescription("Skipped records")
                .setUnit("1")
                .build();

        this.status = meter.gaugeBuilder("job_process_status")
                .setDescription("Status of the job process")
                .buildWithCallback(result -> result.record(this.statusValue));
    }

    public static void setProcessedRecords(long processedRecords, Attributes attributes) {
        Instance.processed.add(processedRecords, attributes);
    }

    public static void setInsertedRecords(long value, Attributes attributes) {
        Instance.inserts.add(value, attributes);
    }

    public static void setDeletedRecords(long value, Attributes attributes) {
        Instance.deletions.add(value, attributes);
    }

    public static void setUpdatedRecords(long value, Attributes attributes) {
        Instance.updates.add(value, attributes);
    }

    public static void setSkippedRecords(long value, Attributes attributes) {
        Instance.skipped.add(value, attributes);
    }

    public static void setStatus(long status) {
        Instance.statusValue = status;
    }
}
