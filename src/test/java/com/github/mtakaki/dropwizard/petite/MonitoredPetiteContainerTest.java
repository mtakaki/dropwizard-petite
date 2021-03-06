package com.github.mtakaki.dropwizard.petite;

import static org.assertj.core.api.Assertions.assertThat;
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

@RunWith(MockitoJUnitRunner.class)
public class MonitoredPetiteContainerTest {
    private MonitoredPetiteContainer petite;

    @Mock
    private MetricRegistry metricRegistry;
    @Mock
    private Timer getBeanTimer;
    @Mock
    private Timer addBeanTimer;
    @Mock
    private Timer.Context context;

    @Before
    public void setUp() {
        when(this.metricRegistry
                .timer("com.github.mtakaki.dropwizard.petite.MonitoredPetiteContainer.getBean"))
                        .thenReturn(this.getBeanTimer);
        when(this.metricRegistry
                .timer("com.github.mtakaki.dropwizard.petite.MonitoredPetiteContainer.addBean"))
                        .thenReturn(this.addBeanTimer);
        when(this.getBeanTimer.time()).thenReturn(this.context);
        when(this.addBeanTimer.time()).thenReturn(this.context);

        this.petite = new MonitoredPetiteContainer(this.metricRegistry);

        this.petite.addBean(String.class.getName(), "abc");
        this.petite.addBean(String.class.getSimpleName().toLowerCase(), "def");
    }

    @Test
    public void testGetBean() {
        assertThat((String) this.petite.getBean(String.class.getName())).isEqualTo("abc");

        verify(this.getBeanTimer, times(1)).time();
    }

    @Test
    public void testGetBeanString() {
        assertThat(this.petite.getBean(String.class)).isEqualTo("def");

        verify(this.getBeanTimer, times(2)).time();
    }

    @Test
    public void testAddBean() {
        this.petite.addBean("random", "ghi");

        verify(this.addBeanTimer, times(3)).time();
    }
}