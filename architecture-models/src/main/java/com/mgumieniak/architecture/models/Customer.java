package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@Builder
public class Customer {
    long id;

    @NotNull
    String firstName;

    @NotNull
    String lastName;

    @NotNull
    String email;

    @NotNull
    String address;

    @NotNull
    String level = "bronze";
}
