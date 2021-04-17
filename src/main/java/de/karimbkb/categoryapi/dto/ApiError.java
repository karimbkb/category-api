package de.karimbkb.categoryapi.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiError {
  private List<ApiErrorDetails> errors = new ArrayList<>();
}
