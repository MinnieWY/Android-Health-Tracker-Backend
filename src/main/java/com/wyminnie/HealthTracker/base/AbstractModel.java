package com.wyminnie.healthtracker.base;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.wyminnie.healthtracker.common.Constants;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractModel {
    public abstract Serializable getId();

    @CreatedBy
    @Column(updatable = false)
    protected String createdBy = Constants.SYSTEM;

    @CreatedDate
    @Column(updatable = false)
    protected Instant dateCreated;

    @LastModifiedBy
    protected String modifiedBy = Constants.SYSTEM;

    @LastModifiedDate
    protected Instant dateModified;

    protected String dataStatus = "A"; // = A, I

    public boolean isDataStatusActive() {
        return Objects.equals(Constants.STATUS_ACTIVE, this.dataStatus);
    }

    public AbstractModel() {
        super();
    }
}
