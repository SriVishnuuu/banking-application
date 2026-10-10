package com.vishnu.banking_application.beneficiary;


import com.vishnu.banking_application.beneficiary.dto.BeneficiaryCreateRequestDto;
import com.vishnu.banking_application.beneficiary.dto.BeneficiaryResponseDto;
import com.vishnu.banking_application.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/me/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService){
        this.beneficiaryService = beneficiaryService;
    }


    @PostMapping
    public ResponseEntity<BeneficiaryResponseDto> createBeneficiary(@Valid @RequestBody BeneficiaryCreateRequestDto beneficiaryCreateRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        return  new ResponseEntity<>(beneficiaryService.createBeneficiary(beneficiaryCreateRequestDto,userDetails.getUser()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponseDto>> viewAllBeneficiaries(@AuthenticationPrincipal CustomUserDetails userDetails){
        return  new ResponseEntity<>(beneficiaryService.viewMyBeneficiaries(userDetails.getUser()), HttpStatus.OK);
    }

    @GetMapping("/{beneficiaryId}")
    public ResponseEntity<BeneficiaryResponseDto> viewBeneficiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long beneficiaryId) {

        return new ResponseEntity<>(
                beneficiaryService.viewBeneficiary(userDetails.getUser(), beneficiaryId),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{beneficiaryId}/remove")
    public ResponseEntity<BeneficiaryResponseDto> removeBeneficiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long beneficiaryId) {

        return new ResponseEntity<>(
                beneficiaryService.removeBeneficiary(userDetails.getUser(), beneficiaryId),
                HttpStatus.OK
        );
    }

}
