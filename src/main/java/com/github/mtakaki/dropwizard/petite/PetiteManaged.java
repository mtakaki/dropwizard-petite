package com.github.mtakaki.dropwizard.petite;

import com.codahale.metrics.MetricRegistry;

import io.dropwizard.lifecycle.Managed;

import jodd.petite.PetiteContainer;
import lombok.Getter;

public class PetiteManaged implements Managed {
    private final PetiteConfiguration configuration;
    private final MetricRegistry metricRegistry;
    @Getter
    private final PetiteContainer petite;

    public PetiteManaged(final PetiteConfiguration configuration,
            final MetricRegistry metricRegistry) {
        this.configuration = configuration;
        this.metricRegistry = metricRegistry;

        this.petite = this.configuration.build(this.metricRegistry);
        if (this.configuration.isUseFullTypeNames()) {
            this.petite.addBean(MetricRegistry.class.getName(), this.metricRegistry);
        } else {
            this.petite.addBean(MetricRegistry.class.getSimpleName(), this.metricRegistry);
        }
    }

    @Override
    public void start() throws Exception {
        // We don't have anything to do at this moment.
    }

    @Override
    public void stop() throws Exception {
        this.petite.shutdown();
    }
}