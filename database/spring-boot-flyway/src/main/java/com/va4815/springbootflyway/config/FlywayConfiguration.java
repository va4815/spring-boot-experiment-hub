package com.va4815.springbootflyway.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfiguration {

    @Bean(name = "authenticationFlyway", initMethod = "migrate")
    Flyway authenticationFlyway(DataSource dataSource) {
        return createFlyway(dataSource, "authentication");
    }

    @Bean(name = "productFlyway", initMethod = "migrate")
    Flyway productFlyway(DataSource dataSource) {
        return createFlyway(dataSource, "product");
    }

    private Flyway createFlyway(DataSource dataSource, String schema) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/" + schema)
                .defaultSchema(schema)
                .schemas(schema)
                .createSchemas(true)
                .load();
    }
}
