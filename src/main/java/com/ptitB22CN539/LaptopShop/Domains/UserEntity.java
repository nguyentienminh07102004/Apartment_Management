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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @JoinColumn(name = "role_name", referencedColumnName = RoleEntity_.NAME)
    @JsonManagedReference
    private RoleEntity role;

    @ManyToMany
    @JoinTable(name = "user_permission",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @JsonManagedReference
    private List<PermissionEntity> permissions;
}
