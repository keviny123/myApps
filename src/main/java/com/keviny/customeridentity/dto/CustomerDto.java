package com.keviny.customeridentity.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerDto {
    private Long id;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name cannot be longer than 100 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name cannot be longer than 100 characters")
    private String lastName;

    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be 'Male', 'Female', or 'Other'", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "SSN cannot be blank")
    @Size(max = 32, message = "SSN cannot be longer than 32 characters")
    private String ssn;

    // Getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public LocalDate getDob() { return dob; }
    public String getSsn() { return ssn; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setSsn(String ssn) { this.ssn = ssn; }
}