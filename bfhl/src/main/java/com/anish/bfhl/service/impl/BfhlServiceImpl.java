package com.anish.bfhl.service.impl;

import com.anish.bfhl.dto.BfhlRequestDTO;
import com.anish.bfhl.dto.BfhlResponseDTO;
import com.anish.bfhl.service.BfhlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BfhlService.
 * Contains the core logic for processing input data arrays:
 * - Categorizing into numbers, alphabets, special characters
 * - Computing sum of all numbers
 * - Building alternating-caps reverse concat string from alphabets
 */
@Service
public class BfhlServiceImpl implements BfhlService {

    @Value("${app.user.full-name}")
    private String fullName;

    @Value("${app.user.dob}")
    private String dob;

    @Value("${app.user.email}")
    private String email;

    @Value("${app.user.roll-number}")
    private String rollNumber;

    @Override
    public BfhlResponseDTO processData(BfhlRequestDTO requestDTO) {
        List<String> data = requestDTO.getData();

        List<String> oddNumbers = new ArrayList<>();
        List<String> evenNumbers = new ArrayList<>();
        List<String> alphabets = new ArrayList<>();
        List<String> specialCharacters = new ArrayList<>();

        // List to collect individual alphabet characters (preserving input order)
        // for concat_string logic
        List<Character> allAlphaChars = new ArrayList<>();

        long sum = 0;

        for (String token : data) {
            if (isNumber(token)) {
                // Numbers: check odd/even
                long num = Long.parseLong(token);
                sum += num;
                if (num % 2 == 0) {
                    evenNumbers.add(token); // return as string as per requirement
                } else {
                    oddNumbers.add(token);
                }
            } else if (isAlphabet(token)) {
                // Alphabets: convert to uppercase for display
                alphabets.add(token.toUpperCase());
                // Collect each individual character (original case) for concat logic
                for (char c : token.toCharArray()) {
                    allAlphaChars.add(c);
                }
            } else {
                // Special characters (anything not a number and not purely alphabetic)
                // Check char by char - could be mixed, but per examples tokens are atomic
                specialCharacters.add(token);
            }
        }

        // Build concat_string:
        // 1. Take all individual alphabet chars collected in input order
        // 2. Reverse the list
        // 3. Apply alternating caps: index 0 → uppercase, index 1 → lowercase, etc.
        String concatString = buildConcatString(allAlphaChars);

        // Build user_id: full_name_ddmmyyyy (all lowercase)
        String userId = fullName.toLowerCase() + "_" + dob;

        return BfhlResponseDTO.builder()
                .isSuccess(true)
                .userId(userId)
                .email(email)
                .rollNumber(rollNumber)
                .oddNumbers(oddNumbers)
                .evenNumbers(evenNumbers)
                .alphabets(alphabets)
                .specialCharacters(specialCharacters)
                .sum(String.valueOf(sum))
                .concatString(concatString)
                .build();
    }

    /**
     * Checks if a token is purely numeric (supports multi-digit numbers).
     */
    private boolean isNumber(String token) {
        if (token == null || token.isEmpty()) return false;
        for (char c : token.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /**
     * Checks if a token contains only alphabetic characters.
     */
    private boolean isAlphabet(String token) {
        if (token == null || token.isEmpty()) return false;
        for (char c : token.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    /**
     * Builds the concat_string:
     * - Takes all individual alphabet characters from input (in order)
     * - Reverses the entire sequence
     * - Applies alternating caps: position 0 → UPPER, 1 → lower, 2 → UPPER, ...
     *
     * Example: ["a","R"] → chars=[a,R] → reversed=[R,a] → "Ra"
     * Example: ["A","ABCD","DOE"] → chars=[A,B,C,D,D,O,E] → reversed=[E,O,D,D,C,B,A]
     *          → alternating = E(upper), o(lower), D(upper), d(lower), C(upper), b(lower), A(upper)
     *          → "EoDdCbAa" ... wait, that's 8 chars
     *          Actually → "EoDdCbA" ... let me recheck Example C output: "EoDdCbAa"
     *          Chars from [A, ABCD, DOE] = A, A,B,C,D, D,O,E = 8 chars
     *          Reversed = E,O,D,D,C,B,A,A
     *          Alternating = E(U),o(L),D(U),d(L),C(U),b(L),A(U),a(L) = "EoDdCbAa" ✓
     */
    private String buildConcatString(List<Character> allAlphaChars) {
        if (allAlphaChars.isEmpty()) return "";

        // Reverse the list
        List<Character> reversed = new ArrayList<>(allAlphaChars);
        java.util.Collections.reverse(reversed);

        // Apply alternating caps
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reversed.size(); i++) {
            char c = reversed.get(i);
            if (i % 2 == 0) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }

        return sb.toString();
    }
}
