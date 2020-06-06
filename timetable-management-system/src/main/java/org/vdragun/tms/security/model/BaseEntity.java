package org.vdragun.tms.security.model;

import static javax.persistence.EnumType.STRING;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Abstract parent for all security-related entities
 * 
 * @author Vitaliy Dragun
 *
 */
@MappedSuperclass
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public BaseEntity() {
        this(Status.ACTIVE);
    }

    public BaseEntity(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
