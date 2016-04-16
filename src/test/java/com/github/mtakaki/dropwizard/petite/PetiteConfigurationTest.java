package com.github.mtakaki.dropwizard.petite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.dropwizard.testing.FixtureHelpers;

import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;

public class PetiteConfigurationTest {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private PetiteConfiguration configuration;
    private PetiteConfiguration expectedConfiguration;
    private MetricRegistry metricRegistry;

    @Before
    public void setup() throws Exception {
        this.metricRegistry = new MetricRegistry();

        this.expectedConfiguration = new PetiteConfiguration();
        this.expectedConfiguration.setAutomagicConfigurator(true);
        this.expectedConfiguration.setRegisterSelf(true);
        this.expectedConfiguration.setUseFullTypeNames(false);

        this.configuration = MAPPER.readValue(
                FixtureHelpers.fixture("config_short_name_automagic.yml"),
                PetiteConfiguration.class);
    }

    @Test
    public void testDeserialization() {
        assertThat(this.configuration).isEqualTo(this.expectedConfiguration);
    }

    @Test
    public void testBuildNamingStrategyWithShortName() {
        assertThat(this.configuration.isUseFullTypeNames()).isFalse();
    }

    @Test
    public void testBuildNamingStrategyWithFullName() throws Exception {
        assertThat(MAPPER.readValue(
                FixtureHelpers.fixture("config_full_name_automagic.yml"),
                PetiteConfiguration.class).isUseFullTypeNames()).isTrue();
    }

    @Test
    public void testBuild() {
        final PetiteContainer petite = this.configuration.build(this.metricRegistry);
        final PetiteConfig config = petite.getConfig();
        assertThat(config.getUseFullTypeNames()).isFalse();
    }
}