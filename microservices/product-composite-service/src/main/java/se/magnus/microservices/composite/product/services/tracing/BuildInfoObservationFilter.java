package se.magnus.microservices.composite.product.services.tracing;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import org.springframework.boot.info.BuildProperties;

public class BuildInfoObservationFilter implements ObservationFilter {

    private final BuildProperties buildProperties;

    public BuildInfoObservationFilter(final BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Override
    public Observation.Context map(final Observation.Context context) {
        return context.addLowCardinalityKeyValue(KeyValue.of("build.info", buildProperties.getVersion()));
    }
}
