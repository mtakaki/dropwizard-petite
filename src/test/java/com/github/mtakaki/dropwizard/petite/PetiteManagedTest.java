package com.github.mtakaki.dropwizard.petite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.dropwizard.testing.FixtureHelpers;

import jodd.petite.PetiteContainer;

@RunWith(MockitoJUnitRunner.class)
public class PetiteManagedTest {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private PetiteManaged petiteManaged;

    private PetiteConfiguration configuration;
    private MetricRegistry metricRegistry;
    @Mock
    private PetiteContainer petite;
    @Mock
    private Timer timer;

    @Before
    public void setup() throws Exception {
        this.configuration = MAPPER.readValue(
                FixtureHelpers.fixture("config_short_name_automagic.yml"),
                PetiteConfiguration.class);
        this.metricRegistry = new MetricRegistry();
        this.petiteManaged = new PetiteManaged(this.configuration, this.metricRegistry);
    }

    @Test
    public void testStart() throws Exception {
        this.petiteManaged.start();
    }

    @Test
    public void testStop() throws Exception {
        this.petiteManaged.start();
        this.petiteManaged.stop();
    }
}