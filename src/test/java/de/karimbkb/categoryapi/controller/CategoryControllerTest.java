package de.karimbkb.categoryapi.controller;

import de.karimbkb.categoryapi.dto.Category;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxStreamingHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryControllerTest implements TestPropertyProvider {

    @Inject
    @Client("/")
    RxStreamingHttpClient client;

    private static HttpResponse<Category> response;

    @ClassRule
    public static GenericContainer<?> mongoDBContainer =
            new GenericContainer<>("mongo:4.4.5")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init-mongo.js"),
                            "/docker-entrypoint-initdb.d/init-mongo.js")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("clean-collections.js"), "/clean-collections.js");

    static {
        mongoDBContainer.start();
    }

    @BeforeEach
    void populateCategoryData() {
        Category category = new Category("Jeans For Men", "jeans-for-men");
        response =
                client
                        .exchange(HttpRequest.POST("/v1/category/", category), Category.class)
                        .blockingFirst();
    }

    @AfterEach
    void cleanDatabase() throws IOException, InterruptedException {
        mongoDBContainer.execInContainer("/bin/sh", "-c", "mongo category < clean-collections.js");
    }

    @AfterAll
    void tearDown() {
        mongoDBContainer.close();
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        return CollectionUtils.mapOf(
                "mongodb.uri",
                "mongodb://localhost:" + mongoDBContainer.getMappedPort(27017) + "/category");
    }

    @Test
    void getCategory() {
        HttpResponse<Category> response =
                client
                        .exchange(HttpRequest.GET("/v1/category/jeans-for-men"), Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().get().getId().matches("[A-Za-z0-9\\-]+"));
        assertEquals("Jeans For Men", response.getBody().get().getName());
        assertEquals("jeans-for-men", response.getBody().get().getPath());
        assertNotNull(response.getBody().get().getCreatedAt());
        assertNotNull(response.getBody().get().getUpdatedAt());

    }

    @Test
    void getCategoryWithCategoryNotFoundException() {
        Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> {
                    HttpResponse<Category> getResponse =
                            client
                                    .exchange(HttpRequest.GET("/v1/category/pants"), Category.class)
                                    .blockingFirst();

                    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatus());
                });
    }

    @Test
    void saveCategory() {
        Category category = new Category("Hoodies For Men", "hoodies-for-men");
        HttpResponse<Category> saveResponse =
                client
                        .exchange(HttpRequest.POST("/v1/category/", category), Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.CREATED, saveResponse.getStatus());
        assertTrue(saveResponse.getBody().get().getId().matches("[A-Za-z0-9\\-]+"));
        assertEquals("Hoodies For Men", saveResponse.getBody().get().getName());
        assertEquals("hoodies-for-men", saveResponse.getBody().get().getPath());
        assertNotNull(saveResponse.getBody().get().getCreatedAt());
        assertNotNull(saveResponse.getBody().get().getUpdatedAt());
    }

    @Test
    void saveCategoryWithPathNull() {
        Category category = new Category("Hoodies For Men", "");
        HttpResponse<Category> saveResponse =
                client
                        .exchange(HttpRequest.POST("/v1/category/", category), Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.CREATED, saveResponse.getStatus());
        assertTrue(saveResponse.getBody().get().getId().matches("[A-Za-z0-9\\-]+"));
        assertEquals("Hoodies For Men", saveResponse.getBody().get().getName());
        assertEquals("hoodies-for-men", saveResponse.getBody().get().getPath());
        assertNotNull(saveResponse.getBody().get().getCreatedAt());
        assertNotNull(saveResponse.getBody().get().getUpdatedAt());
    }

    @Test
    void saveCategoryWithCategoryException() {
        Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> {
                    Category category = new Category("", "");
                    HttpResponse<Category> saveResponse =
                            client
                                    .exchange(HttpRequest.POST("/v1/category/", category), Category.class)
                                    .blockingFirst();

                    assertEquals(HttpStatus.NOT_FOUND, saveResponse.getStatus());
                });
    }

    @Test
    void updateCategory() {
        Category categoryUpdate = new Category("Jeans For Men", "jeans-for-men");
        HttpResponse<Category> updateResponse =
                client
                        .exchange(
                                HttpRequest.PUT("/v1/category/" + response.getBody().get().getId(), categoryUpdate),
                                Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.OK, updateResponse.getStatus());
        assertTrue(updateResponse.getBody().get().getId().matches("[A-Za-z0-9\\-]+"));
        assertEquals("Jeans For Men", updateResponse.getBody().get().getName());
        assertEquals("jeans-for-men", updateResponse.getBody().get().getPath());
        assertNotNull(updateResponse.getBody().get().getCreatedAt());
        assertNotNull(updateResponse.getBody().get().getUpdatedAt());
    }

    @Test
    void updateCategoryWithNullValues() {
        Category categoryUpdate = new Category("", "", "", "");
        HttpResponse<Category> updateResponse = client.exchange(
                                HttpRequest.PUT("/v1/category/" + response.getBody().get().getId(), categoryUpdate),
                                Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.OK, updateResponse.getStatus());
    }

    @Test
    void updateCategoryWithNullValuesButExistingCreatedAt() {
        Category categoryUpdate =
                new Category("", "", "2021-04-15T13:48:05.090890", "2021-04-16T00:57:38.702241");
        HttpResponse<Category> updateResponse =
                client
                        .exchange(
                                HttpRequest.PUT("/v1/category/" + response.getBody().get().getId(), categoryUpdate),
                                Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.OK, updateResponse.getStatus());
        assertEquals(response.getBody().get().getId(), updateResponse.getBody().get().getId());
    }

    @Test
    void updateCategoryWithCategoryNotFoundException() {
        Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> {
                    Category categoryUpdate = new Category("", "", "", "");
                    HttpResponse<Category> updateResponse =
                            client
                                    .exchange(
                                            HttpRequest.PUT("/v1/category/123456789", categoryUpdate), Category.class)
                                    .blockingFirst();

                    assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatus());
                });
    }

    @Test
    void deleteCategory() {
        HttpResponse<Category> deleteResponse =
                client
                        .exchange(
                                HttpRequest.DELETE("/v1/category/" + response.getBody().get().getId()),
                                Category.class)
                        .blockingFirst();

        assertEquals(HttpStatus.OK, deleteResponse.getStatus());
        assertEquals(response.getBody().get().getId(), deleteResponse.getBody().get().getId());
        assertEquals("Jeans For Men", deleteResponse.getBody().get().getName());
        assertEquals("jeans-for-men", deleteResponse.getBody().get().getPath());
        assertNotNull(deleteResponse.getBody().get().getCreatedAt());
        assertNotNull(deleteResponse.getBody().get().getUpdatedAt());
    }

    @Test
    void deleteCategoryWithCategoryNotFoundException() {
        Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> {
                    HttpResponse<Category> updateResponse =
                            client
                                    .exchange(HttpRequest.DELETE("/v1/category/1234567"), Category.class)
                                    .blockingFirst();

                    assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatus());
                });
    }
}
