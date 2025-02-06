package com.ptitB22CN539.LaptopShop.Domains;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jwts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "token", nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = UserEntity_.EMAIL, nullable = false)
    @JsonBackReference
    private UserEntity user;
}
