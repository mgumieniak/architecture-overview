package com.mgumieniak.architecture.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Customer {
    long id;
    String firstName;
    String lastName;
    String email;
    String address;
    String level = "bronze";
}
