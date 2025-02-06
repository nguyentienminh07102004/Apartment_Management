package com.ptitB22CN539.LaptopShop.Domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptitB22CN539.LaptopShop.Config.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "address")
    private String address;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "password")
    @JsonIgnore
    private String password;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private UserStatus status;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonManagedReference
    private RoleEntity role;

    @ManyToMany
    @JoinTable(name = "user_permission",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<PermissionEntity> permissions;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JsonManagedReference
    private List<JwtEntity> jwts;
}
