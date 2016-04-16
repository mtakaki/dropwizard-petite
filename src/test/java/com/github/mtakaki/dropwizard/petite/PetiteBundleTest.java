package com.github.mtakaki.dropwizard.petite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

@RunWith(MockitoJUnitRunner.class)
public class PetiteBundleTest {
    private static class TestConfiguration extends Configuration {
    }

    private PetiteBundle<TestConfiguration> petiteBundle;

    @Mock
    private Bootstrap<?> bootstrap;
    @Mock
    private Environment environment;
    @Mock
    private MetricRegistry metricRegistry;

    @Before
    public void setup() {
        this.petiteBundle = new PetiteBundle<PetiteBundleTest.TestConfiguration>() {
            @Override
            protected PetiteConfiguration getConfiguration(final TestConfiguration configuration) {
                final PetiteConfiguration petite = new PetiteConfiguration();
                petite.setAutomagicConfigurator(true);
                petite.setRegisterSelf(true);
                petite.setUseFullTypeNames(true);
                petite.setUseMetrics(false);
                return petite;
            }
        };

        when(this.environment.metrics()).thenReturn(this.metricRegistry);
        final Timer automagicTimer = mock(Timer.class);
        when(this.metricRegistry
                .timer(MetricRegistry.name(PetiteConfiguration.class, "automagicConfigurator")))
                        .thenReturn(automagicTimer);
        final Timer.Context automagicTimerContext = mock(Timer.Context.class);
        when(automagicTimer.time()).thenReturn(automagicTimerContext);
        final LifecycleEnvironment lifeCycle = mock(LifecycleEnvironment.class);
        when(this.environment.lifecycle()).thenReturn(lifeCycle);
    }

    @Test
    public void testInitialize() {
        this.petiteBundle.initialize(this.bootstrap);
    }

    @Test
    public void testRun() throws Exception {
        this.petiteBundle.run(new TestConfiguration(), this.environment);

        verify(this.environment, times(1)).metrics();
        verify(this.environment, times(1)).lifecycle();
        verify(this.metricRegistry, times(1))
                .timer(MetricRegistry.name(PetiteConfiguration.class, "automagicConfigurator"));
    }

    @Test
    public void testRunWithoutAutomagicConfigurator() throws Exception {
        this.petiteBundle = new PetiteBundle<PetiteBundleTest.TestConfiguration>() {
            @Override
            protected PetiteConfiguration getConfiguration(final TestConfiguration configuration) {
                final PetiteConfiguration petite = new PetiteConfiguration();
                petite.setAutomagicConfigurator(false);
                petite.setRegisterSelf(true);
                petite.setUseFullTypeNames(true);
                petite.setUseMetrics(false);
                return petite;
            }
        };

        this.petiteBundle.run(new TestConfiguration(), this.environment);

        verify(this.environment, times(1)).metrics();
        verify(this.environment, times(1)).lifecycle();
        verify(this.metricRegistry, times(0))
                .timer(MetricRegistry.name(PetiteConfiguration.class, "automagicConfigurator"));
    }

    @Test
    public void testGetPetiteContainer() throws Exception {
        this.petiteBundle.run(new TestConfiguration(), this.environment);

        assertThat(this.petiteBundle.getPetiteContainer()).isNotNull();
    }
}