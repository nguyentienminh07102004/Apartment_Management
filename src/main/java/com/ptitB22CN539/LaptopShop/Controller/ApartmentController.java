package com.ptitB22CN539.LaptopShop.Controller;

import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentRegisterRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentSearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Service.Apartment.IApartmentService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/apartments")
public class ApartmentController {
    private final IApartmentService apartmentService;

    @PostMapping(value = "/")
    public ResponseEntity<APIResponse> save(@RequestBody ApartmentRegisterRequestDTO apartmentRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(apartmentService.save(apartmentRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/")
    public ResponseEntity<APIResponse> getAll(@ModelAttribute ApartmentSearchRequestDTO apartmentSearchRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(apartmentService.getAll(apartmentSearchRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable String id) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(apartmentService.getById(id))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/rental/apartment/{apartmentId}/user/{userId}")
    public ResponseEntity<APIResponse> rentalApartment(@PathVariable String apartmentId, @PathVariable String userId, @RequestParam Boolean isOwner) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(apartmentService.rentalApartment(userId, apartmentId, isOwner))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
