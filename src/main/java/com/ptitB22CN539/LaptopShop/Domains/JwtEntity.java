package com.ptitB22CN539.LaptopShop.Domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "token", columnDefinition = "TEXT")
    private String token;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "refresh_token_expiated")
    private Date refreshTokenExpiatedDate;
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = UserEntity_.EMAIL)
    @JsonIgnore
    private UserEntity user;
}
