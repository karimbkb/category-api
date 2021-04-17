package de.karimbkb.categoryapi.exception.handler;

import de.karimbkb.categoryapi.dto.ApiError;
import de.karimbkb.categoryapi.dto.ApiErrorDetails;
import de.karimbkb.categoryapi.exception.CategoryException;
import de.karimbkb.categoryapi.exception.CategoryNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {CategoryNotFoundException.class, ExceptionHandler.class})
public class CategoryExceptionHandler implements ExceptionHandler<CategoryException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, CategoryException exception) {
    return HttpResponse.notFound(
        new ApiError(
            List.of(new ApiErrorDetails(404, exception.getMessage(), "CE-002", Set.of()))));
  }
}
