package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for customer self-registration in the marketplace.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerRegistrationDTO implements Serializable {

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    @Email
    @Size(max = 254)
    private String email;

    @NotNull
    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Phone number must be between 8 and 15 digits")
    private String phone;

    @Size(max = 20)
    private String fax;

    @Size(max = 50)
    private String taxId;

    @Size(max = 100)
    private String registrationArticle;

    @Size(max = 50)
    private String statisticalId;

    @Size(max = 50)
    private String rc;

    @NotNull
    @Size(min = 8, max = 100)
    private String password;

    @NotNull
    @Size(min = 2, max = 10)
    private String langKey = "en";

    // Terms acceptance
    @NotNull
    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean acceptTerms;

    // Newsletter subscription (optional)
    private Boolean subscribeNewsletter = false;

    // Constructors
    public CustomerRegistrationDTO() {}

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getRegistrationArticle() {
        return registrationArticle;
    }

    public void setRegistrationArticle(String registrationArticle) {
        this.registrationArticle = registrationArticle;
    }

    public String getStatisticalId() {
        return statisticalId;
    }

    public void setStatisticalId(String statisticalId) {
        this.statisticalId = statisticalId;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Boolean getAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(Boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }

    public Boolean getSubscribeNewsletter() {
        return subscribeNewsletter;
    }

    public void setSubscribeNewsletter(Boolean subscribeNewsletter) {
        this.subscribeNewsletter = subscribeNewsletter;
    }

    // Computed properties
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerRegistrationDTO)) {
            return false;
        }

        CustomerRegistrationDTO that = (CustomerRegistrationDTO) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerRegistrationDTO{" +
            "firstName='" + firstName + "'" +
            ", lastName='" + lastName + "'" +
            ", email='" + email + "'" +
            ", phone='" + phone + "'" +
            ", fax='" + fax + "'" +
            ", taxId='" + taxId + "'" +
            ", registrationArticle='" + registrationArticle + "'" +
            ", statisticalId='" + statisticalId + "'" +
            ", rc='" + rc + "'" +
            ", langKey='" + langKey + "'" +
            ", acceptTerms=" + acceptTerms +
            ", subscribeNewsletter=" + subscribeNewsletter +
            "}";
    }
}
