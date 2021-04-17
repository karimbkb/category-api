package de.karimbkb.categoryapi.exception;

import de.karimbkb.categoryapi.dto.Category;

public class CategoryException extends RuntimeException {
  public CategoryException(String message) {
    super(message);
  }

  public static CategoryException withCategory(Category category) {
    return new CategoryException(
        String.format("Category [%s] could not be saved", category.getName()));
  }

  public static CategoryException withId(String id) {
    return new CategoryException(String.format("Category with id [%s] could not be deleted", id));
  }
}
