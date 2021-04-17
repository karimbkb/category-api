package de.karimbkb.categoryapi.exception;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(String message) {
    super(message);
  }

  public static CategoryNotFoundException withPath(String path) {
    return new CategoryNotFoundException(
        String.format("Category path [%s] could not be found", path));
  }

  public static CategoryNotFoundException withId(String id) {
    return new CategoryNotFoundException(String.format("Category id [%s] could not be found", id));
  }
}
