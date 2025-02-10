package com.ptitB22CN539.LaptopShop.Domains;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "jwts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;
    @Column(name = "expired_date")
    private Date expiredDate;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = UserEntity_.EMAIL, nullable = false)
    @JsonBackReference
    private UserEntity user;
}
