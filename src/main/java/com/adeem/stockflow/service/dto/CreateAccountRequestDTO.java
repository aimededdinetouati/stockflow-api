package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for creating marketplace accounts for existing customers.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CreateAccountRequestDTO implements Serializable {

    @NotNull
    @Email
    @Size(max = 254)
    private String email;

    @Size(min = 2, max = 10)
    private String langKey = "en";

    // Optional: Send welcome email
    private Boolean sendWelcomeEmail = true;

    // Optional: Customer can change password on first login
    private Boolean requirePasswordChange = true;

    // Constructors
    public CreateAccountRequestDTO() {}

    public CreateAccountRequestDTO(String email) {
        this.email = email;
    }

    public CreateAccountRequestDTO(String email, String langKey) {
        this.email = email;
        this.langKey = langKey;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Boolean getSendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    public void setSendWelcomeEmail(Boolean sendWelcomeEmail) {
        this.sendWelcomeEmail = sendWelcomeEmail;
    }

    public Boolean getRequirePasswordChange() {
        return requirePasswordChange;
    }

    public void setRequirePasswordChange(Boolean requirePasswordChange) {
        this.requirePasswordChange = requirePasswordChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateAccountRequestDTO)) {
            return false;
        }

        CreateAccountRequestDTO that = (CreateAccountRequestDTO) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CreateAccountRequestDTO{" +
            "email='" + email + "'" +
            ", langKey='" + langKey + "'" +
            ", sendWelcomeEmail=" + sendWelcomeEmail +
            ", requirePasswordChange=" + requirePasswordChange +
            "}";
    }
}
