package de.karimbkb.categoryapi.service;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import de.karimbkb.categoryapi.configuration.CategoryConfig;
import de.karimbkb.categoryapi.dto.Category;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class CategoryService {

  private final MongoClient mongoClient;
  private final CategoryConfig categoryConfig;

  public Category loadCategoryByPath(String path) {
    return getCollection().find(eq("path", path)).limit(1).first();
  }

  public Category loadCategoryById(String id) {
    return getCollection().find(eq("_id", id)).limit(1).first();
  }

  public MongoCollection<Category> getCollection() {
    return mongoClient
        .getDatabase(categoryConfig.getDatabaseName())
        .getCollection(categoryConfig.getCollectionName(), Category.class);
  }

  public String buildPath(Category c) {
    return c.getName().toLowerCase().trim().replace(" ", "-");
  }
}
