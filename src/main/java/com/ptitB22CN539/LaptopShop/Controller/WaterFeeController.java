package com.ptitB22CN539.LaptopShop.Controller;

import com.ptitB22CN539.LaptopShop.Config.FileExcelType;
import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.DTO.Water.WaterFeeRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Water.WaterSearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.WaterFeeEntity;
import com.ptitB22CN539.LaptopShop.Service.Water.IWaterService;
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
@RequestMapping(value = "/${api.prefix}/waters")
public class WaterFeeController {
    private final IWaterService waterService;

    @GetMapping(value = "/all")
    public ResponseEntity<APIResponse> getWaterFee(@RequestParam Integer page, @RequestParam Integer limit) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(waterService.findAllWaterFee(page, limit))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/apartments/{apartmentId}")
    public ResponseEntity<APIResponse> getWaterFeeByApartment(@PathVariable String apartmentId,
                                                              @RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer limit) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(waterService.findAllByApartment_Id(apartmentId, page, limit))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/")
    public ResponseEntity<APIResponse> save(@RequestBody WaterFeeRequestDTO waterFeeRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(waterService.save(waterFeeRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/")
    public ResponseEntity<APIResponse> findAll(@ModelAttribute WaterSearchRequestDTO waterSearchRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(waterService.findAllWaterFee(waterSearchRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable String id) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(waterService.findWaterFeeById(id))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/chart")
    public ResponseEntity<APIResponse> getDataWaterChart(@RequestParam String paymentPeriod) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(waterService.getWaterFeeChartByApartmentId(paymentPeriod))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/read-from-excel")
    public ResponseEntity<APIResponse> saveFromExcel(@Valid @RequestPart @CheckFileExcelName(type = FileExcelType.WATER) MultipartFile file) {
        List<WaterFeeEntity> list = waterService.saveFromExcel(file);
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(list)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
