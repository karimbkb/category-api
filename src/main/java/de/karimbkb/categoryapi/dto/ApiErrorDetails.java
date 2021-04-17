package de.karimbkb.categoryapi.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiErrorDetails {
  private int status;
  private String detail;
  private String code;
  private Set<String> links;
}
