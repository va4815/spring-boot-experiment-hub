package com.va4815.springresourcebackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequestDTO {

    @Email
    @Size(max = 320)
    @Pattern(regexp = ".*\\S.*", message = "Email must not be blank")
    private String email;

    @Size(max = 255)
    @Pattern(regexp = ".*\\S.*", message = "First name must not be blank")
    private String firstName;

    @Size(max = 255)
    @Pattern(regexp = ".*\\S.*", message = "Last name must not be blank")
    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
