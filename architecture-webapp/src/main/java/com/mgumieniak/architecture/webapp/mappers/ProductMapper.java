package com.mgumieniak.architecture.webapp.mappers;

import com.mgumieniak.architecture.models.products.Product;
import com.mgumieniak.architecture.models.products.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(uses = MapperConfigurations.class)
public interface ProductMapper {

}
