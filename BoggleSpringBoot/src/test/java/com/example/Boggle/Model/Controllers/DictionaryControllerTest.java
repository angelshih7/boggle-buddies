package com.example.Boggle.Model.Controllers;
import com.example.Boggle.Model.Tables.Dictionary;
import com.example.Boggle.repository.DictionaryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(DictionaryController.class)
class DictionaryControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DictionaryRepository dictionaryRepository;

    @Test
    void getAllWords_returnsJson() throws Exception {
        Dictionary word = new Dictionary();
        word.setWord("cat");
        word.setPointValue(3);

        when(dictionaryRepository.findAll()).thenReturn(List.of(word));

        mockMvc.perform(get("/api/dictionary/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("cat"))
                .andExpect(jsonPath("$[0].pointValue").value(3));
    }
}