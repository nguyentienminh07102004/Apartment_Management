package com.ptitB22CN539.LaptopShop.Domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptitB22CN539.LaptopShop.Config.ApartmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Table(name = "apartments")
@Getter
@Setter
public class ApartmentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "phone")
    private String phone;
    @Column(name = "area")
    private Double area;
    @Column(name = "floor")
    private Integer floor;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ApartmentStatus status;

    @OneToMany(mappedBy = "apartment")
    @Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<ApartmentUserEntity> apartmentUsers;

    @OneToMany(mappedBy = "apartment")
    @JsonIgnore
    private List<ElectricityFeeEntity> electricityFee;

    @OneToMany(mappedBy = "apartment")
    @JsonIgnore
    private List<WaterFeeEntity> waterFee;
}