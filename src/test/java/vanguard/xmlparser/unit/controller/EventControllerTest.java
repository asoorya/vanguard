package vanguard.xmlparser.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vanguard.xmlparser.controller.EventController;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    public void testProcessXmlFiles() throws Exception {
        doNothing().when(eventService).processXmlFiles();

        mockMvc.perform(post("/api/events/process"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFilterEvents() throws Exception {
        Event event1 = new Event("BUYER_1", "EMU_BANK", 100.0, "AUD");
        Event event2 = new Event("BUYER_2", "BISON_BANK", 200.0, "USD");

        List<Event> filteredEvents = Arrays.asList(event1, event2);

        when(eventService.filterEvents()).thenReturn(filteredEvents);

        mockMvc.perform(get("/api/events/filter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].buyerParty", is("BUYER_1")))
                .andExpect(jsonPath("$[0].sellerParty", is("EMU_BANK")))
                .andExpect(jsonPath("$[0].premiumAmount", is(100.0)))
                .andExpect(jsonPath("$[0].premiumCurrency", is("AUD")))
                .andExpect(jsonPath("$[1].buyerParty", is("BUYER_2")))
                .andExpect(jsonPath("$[1].sellerParty", is("BISON_BANK")))
                .andExpect(jsonPath("$[1].premiumAmount", is(200.0)))
                .andExpect(jsonPath("$[1].premiumCurrency", is("USD")));
    }

    @Test
    public void testFilterEvents_NoMatchingEvents() throws Exception {
        when(eventService.filterEvents()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/events/filter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testFilterEvents_Anagram() throws Exception {
        Event event = new Event("EMU_BANK", "EMU_BANK", 100.0, "AUD");

        when(eventService.filterEvents()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/events/filter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}




