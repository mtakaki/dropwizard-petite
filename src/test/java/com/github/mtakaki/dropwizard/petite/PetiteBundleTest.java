package com.github.mtakaki.dropwizard.petite;

import static org.mockito.Mockito.mock;
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

        final MetricRegistry metricRegistry = mock(MetricRegistry.class);
        when(this.environment.metrics()).thenReturn(metricRegistry);
        final Timer automagicTimer = mock(Timer.class);
        when(metricRegistry
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
    }
}