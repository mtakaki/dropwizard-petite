package com.github.mtakaki.dropwizard.petite;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import jodd.petite.PetiteContainer;

public abstract class PetiteBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private PetiteManaged petiteManaged;

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final PetiteConfiguration petiteConfiguration = this.getConfiguration(configuration);
        this.petiteManaged = new PetiteManaged(petiteConfiguration, environment.metrics());
        environment.lifecycle().manage(this.petiteManaged);
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        // We don't have anything to do at this moment.
    }

    /**
     * Extracts the {@link PetiteConfiguration} from the given configuration
     * object.
     *
     * @param configuration
     *            The application configuration object, from where we'll get the
     *            petite configuration.
     * @return The petite configuration used to build the
     *         {@link PetiteContainer}.
     */
    protected abstract PetiteConfiguration getConfiguration(T configuration);

    public PetiteContainer getPetiteContainer() {
        return this.petiteManaged.getPetite();
    }
}