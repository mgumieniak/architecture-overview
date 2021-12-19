package com.mgumieniak.architecture.models.products;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProductDTO {
    @NonNull
    Product product;

    @NonNull
    Integer amount;
}
