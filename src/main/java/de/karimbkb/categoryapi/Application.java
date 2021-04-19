package de.karimbkb.categoryapi;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Category Api",
            version = "1.0",
            description = "CRUD api for categories.",
            contact = @Contact(name = "Karim Baydon", email = "karimb.kb@gmail.com")))
public class Application {
  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
