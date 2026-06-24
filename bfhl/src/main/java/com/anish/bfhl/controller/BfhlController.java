package com.anish.bfhl.controller;

import com.anish.bfhl.dto.BfhlRequestDTO;
import com.anish.bfhl.dto.BfhlResponseDTO;
import com.anish.bfhl.service.BfhlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for BFHL API.
 * Exposes POST /bfhl endpoint as per the problem statement.
 */
@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private final BfhlService bfhlService;

    @Autowired
    public BfhlController(BfhlService bfhlService) {
        this.bfhlService = bfhlService;
    }

    /**
     * POST /bfhl
     * Accepts a JSON array of strings and returns categorized data.
     *
     * @param requestDTO request body containing "data" array
     * @return 200 OK with categorized response
     */
    @PostMapping
    public ResponseEntity<BfhlResponseDTO> processData(
            @Valid @RequestBody BfhlRequestDTO requestDTO) {

        BfhlResponseDTO response = bfhlService.processData(requestDTO);
        return ResponseEntity.ok(response);
    }
}
