package com.anish.bfhl.service;

import com.anish.bfhl.dto.BfhlRequestDTO;
import com.anish.bfhl.dto.BfhlResponseDTO;

/**
 * Service interface for BFHL API processing logic.
 * Defines the contract for processing input data arrays.
 */
public interface BfhlService {

    /**
     * Processes the input data array and returns categorized response.
     *
     * @param requestDTO the request containing data array
     * @return BfhlResponseDTO with categorized data, sum, and concat string
     */
    BfhlResponseDTO processData(BfhlRequestDTO requestDTO);
}
