package vanguard.xmlparser.component.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vanguard.xmlparser.component.ComponentTest;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@AutoConfigureMockMvc
@ComponentTest
public class EventControllerTest {

    @Autowired
    private EventService eventService;

    private static MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    void createEvent_validFile_returns200Successful() {
        mockMvc.perform(post("/api/events/process")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createEvent_validFile_savedInDB() {
        eventService.processXmlFiles();
       MvcResult mvcResult =  mockMvc.perform(get("/api/events/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

       String str = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Event> filteredEvents = objectMapper.readValue(str, new TypeReference<List<Event>>() {});
        assertEquals(4, filteredEvents.size());
        assertTrue(filteredEvents.stream().anyMatch(e -> "EMU_BANK".equals(e.getBuyerParty()) && "BISON_BANK".equals(e.getSellerParty())));
        assertTrue(filteredEvents.stream().anyMatch(e -> "LEFT_BANK".equals(e.getBuyerParty()) && "EMU_BANK".equals(e.getSellerParty())));
    }
}
