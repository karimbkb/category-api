package de.karimbkb.categoryapi.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties("category")
public class CategoryConfig {
  private String databaseName;
  private String collectionName;
}
