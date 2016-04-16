package com.github.mtakaki.dropwizard.petite;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import jodd.petite.PetiteContainer;

public class MonitoredPetiteContainer extends PetiteContainer {
    private final Timer timerGetBean;
    private final Timer timerAddBean;

    public MonitoredPetiteContainer(final MetricRegistry metricRegistry) {
        this.timerGetBean = metricRegistry
                .timer(MetricRegistry.name(MonitoredPetiteContainer.class, "getBean"));
        this.timerAddBean = metricRegistry
                .timer(MetricRegistry.name(MonitoredPetiteContainer.class, "addBean"));
    }

    @Override
    public <T> T getBean(final Class<T> type) {
        try (Timer.Context context = this.timerGetBean.time()) {
            return super.getBean(type);
        }
    }

    @Override
    public Object getBean(final String name) {
        try (Timer.Context context = this.timerGetBean.time()) {
            return super.getBean(name);
        }
    }

    @Override
    public void addBean(final String name, final Object bean) {
        try (Timer.Context context = this.timerAddBean.time()) {
            super.addBean(name, bean);
        }
    }
}