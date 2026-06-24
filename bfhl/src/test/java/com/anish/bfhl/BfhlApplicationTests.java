package com.anish.bfhl;

import com.anish.bfhl.dto.BfhlRequestDTO;
import com.anish.bfhl.dto.BfhlResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BfhlApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── Helper ──────────────────────────────────────────────────────────────

    private BfhlResponseDTO postAndParse(List<String> data) throws Exception {
        BfhlRequestDTO request = new BfhlRequestDTO(data);
        MvcResult result = mockMvc.perform(
                post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BfhlResponseDTO.class
        );
    }

    // ─── Example A ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Example A: [a, 1, 334, 4, R, $]")
    void testExampleA() throws Exception {
        BfhlResponseDTO response = postAndParse(Arrays.asList("a", "1", "334", "4", "R", "$"));

        assertTrue(response.isSuccess());

        // Numbers
        assertEquals(List.of("1"), response.getOddNumbers());
        assertEquals(List.of("334", "4"), response.getEvenNumbers());

        // Alphabets (uppercase)
        assertEquals(List.of("A", "R"), response.getAlphabets());

        // Special characters
        assertEquals(List.of("$"), response.getSpecialCharacters());

        // Sum
        assertEquals("339", response.getSum());

        // concat_string: chars=[a,R], reversed=[R,a], alternating → "Ra"
        assertEquals("Ra", response.getConcatString());
    }

    // ─── Example B ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Example B: [2, a, y, 4, &, -, *, 5, 92, b]")
    void testExampleB() throws Exception {
        BfhlResponseDTO response = postAndParse(
                Arrays.asList("2", "a", "y", "4", "&", "-", "*", "5", "92", "b"));

        assertTrue(response.isSuccess());

        // Numbers
        assertEquals(List.of("5"), response.getOddNumbers());
        assertEquals(List.of("2", "4", "92"), response.getEvenNumbers());

        // Alphabets (uppercase)
        assertEquals(List.of("A", "Y", "B"), response.getAlphabets());

        // Special characters
        assertEquals(List.of("&", "-", "*"), response.getSpecialCharacters());

        // Sum: 2+4+5+92 = 103
        assertEquals("103", response.getSum());

        // concat_string: chars=[a,y,b], reversed=[b,y,a], alternating → B(U),y(L),A(U) = "ByA"
        assertEquals("ByA", response.getConcatString());
    }

    // ─── Example C ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Example C: [A, ABCD, DOE]")
    void testExampleC() throws Exception {
        BfhlResponseDTO response = postAndParse(Arrays.asList("A", "ABCD", "DOE"));

        assertTrue(response.isSuccess());

        // No numbers
        assertEquals(List.of(), response.getOddNumbers());
        assertEquals(List.of(), response.getEvenNumbers());

        // Alphabets
        assertEquals(List.of("A", "ABCD", "DOE"), response.getAlphabets());

        // No special chars
        assertEquals(List.of(), response.getSpecialCharacters());

        // Sum = 0
        assertEquals("0", response.getSum());

        // concat_string:
        // chars from A=A, ABCD=A,B,C,D, DOE=D,O,E → [A,A,B,C,D,D,O,E]
        // reversed → [E,O,D,D,C,B,A,A]
        // alternating → E(U),o(L),D(U),d(L),C(U),b(L),A(U),a(L) = "EoDdCbAa"
        assertEquals("EoDdCbAa", response.getConcatString());
    }

    // ─── Edge Cases ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Empty data array should return zero sum and empty lists")
    void testEmptyArray() throws Exception {
        BfhlResponseDTO response = postAndParse(List.of());

        assertTrue(response.isSuccess());
        assertEquals(List.of(), response.getOddNumbers());
        assertEquals(List.of(), response.getEvenNumbers());
        assertEquals(List.of(), response.getAlphabets());
        assertEquals(List.of(), response.getSpecialCharacters());
        assertEquals("0", response.getSum());
        assertEquals("", response.getConcatString());
    }

    @Test
    @DisplayName("Only numbers in input")
    void testOnlyNumbers() throws Exception {
        BfhlResponseDTO response = postAndParse(Arrays.asList("2", "3", "10"));

        assertTrue(response.isSuccess());
        assertEquals(List.of("3"), response.getOddNumbers());
        assertEquals(List.of("2", "10"), response.getEvenNumbers());
        assertEquals(List.of(), response.getAlphabets());
        assertEquals("15", response.getSum());
        assertEquals("", response.getConcatString());
    }

    @Test
    @DisplayName("Missing 'data' field returns 400")
    void testMissingDataField() throws Exception {
        mockMvc.perform(
                post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Malformed JSON returns 400")
    void testMalformedJson() throws Exception {
        mockMvc.perform(
                post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Response contains user_id, email, roll_number fields")
    void testResponseMetaFields() throws Exception {
        BfhlResponseDTO response = postAndParse(List.of("1"));

        assertNotNull(response.getUserId());
        assertNotNull(response.getEmail());
        assertNotNull(response.getRollNumber());
        // user_id format: name_ddmmyyyy (all lowercase, no uppercase)
        assertEquals(response.getUserId(), response.getUserId().toLowerCase()
                .replace(" ", "_"));
    }
}
