package com.ptitB22CN539.LaptopShop.Domains;

import com.ptitB22CN539.LaptopShop.Config.ServiceFeeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "water_fees")
@Getter
@Setter
public class WaterFeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "from_date")
    private Date fromDate;
    @Column(name = "to_date")
    private Date toDate;
    @Column(name = "water_index_start")
    private Integer waterIndexStart;
    @Column(name = "water_index_end")
    private Integer waterIndexEnd;
    @Column(name = "due_Date")
    private Date dueDate;
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private ApartmentEntity apartment;
    @Column(name = "price_unit")
    private Double priceUnit;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ServiceFeeStatus status;
}
