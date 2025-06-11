package com.adeem.stockflow.service.criteria.filter;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter firstName;
    private StringFilter lastName;
    private StringFilter phone;
    private StringFilter fax;
    private StringFilter taxId;
    private StringFilter registrationArticle;
    private StringFilter statisticalId;
    private StringFilter rc;
    private BooleanFilter enabled;
    private LongFilter createdByClientAccountId;
    private LongFilter userId;
    private StringFilter createdBy;
    private InstantFilter createdDate;
    private StringFilter lastModifiedBy;
    private InstantFilter lastModifiedDate;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.fax = other.fax == null ? null : other.fax.copy();
        this.taxId = other.taxId == null ? null : other.taxId.copy();
        this.registrationArticle = other.registrationArticle == null ? null : other.registrationArticle.copy();
        this.statisticalId = other.statisticalId == null ? null : other.statisticalId.copy();
        this.rc = other.rc == null ? null : other.rc.copy();
        this.enabled = other.enabled == null ? null : other.enabled.copy();
        this.createdByClientAccountId = other.createdByClientAccountId == null ? null : other.createdByClientAccountId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
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

    public BooleanFilter getEnabled() {
        return enabled;
    }

    public BooleanFilter enabled() {
        if (enabled == null) {
            enabled = new BooleanFilter();
        }
        return enabled;
    }

    public void setEnabled(BooleanFilter enabled) {
        this.enabled = enabled;
    }

    public LongFilter getCreatedByClientAccountId() {
        return createdByClientAccountId;
    }

    public LongFilter createdByClientAccountId() {
        if (createdByClientAccountId == null) {
            createdByClientAccountId = new LongFilter();
        }
        return createdByClientAccountId;
    }

    public void setCreatedByClientAccountId(LongFilter createdByClientAccountId) {
        this.createdByClientAccountId = createdByClientAccountId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Check if criteria is empty.
     */
    public boolean isEmpty() {
        return (
            id == null &&
            firstName == null &&
            lastName == null &&
            phone == null &&
            fax == null &&
            taxId == null &&
            registrationArticle == null &&
            statisticalId == null &&
            rc == null &&
            enabled == null &&
            createdByClientAccountId == null &&
            userId == null &&
            createdBy == null &&
            createdDate == null &&
            lastModifiedBy == null &&
            lastModifiedDate == null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(fax, that.fax) &&
            Objects.equals(taxId, that.taxId) &&
            Objects.equals(registrationArticle, that.registrationArticle) &&
            Objects.equals(statisticalId, that.statisticalId) &&
            Objects.equals(rc, that.rc) &&
            Objects.equals(enabled, that.enabled) &&
            Objects.equals(createdByClientAccountId, that.createdByClientAccountId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            lastName,
            phone,
            fax,
            taxId,
            registrationArticle,
            statisticalId,
            rc,
            enabled,
            createdByClientAccountId,
            userId,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (fax != null ? "fax=" + fax + ", " : "") +
            (taxId != null ? "taxId=" + taxId + ", " : "") +
            (registrationArticle != null ? "registrationArticle=" + registrationArticle + ", " : "") +
            (statisticalId != null ? "statisticalId=" + statisticalId + ", " : "") +
            (rc != null ? "rc=" + rc + ", " : "") +
            (enabled != null ? "enabled=" + enabled + ", " : "") +
            (createdByClientAccountId != null ? "createdByClientAccountId=" + createdByClientAccountId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
