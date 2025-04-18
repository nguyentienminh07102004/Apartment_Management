package com.ptitB22CN539.LaptopShop.Controller;

import com.ptitB22CN539.LaptopShop.Config.FileExcelType;
import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricitySearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import com.ptitB22CN539.LaptopShop.Service.Electricity.IElectricityService;
import com.ptitB22CN539.LaptopShop.Validation.CheckFileExcelName.CheckFileExcelName;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/electrics")
public class ElectricityFeeController {
    private final IElectricityService electricityService;

    @GetMapping(value = "/apartment/{apartmentId}")
    public ResponseEntity<APIResponse> getElectricityFeeByApartment(@PathVariable String apartmentId,
                                                                    @RequestParam(required = false) Integer page,
                                                                    @RequestParam(required = false) Integer limit) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(electricityService.findAllByApartment_Id(apartmentId, page, limit))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/")
    public ResponseEntity<APIResponse> saveElectricityFee(@RequestBody ElectricityRequestDTO electricityRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(electricityService.save(electricityRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<APIResponse> getAllElectricityFees(@RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer limit) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(electricityService.findAllElectricityFee(page, limit))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable String id) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(electricityService.findElectricityFeeById(id))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/")
    public ResponseEntity<APIResponse> findAllElectricityFees(@ModelAttribute ElectricitySearchRequestDTO electricitySearchRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(electricityService.findAllElectricityFee(electricitySearchRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/chart")
    public ResponseEntity<APIResponse> getElectricityChart(@RequestParam String paymentPeriod) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(electricityService.getElectricityFeeChartByApartmentId(paymentPeriod))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/read-from-excel")
    public ResponseEntity<APIResponse> saveFromExcel(@Valid @RequestPart @CheckFileExcelName(type = FileExcelType.ELECTRIC) MultipartFile file) {
        List<ElectricityFeeEntity> list = electricityService.saveFromFileExcel(file);
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(list)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
