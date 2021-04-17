package de.karimbkb.categoryapi.service;

import de.karimbkb.categoryapi.dto.Category;
import io.micronaut.context.annotation.Prototype;

@Prototype
public class CategoryService {
  public String buildPath(Category c) {
    // TODO: look into Magentos way of converting names to urls
    return c.getName().toLowerCase().trim().replaceAll(" ", "-");
  }
}
