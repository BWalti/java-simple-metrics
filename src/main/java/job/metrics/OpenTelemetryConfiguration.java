package job.metrics;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class OpenTelemetryConfiguration {
    private static final OpenTelemetryConfiguration Instance = new OpenTelemetryConfiguration();

    private final Meter meter;
    private final OpenTelemetrySdk openTelemetry;

    private OpenTelemetryConfiguration() {
        Resource resource = Resource.create(
                Attributes.of(ResourceAttributes.SERVICE_NAME, "job-metrics"));

        // Create an instance of PeriodicMetricReader and configure it
        // to export via the logging exporter
        MetricReader logMetricReader =
                PeriodicMetricReader.builder(LoggingMetricExporter.create())
                        .build();

        // to export via OTLP gRPC to a Collector (using default endpoint)
        PeriodicMetricReader otlpMetricReader = PeriodicMetricReader.builder(
                OtlpGrpcMetricExporter.builder()
                        //.setEndpoint("http://localhost:4317") // defaults to: "http://localhost:4317"
                        .build()).build();

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                .registerMetricReader(otlpMetricReader)
                .registerMetricReader(logMetricReader)
                .setResource(resource)
                .build();

        this.openTelemetry = OpenTelemetrySdk.builder()
                .setMeterProvider(sdkMeterProvider)
                .buildAndRegisterGlobal();

        // Gets or creates a named meter instance
        this.meter = this.openTelemetry.meterBuilder("job-metrics")
                .setInstrumentationVersion("1.0.0")
                .build();
    }

    public static Meter getMeter(){
        return Instance.meter;
    }

    public static void shutdown() {
        Instance.openTelemetry.shutdown();
    }
}
