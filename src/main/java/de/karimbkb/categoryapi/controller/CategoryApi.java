package de.karimbkb.categoryapi.controller;

import de.karimbkb.categoryapi.dto.Category;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.reactivex.Single;

import javax.validation.constraints.NotEmpty;

public interface CategoryApi {
    Single<MutableHttpResponse<Category>> getCategory(@NotEmpty String path);
    Single<MutableHttpResponse<Category>> saveCategory(@Body Single<Category> categoryInput);
    Single<MutableHttpResponse<Category>> updateCategory(@NotEmpty String id, @Body Single<Category> categoryInput);
    Single<MutableHttpResponse<Category>> deleteCategory(@NotEmpty String id);
}
