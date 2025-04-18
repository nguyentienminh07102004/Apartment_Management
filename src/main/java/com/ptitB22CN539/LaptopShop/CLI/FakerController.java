package com.ptitB22CN539.LaptopShop.CLI;

import com.github.javafaker.Faker;
import com.ptitB22CN539.LaptopShop.Config.ApartmentStatus;
import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.ServiceFeeStatus;
import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentRegisterRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.DTO.Water.WaterFeeRequestDTO;
import com.ptitB22CN539.LaptopShop.Service.Apartment.IApartmentService;
import com.ptitB22CN539.LaptopShop.Service.Electricity.IElectricityService;
import com.ptitB22CN539.LaptopShop.Service.User.IUserService;
import com.ptitB22CN539.LaptopShop.Service.Water.IWaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/faker")
public class FakerController {
    private final IApartmentService apartmentService;
    private final IUserService userService;
    private final IWaterService waterService;
    private final IElectricityService electricityService;

    @PostMapping(value = "/apartments")
    public void ApartmentController() {
        Faker faker = new Faker();
        String[] apartmentStatus = {"ACTIVE",
                "INACTIVE",
                "RENTING",
                "NOT_RENTING",
                "MAINTENANCE"};
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                ApartmentRegisterRequestDTO apartmentRegisterRequestDTO = ApartmentRegisterRequestDTO.builder()
                        .phone(faker.phoneNumber().phoneNumber())
                        .area(faker.number().randomDouble(1, 15, 30))
                        .floor(i)
                        .phone(faker.phoneNumber().phoneNumber())
                        .name("%d0%d".formatted(i, j))
                        .status(ApartmentStatus.valueOf(apartmentStatus[faker.number().numberBetween(0, apartmentStatus.length - 1)]))
                        .build();
                apartmentService.save(apartmentRegisterRequestDTO);
            }
        }
    }

    @PostMapping(value = "/users")
    @Transactional
    public void UserController() {
        Faker faker = new Faker();
        for (int i = 0; i < 81; i++) {
            UserRegister userRegister = UserRegister.builder()
                    .permissionIds(List.of("644df756-8233-4def-8451-de91d5633b79", "723b7e56-1b28-48e7-b063-8c46997b5a39"))
                    .address(faker.address().fullAddress())
                    .phone(faker.phoneNumber().phoneNumber())
                    .email("%s%d@gmail.com".formatted(faker.name().lastName(), i))
                    .password(ConstantConfig.DEFAULT_PASSWORD)
                    .confirmPassword(ConstantConfig.DEFAULT_PASSWORD)
                    .roleId("4c5779e0-a882-48a5-83df-93069e0d38f0")
                    .fullName(faker.name().fullName())
                    .build();
            userService.register(userRegister);
        }
    }

    @PostMapping(value = "/water-fee")
    @Transactional
    public void WaterFeeController() {
        Faker faker = new Faker();
        for (int j = 1; j <= 12; j++) {
            for (int i = 0; i < 81; i++) {
                WaterFeeRequestDTO waterFeeRequestDTO = WaterFeeRequestDTO.builder()
                        .apartmentId(apartmentService.getAll(1, 81).getContent().get(i).getId())
                        .priceUnit(faker.number().randomDouble(2, 15, 30))
                        .status(faker.number().numberBetween(1, 100) % 2 == 0 ? ServiceFeeStatus.UNPAID : ServiceFeeStatus.PAID)
                        .waterIndexStart(faker.number().numberBetween(0, 50))
                        .waterIndexEnd(faker.number().numberBetween(51, 100))
                        .paymentPeriod("%02d/%d".formatted(j, 2024))
                        .dueDate(faker.date().future(faker.number().numberBetween(1, 10), TimeUnit.DAYS))
                        .build();
                waterService.save(waterFeeRequestDTO);
            }
        }
    }

    @PostMapping(value = "/electricity-fees")
    public void ElectricityController() {
        Faker faker = new Faker();
        for (int j = 1; j <= 12; j++) {
            for (int i = 0; i < 81; i++) {
                ElectricityRequestDTO electricityRequestDTO = ElectricityRequestDTO.builder()
                        .apartmentId(apartmentService.getAll(1, 81).getContent().get(i).getId())
                        .priceUnit(faker.number().randomDouble(1, 2, 4))
                        .status(faker.number().numberBetween(1, 100) % 2 == 0 ? ServiceFeeStatus.UNPAID : ServiceFeeStatus.PAID)
                        .electricityIndexStart(faker.number().numberBetween(0, 150))
                        .electricityIndexEnd(faker.number().numberBetween(151, 200))
                        .paymentPeriod("%02d/%d".formatted(j, 2024))
                        .dueDate(faker.date().future(faker.number().numberBetween(1, 10), TimeUnit.DAYS))
                        .build();
                electricityService.save(electricityRequestDTO);
            }
        }
    }
}