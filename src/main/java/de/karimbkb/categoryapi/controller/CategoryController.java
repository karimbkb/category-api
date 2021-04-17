package de.karimbkb.categoryapi.controller;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import de.karimbkb.categoryapi.configuration.CategoryConfig;
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
  private final MongoClient mongoClient;
  private final CategoryConfig categoryConfig;

  public CategoryController(
      @Named(TaskExecutors.IO) ExecutorService executorService,
      CategoryService categoryService,
      MongoClient mongoClient,
      CategoryConfig categoryConfig) {
    this.scheduler = Schedulers.from(executorService);
    this.categoryService = categoryService;
    this.mongoClient = mongoClient;
    this.categoryConfig = categoryConfig;
  }

  @Get("/category/{path}")
  public Single<MutableHttpResponse<Category>> getCategory(@NotEmpty String path) {
    return Single.fromCallable(
            () ->
                Optional.ofNullable(loadCategoryByPath(path))
                    .map(HttpResponse::ok)
                    .orElseThrow(() -> CategoryNotFoundException.withPath(path)))
        .subscribeOn(scheduler);
  }

  @Post("/category")
  public Single<MutableHttpResponse<Category>> saveCategory(@Body Single<Category> categoryInput) {
    return categoryInput.map(
        c -> {
          try {
            Category category =
                new Category(
                    UUID.randomUUID().toString(),
                    c.getName(),
                    c.getPath() == null ? categoryService.buildPath(c) : c.getPath(),
                    LocalDateTime.now().toString(),
                    LocalDateTime.now().toString());

            getCollection().insertOne(category);
            log.info("Category {} was saved successfully.", category);
            return HttpResponse.created(category);
          } catch (Exception e) {
            log.error("Category {} could not be saved with exception {}", c.getName(), e);
            throw CategoryException.withCategory(c);
          }
        });
  }

  @Put("/category/{id}")
  public Single<MutableHttpResponse<Category>> updateCategory(
      @NotEmpty String id, @Body Single<Category> categoryInput) {
    return categoryInput.map(
        c -> {
          try {
            return Optional.ofNullable(loadCategoryById(id))
                .map(
                    loadedCategory -> {
                      Category category =
                          new Category(
                              loadedCategory.getId(),
                              c.getName() == null ? loadedCategory.getName() : c.getName(),
                              c.getPath() == null ? loadedCategory.getPath() : c.getPath(),
                              c.getCreatedAt() == null
                                  ? loadedCategory.getCreatedAt()
                                  : c.getCreatedAt(),
                              c.getUpdatedAt() == null
                                  ? loadedCategory.getUpdatedAt()
                                  : c.getUpdatedAt());
                      getCollection()
                          .updateOne(
                              new Document("_id", category.getId()),
                              new Document("$set", category));

                      log.info("Category {} was updated successfully.", category);
                      return HttpResponse.ok(category);
                    })
                .orElseThrow(() -> CategoryNotFoundException.withPath(c.getPath()));
          } catch (Exception e) {
            log.error("Category {} could not be updated with exception {}", c.getName(), e);
            throw CategoryException.withCategory(c);
          }
        });
  }

  @Delete("/category/{id}")
  public Single<MutableHttpResponse<Category>> deleteCategory(@NotEmpty String id) {
    return Single.fromCallable(
            () -> {
              try {
                return Optional.ofNullable(loadCategoryById(id))
                    .map(
                        loadedCategory -> {
                          getCollection().deleteOne(new Document("_id", id));

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

  private MongoCollection<Category> getCollection() {
    return mongoClient
        .getDatabase(categoryConfig.getDatabaseName())
        .getCollection(categoryConfig.getCollectionName(), Category.class);
  }

  private Category loadCategoryByPath(String path) {
    return getCollection().find(eq("path", path)).limit(1).first();
  }

  private Category loadCategoryById(String id) {
    return getCollection().find(eq("_id", id)).limit(1).first();
  }
}
