package com.adeem.stockflow.service.criteria.filter;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplierCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter firstName;
    private StringFilter lastName;
    private StringFilter companyName;
    private StringFilter phone;
    private StringFilter email;
    private StringFilter fax;
    private StringFilter taxId;
    private StringFilter registrationArticle;
    private StringFilter statisticalId;
    private StringFilter rc;
    private BooleanFilter active;
    private StringFilter notes;
    private StringFilter createdBy;
    private InstantFilter createdDate;
    private StringFilter lastModifiedBy;
    private InstantFilter lastModifiedDate;
    private LongFilter clientAccountId;
    private LongFilter addressId;

    private Boolean distinct;

    public SupplierCriteria() {}

    public SupplierCriteria(SupplierCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.companyName = other.companyName == null ? null : other.companyName.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.fax = other.fax == null ? null : other.fax.copy();
        this.taxId = other.taxId == null ? null : other.taxId.copy();
        this.registrationArticle = other.registrationArticle == null ? null : other.registrationArticle.copy();
        this.statisticalId = other.statisticalId == null ? null : other.statisticalId.copy();
        this.rc = other.rc == null ? null : other.rc.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.clientAccountId = other.clientAccountId == null ? null : other.clientAccountId.copy();
        this.addressId = other.addressId == null ? null : other.addressId.copy();
        this.distinct = other.distinct;
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public StringFilter firstName() {
        if (firstName == null) {
            firstName = new StringFilter();
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public StringFilter lastName() {
        if (lastName == null) {
            lastName = new StringFilter();
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getCompanyName() {
        return companyName;
    }

    public StringFilter companyName() {
        if (companyName == null) {
            companyName = new StringFilter();
        }
        return companyName;
    }

    public void setCompanyName(StringFilter companyName) {
        this.companyName = companyName;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getFax() {
        return fax;
    }

    public StringFilter fax() {
        if (fax == null) {
            fax = new StringFilter();
        }
        return fax;
    }

    public void setFax(StringFilter fax) {
        this.fax = fax;
    }

    public StringFilter getTaxId() {
        return taxId;
    }

    public StringFilter taxId() {
        if (taxId == null) {
            taxId = new StringFilter();
        }
        return taxId;
    }

    public void setTaxId(StringFilter taxId) {
        this.taxId = taxId;
    }

    public StringFilter getRegistrationArticle() {
        return registrationArticle;
    }

    public StringFilter registrationArticle() {
        if (registrationArticle == null) {
            registrationArticle = new StringFilter();
        }
        return registrationArticle;
    }

    public void setRegistrationArticle(StringFilter registrationArticle) {
        this.registrationArticle = registrationArticle;
    }

    public StringFilter getStatisticalId() {
        return statisticalId;
    }

    public StringFilter statisticalId() {
        if (statisticalId == null) {
            statisticalId = new StringFilter();
        }
        return statisticalId;
    }

    public void setStatisticalId(StringFilter statisticalId) {
        this.statisticalId = statisticalId;
    }

    public StringFilter getRc() {
        return rc;
    }

    public StringFilter rc() {
        if (rc == null) {
            rc = new StringFilter();
        }
        return rc;
    }

    public void setRc(StringFilter rc) {
        this.rc = rc;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public StringFilter notes() {
        if (notes == null) {
            notes = new StringFilter();
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            lastModifiedBy = new StringFilter();
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getClientAccountId() {
        return clientAccountId;
    }

    public LongFilter clientAccountId() {
        if (clientAccountId == null) {
            clientAccountId = new LongFilter();
        }
        return clientAccountId;
    }

    public void setClientAccountId(LongFilter clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    public LongFilter getAddressId() {
        return addressId;
    }

    public LongFilter addressId() {
        if (addressId == null) {
            addressId = new LongFilter();
        }
        return addressId;
    }

    public void setAddressId(LongFilter addressId) {
        this.addressId = addressId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SupplierCriteria that = (SupplierCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(companyName, that.companyName) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(email, that.email) &&
            Objects.equals(fax, that.fax) &&
            Objects.equals(taxId, that.taxId) &&
            Objects.equals(registrationArticle, that.registrationArticle) &&
            Objects.equals(statisticalId, that.statisticalId) &&
            Objects.equals(rc, that.rc) &&
            Objects.equals(active, that.active) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(clientAccountId, that.clientAccountId) &&
            Objects.equals(addressId, that.addressId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            lastName,
            companyName,
            phone,
            email,
            fax,
            taxId,
            registrationArticle,
            statisticalId,
            rc,
            active,
            notes,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            clientAccountId,
            addressId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplierCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (companyName != null ? "companyName=" + companyName + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (fax != null ? "fax=" + fax + ", " : "") +
            (taxId != null ? "taxId=" + taxId + ", " : "") +
            (registrationArticle != null ? "registrationArticle=" + registrationArticle + ", " : "") +
            (statisticalId != null ? "statisticalId=" + statisticalId + ", " : "") +
            (rc != null ? "rc=" + rc + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (notes != null ? "notes=" + notes + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (clientAccountId != null ? "clientAccountId=" + clientAccountId + ", " : "") +
            (addressId != null ? "addressId=" + addressId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
