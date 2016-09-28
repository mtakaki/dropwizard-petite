# Status
![Build Status](https://codeship.com/projects/73168060-e591-0133-7607-2e05c0d114fb/status?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mtakaki/dropwizard-petite/badge.svg?branch=master)](https://coveralls.io/github/mtakaki/dropwizard-petite?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/2133b69e48c248b68bee3556ec815f9a)](https://www.codacy.com/app/mitsuotakaki/dropwizard-petite)
[![Download](https://maven-badges.herokuapp.com/maven-central/com.github.mtakaki/dropwizard-petite/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mtakaki/dropwizard-petite)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.github.mtakaki/dropwizard-petite/badge.svg)](http://www.javadoc.io/doc/com.github.mtakaki/dropwizard-petite)
[![Dependency Status](https://www.versioneye.com/user/projects/57e785db79806f0039832df7/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57e785db79806f0039832df7)

# dropwizard-petite
This library provides an integration for the awesome [Jodd Petite](http://jodd.org/doc/petite/index.html) and dropwizard. It provides a light weight dependency injection, similarly to Spring but with a much smaller footprint and better error messages.

It also comes with an optional implementation (`MonitoredPetiteContainer`) that adds metrics to `PetiteContainer`, so you can monitor the cost of running Petite in your application. By default it's disabled and works exactly as if you implemented it yourself.

Jodd Petite by default will make your beans singleton, which is perfect for most of DAOs and resource classes.

Supported versions:

| Dropwizard  |  Jodd Petite |
|---|---|
| 0.9.X  | 0.0.1  |
| 1.X.X  | 1.0.0  |

## Maven
The library is available at the maven central, so just add dependency to `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>com.github.mtakaki</groupId>
    <artifactId>dropwizard-petite</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

## How to use it
### Bundle

```java
public class TestApplication extends Application<TestConfiguration> {
    // Adding hibernate just to show how to register the SessionFactory.
    private final HibernateBundle<TestConfiguration> hibernate = new HibernateBundle<TestConfiguration>(
            TestEntity.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(
                final TestConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

    // This is the Jodd Petite bundle.
    private final PetiteBundle<TestConfiguration> petite = new PetiteBundle<TestConfiguration>() {
        @Override
        protected PetiteConfiguration getConfiguration(final TestConfiguration configuration) {
            return configuration.getPetite();
        }
    };

    @Override
    public void initialize(final Bootstrap<TestConfiguration> bootstrap) {
        bootstrap.addBundle(this.hibernate);
        bootstrap.addBundle(this.petite);
    }

    @Override
    public void run(final TestConfiguration configuration, final Environment environment)
            throws Exception {
        // Registering the SessionFactory to the container because SessionFactory is not annotated
        // with @PetiteBean.
        this.petite.getPetiteContainer().addBean(SessionFactory.class.getName(),
                this.hibernate.getSessionFactory());

        // Here we're asking the Petite container to get/build the resource.
        environment.jersey().register(this.petite.getPetiteContainer().getBean(TestResource.class));
    }
}
```

### Beans
To expose your class as a bean you will need to annotate it with `@PetiteBean` and the constructor with `@PetiteInject`, although the latter is optional as Petite can figure it out automatically. Commonly you would annotate your DAO and Resource classes, like this:

```java
@PetiteBean
public class TestDAO extends AbstractDAO<Test> {
    @PetiteInject
    public TestDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
```

The DAO class depends on having the `SessionFactory` registered in the container, otherwise it won't find it when calling the constructor.

The Resource class:

```java
@PetiteBean
public class TestResource {
    private final TestEntityDAO dao;
    
    @PetiteInject
    public TestResource(TestEntityDAO dao) {
        this.dao = dao;
    }
}
```

## Configuration

The bundle comes with `PetiteConfiguration`, so your configuration will look like this:

```java
public class TestConfiguration extends Configuration {
    private final PetiteConfiguration petite = new PetiteConfiguration();
    
    public PetiteConfiguration getPetite() {
        return this.petite;
    }
}
```

And the YAML will look like this:

```yml
petite:
  useFullTypeNames: true
  automagicConfigurator: true
  registerSelf: true
  useMetrics: true
```

- `useFullTypeNames`, will make the container use the full class name, rather than just the `getSimpleName()`. This is very useful to avoid class names collision.
- `automagicConfigurator`, will make the container automatically look for beans in the class path. If this is not configured, you will need to manually register your beans.
- `registerSelf`, will register the container into itself.
- `useMetrics`, will use the `MonitoredPetiteContainer` and will push metrics for the methods `getBean()` and `addBean()`.


## Metrics

These metrics will show up if you setup the `useMetrics` settings to true (it's default to `false`). This will show up exactly the overhead of using Jodd Petite.

```json
{
"timers" : {
    "com.github.mtakaki.dropwizard.petite.MonitoredPetiteContainer.addBean" : {
      "count" : 2,
      "max" : 0.006725265,
      "mean" : 0.003824707,
      "min" : 9.241490000000001E-4,
      "p50" : 0.006725265,
      "p75" : 0.006725265,
      "p95" : 0.006725265,
      "p98" : 0.006725265,
      "p99" : 0.006725265,
      "p999" : 0.006725265,
      "stddev" : 0.002900558,
      "m15_rate" : 0.4,
      "m1_rate" : 0.4,
      "m5_rate" : 0.4,
      "mean_rate" : 0.2675745788220869,
      "duration_units" : "seconds",
      "rate_units" : "calls/second"
    },
    "com.github.mtakaki.dropwizard.petite.MonitoredPetiteContainer.getBean" : {
      "count" : 2,
      "max" : 0.00296589,
      "mean" : 0.0029420970000000003,
      "min" : 0.002918304,
      "p50" : 0.00296589,
      "p75" : 0.00296589,
      "p95" : 0.00296589,
      "p98" : 0.00296589,
      "p99" : 0.00296589,
      "p999" : 0.00296589,
      "stddev" : 2.3793E-5,
      "m15_rate" : 0.4,
      "m1_rate" : 0.4,
      "m5_rate" : 0.4,
      "mean_rate" : 0.26755860731761477,
      "duration_units" : "seconds",
      "rate_units" : "calls/second"
    },
    "com.github.mtakaki.dropwizard.petite.PetiteConfiguration.automagicConfigurator" : {
      "count" : 1,
      "max" : 1.683990942,
      "mean" : 1.683990942,
      "min" : 1.683990942,
      "p50" : 1.683990942,
      "p75" : 1.683990942,
      "p95" : 1.683990942,
      "p98" : 1.683990942,
      "p99" : 1.683990942,
      "p999" : 1.683990942,
      "stddev" : 0.0,
      "m15_rate" : 0.2,
      "m1_rate" : 0.2,
      "m5_rate" : 0.2,
      "mean_rate" : 0.13377928037494183,
      "duration_units" : "seconds",
      "rate_units" : "calls/second"
    }
}
```

## Debugging errors

### Null pointer exception when registering resource
If you're getting an NPE when registering your resource, like this:

```
Exception in thread "main" java.lang.NullPointerException
	at com.google.common.base.Preconditions.checkNotNull(Preconditions.java:210)
	at io.dropwizard.jersey.setup.JerseyEnvironment.register(JerseyEnvironment.java:37)
	at com.github.mtakaki.test.TestApplication.run(TestApplication.java:90)
	at com.github.mtakaki.test.TestApplication.run(TestApplication.java:1)
	at io.dropwizard.cli.EnvironmentCommand.run(EnvironmentCommand.java:40)
	at io.dropwizard.cli.ConfiguredCommand.run(ConfiguredCommand.java:77)
	at io.dropwizard.cli.Cli.run(Cli.java:70)
	at io.dropwizard.Application.run(Application.java:80)
	at com.github.mtakaki.test.TestApplication.main(TestApplication.java:34)
```

It means the container could **not** find your bean. It means that your bean was not registered.

**Solutions**:

- Set `automagicConfigurator: true` in your configuration file.
- Manually register the bean into your `PetiteContainer`.

### Wiring exception
If your application fails to initialize due to a `PetiteException` and it throws this stack trace:

```
Exception in thread "main" jodd.petite.PetiteException: Wiring constructor failed. References 'null,sessionFactory,org.hibernate.SessionFactory' not found for constructor: public com.github.mtakaki.test.database.TestDAO(org.hibernate.SessionFactory)
	at jodd.petite.PetiteContainer.newBeanInstance(PetiteContainer.java:90)
	at jodd.petite.PetiteContainer.getBean(PetiteContainer.java:368)
	at jodd.petite.PetiteContainer.getBean(PetiteContainer.java:325)
	at jodd.petite.PetiteContainer.newBeanInstance(PetiteContainer.java:87)
	at jodd.petite.PetiteContainer.getBean(PetiteContainer.java:368)
	at jodd.petite.PetiteContainer.getBean(PetiteContainer.java:314)
	at com.github.mtakaki.dropwizard.petite.MonitoredPetiteContainer.getBean(MonitoredPetiteContainer.java:29)
	at jodd.petite.PetiteContainer.getBean(PetiteContainer.java:302)
	at com.github.mtakaki.dropwizard.petite.MonitoredPetiteContainer.getBean(MonitoredPetiteContainer.java:22)
	at com.github.mtakaki.test.TestApplication.run(TestApplication.java:88)
	at com.github.mtakaki.test.TestApplication.run(TestApplication.java:1)
	at io.dropwizard.cli.EnvironmentCommand.run(EnvironmentCommand.java:40)
	at io.dropwizard.cli.ConfiguredCommand.run(ConfiguredCommand.java:77)
	at io.dropwizard.cli.Cli.run(Cli.java:70)
	at io.dropwizard.Application.run(Application.java:80)
	at com.github.mtakaki.test.TestApplication.main(TestApplication.java:32)
```

It means the `SessionFactory` has not been added to the container. As the stack trace shows, it tried calling a constructor that receives a `SessionFactory` but it could not find the bean to call it.

**Solution**:

- Register the `SessionFactory`. `SessionFactory` is not part of your project, so you cannot annotate it with `@PetiteBean`. The solution is to manually add the bean to the container so it can be found when auto-wiring your beans:

```java
@Override
public void run(final TestConfiguration configuration,
        final Environment environment) throws Exception {
    final PetiteContainer petiteContainer = this.petite.getPetiteContainer();
    petiteContainer.addBean(SessionFactory.class.getName(), this.hibernate.getSessionFactory());
}
```
