package com.github.mtakaki.dropwizard.petite;

import com.codahale.metrics.MetricRegistry;

import io.dropwizard.lifecycle.Managed;

import jodd.petite.PetiteContainer;
import lombok.Getter;

public class PetiteManaged implements Managed {
    @Getter
    private final PetiteContainer petite;

    public PetiteManaged(final PetiteConfiguration configuration,
            final MetricRegistry metricRegistry) {
        // We need to initialize the container at this moment. If we initialize
        // it in the start() method, the container won't be available in the
        // run() method in the application.
        this.petite = configuration.build(metricRegistry);
        if (configuration.isUseFullTypeNames()) {
            this.petite.addBean(MetricRegistry.class.getName(), metricRegistry);
        } else {
            this.petite.addBean(MetricRegistry.class.getSimpleName(), metricRegistry);
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