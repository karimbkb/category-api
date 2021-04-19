package de.karimbkb.categoryapi.dto;

import io.micronaut.core.annotation.Introspected;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Introspected
@AllArgsConstructor
@RequiredArgsConstructor
public class Category {
  private String id;
  @NotEmpty @NotBlank @NotNull private String name;
  @NotEmpty @NotBlank @NotNull private String path;
  private String createdAt;
  private String updatedAt;

  public Category(
      @NotEmpty @NotBlank @NotNull String name, @NotEmpty @NotBlank @NotNull String path) {
    this.name = name;
    this.path = path;
  }

  public Category(
      @NotEmpty @NotBlank @NotNull String name,
      @NotEmpty @NotBlank @NotNull String path,
      String createdAt,
      String updatedAt) {
    this.name = name;
    this.path = path;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
