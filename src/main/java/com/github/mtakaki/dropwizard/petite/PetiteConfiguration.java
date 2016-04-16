package com.github.mtakaki.dropwizard.petite;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import lombok.Data;

@Data
public class PetiteConfiguration {
    private boolean useFullTypeNames;
    private boolean automagicConfigurator;
    private boolean registerSelf = true;
    private boolean useMetrics = false;

    public PetiteContainer build(final MetricRegistry metricRegistry) {
        // We can use either our own extension that measures the performance of
        // the container or the original one.
        final PetiteContainer petite = this.useMetrics
                ? new MonitoredPetiteContainer(metricRegistry) : new PetiteContainer();

        // Enables to use Class full names when referencing them for injection.
        // This will prevent us of having conflicts when generic class name
        // exists.
        petite.getConfig().setUseFullTypeNames(this.useFullTypeNames);

        // This enables automatic registration of PetiteBeans.
        if (this.automagicConfigurator) {
            try (Timer.Context beansContext = metricRegistry
                    .timer(MetricRegistry.name(PetiteConfiguration.class, "automagicConfigurator"))
                    .time()) {
                final AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
                petiteConfigurator.configure(petite);
            }
        }

        if (this.registerSelf) {
            petite.addSelf();
        }

        return petite;
    }
}