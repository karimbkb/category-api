package de.karimbkb.categoryapi.service;

import de.karimbkb.categoryapi.dto.Category;
import io.micronaut.context.annotation.Prototype;

@Prototype
public class CategoryService {
  public String buildPath(Category c) {
    return c.getName().toLowerCase().trim().replaceAll(" ", "-");
  }
}
