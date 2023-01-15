package de.karimbkb.categoryapi.controller;

import de.karimbkb.categoryapi.dto.Category;
import de.karimbkb.categoryapi.exception.CategoryException;
import de.karimbkb.categoryapi.exception.CategoryNotFoundException;
import de.karimbkb.categoryapi.service.CategoryService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.validation.Validated;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

@Slf4j
@Validated
@Controller("/v1")
public class CategoryController implements CategoryApi {

  private final Scheduler scheduler;
  private final CategoryService categoryService;

  public CategoryController(
      @Named(TaskExecutors.IO) ExecutorService executorService, CategoryService categoryService) {
    this.scheduler = Schedulers.from(executorService);
    this.categoryService = categoryService;
  }

  @Get("/category/{path}")
  public Single<MutableHttpResponse<Category>> getCategory(@NotEmpty String path) {
    return Single.fromCallable(
            () ->
                Optional.ofNullable(categoryService.loadCategoryByPath(path))
                    .map(HttpResponse::ok)
                    .orElseThrow(() -> CategoryNotFoundException.withPath(path)))
        .subscribeOn(scheduler);
  }

  @Post("/category")
  public Single<MutableHttpResponse<Category>> saveCategory(@Body Single<Category> categoryInput) {
    return categoryInput.map(
        categoryRequest -> {
          try {
            Category category =
                new Category(
                    UUID.randomUUID().toString(),
                    categoryRequest.getName(),
                    categoryRequest.getPath() == null
                        ? categoryService.buildPath(categoryRequest)
                        : categoryRequest.getPath(),
                    LocalDateTime.now().toString(),
                    LocalDateTime.now().toString());

            categoryService.getCollection().insertOne(category);
            log.info("Category {} was saved successfully.", category);
            return HttpResponse.created(category);
          } catch (Exception e) {
            log.error(
                "Category {} could not be saved with exception {}", categoryRequest.getName(), e);
            throw CategoryException.withCategory(categoryRequest);
          }
        });
  }

  @Put("/category/{id}")
  public Single<MutableHttpResponse<Category>> updateCategory(
      @NotEmpty String id, @Body Single<Category> categoryInput) {
    return categoryInput.map(
        categoryRequest -> {
          try {
            return Optional.ofNullable(categoryService.loadCategoryById(id))
                .map(
                    loadedCategory -> {
                      Category category =
                          new Category(
                              loadedCategory.getId(),
                              categoryRequest.getName() == null
                                  ? loadedCategory.getName()
                                  : categoryRequest.getName(),
                              categoryRequest.getPath() == null
                                  ? loadedCategory.getPath()
                                  : categoryRequest.getPath(),
                              categoryRequest.getCreatedAt() == null
                                  ? loadedCategory.getCreatedAt()
                                  : categoryRequest.getCreatedAt(),
                              categoryRequest.getUpdatedAt() == null
                                  ? loadedCategory.getUpdatedAt()
                                  : categoryRequest.getUpdatedAt());
                      categoryService
                          .getCollection()
                          .updateOne(
                              new Document("_id", category.getId()),
                              new Document("$set", category));

                      log.info("Category {} was updated successfully.", category);
                      return HttpResponse.ok(category);
                    })
                .orElseThrow(() -> CategoryNotFoundException.withPath(categoryRequest.getPath()));
          } catch (Exception e) {
            log.error(
                "Category {} could not be updated with exception {}", categoryRequest.getName(), e);
            throw CategoryException.withCategory(categoryRequest);
          }
        });
  }

  @Delete("/category/{id}")
  public Single<MutableHttpResponse<Category>> deleteCategory(@NotEmpty String id) {
    return Single.fromCallable(
            () -> {
              try {
                return Optional.ofNullable(categoryService.loadCategoryById(id))
                    .map(
                        loadedCategory -> {
                          categoryService.getCollection().deleteOne(new Document("_id", id));

                          log.info("Category {} was deleted successfully.", loadedCategory);
                          return HttpResponse.ok(loadedCategory);
                        })
                    .orElseThrow(() -> CategoryNotFoundException.withId(id));
              } catch (Exception e) {
                log.error("Category with id {} could not be updated with exception {}", id, e);
                throw CategoryException.withId(id);
              }
            })
        .subscribeOn(scheduler);
  }
}
