package com.github.mtakaki.dropwizard.petite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.dropwizard.testing.FixtureHelpers;
import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;

@ExtendWith(MockitoExtension.class)
public class PetiteManagedTest {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private PetiteManaged petiteManaged;

    @BeforeEach
    public void setUp() throws Exception {
        final PetiteConfiguration configuration = MAPPER.readValue(
                FixtureHelpers.fixture("config_short_name_automagic.yml"),
                PetiteConfiguration.class);
        this.petiteManaged = new PetiteManaged(configuration, new MetricRegistry());

        this.petiteManaged.start();
    }

    @Test
    public void testStart() throws Exception {
        final PetiteContainer petite = this.petiteManaged.getPetite();
        final PetiteConfig petiteConfig = petite.config();
        assertThat(petiteConfig.getUseFullTypeNames()).isFalse();
        assertThat((MetricRegistry) petite.getBean(MetricRegistry.class.getSimpleName()))
                .isNotNull();
    }

    @Test
    public void testStop() throws Exception {
        this.petiteManaged.stop();

        final PetiteContainer petite = this.petiteManaged.getPetite();
        final PetiteConfig petiteConfig = petite.config();
        assertThat(petiteConfig.getUseFullTypeNames()).isFalse();
        assertThat((MetricRegistry) petite.getBean(MetricRegistry.class.getSimpleName()))
                .isNull();
    }
}