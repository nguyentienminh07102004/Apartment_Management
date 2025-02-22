package com.ptitB22CN539.LaptopShop.Domains;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(value = AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @LastModifiedBy
    @Column(insertable = false)
    private String modifiedBy;
}
