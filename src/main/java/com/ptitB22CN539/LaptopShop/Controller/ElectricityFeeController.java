package com.ptitB22CN539.LaptopShop.Controller;

import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.Service.Electricity.IElectricityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/electrics")
public class ElectricityFeeController {
    private final IElectricityService electricityService;

    @GetMapping(value = "/apartment/{apartmentId}")
    public ResponseEntity<APIResponse> getElectricityFeeByApartment(@PathVariable String apartmentId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(electricityService.getElectricityFeeByApartmentId(apartmentId, page, limit))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/")
    public ResponseEntity<APIResponse> saveElectricityFee() {
        APIResponse response = APIResponse.builder()
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
