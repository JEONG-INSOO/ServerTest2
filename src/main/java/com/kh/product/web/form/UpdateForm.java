package com.kh.product.web.form;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateForm {

  private Long productId;

  @NotBlank
  @Size(min=1,max=10)
  private String pname;

  @NotNull
  @Positive
  private Long quantity;

  @NotNull
  @Positive
  @Min(100)
  @Max(1000000)
  private Long price;

}