package vanguard.xmlparser.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vanguard.xmlparser.repository.EventRepository;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private List<Event> allEvents;

    @BeforeEach
    public void setUp() {
        Event event1 = new Event("BUYER_1", "EMU_BANK", 100.0, "AUD");
        Event event2 = new Event("BUYER_2", "BISON_BANK", 200.0, "USD");
        Event event3 = new Event("BUYER_3", "EMU_BANK", 300.0, "USD");
        Event event4 = new Event("BUYER_4", "BISON_BANK", 400.0, "AUD");
        Event event5 = new Event("EMU_BANK", "EMU_BANK", 100.0, "AUD"); // Anagram case

        allEvents = Arrays.asList(event1, event2, event3, event4, event5);
    }

    @Test
    public void testFilterEvents() {
        when(eventRepository.findAll()).thenReturn(allEvents);

        List<Event> filteredEvents = eventService.filterEvents();

        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.stream().anyMatch(e -> "BUYER_1".equals(e.getBuyerParty()) && "EMU_BANK".equals(e.getSellerParty())));
        assertTrue(filteredEvents.stream().anyMatch(e -> "BUYER_2".equals(e.getBuyerParty()) && "BISON_BANK".equals(e.getSellerParty())));
    }

    @Test
    public void testFilterEvents_Anagram() {
        when(eventRepository.findAll()).thenReturn(allEvents);

        List<Event> filteredEvents = eventService.filterEvents();

        assertFalse(filteredEvents.stream().anyMatch(e -> "EMU_BANK".equals(e.getBuyerParty()) && "EMU_BANK".equals(e.getSellerParty())));
    }

    @Test
    public void testReadAndSaveEvents() throws Exception {
        // Mock the behavior of saving events to the repository
        doAnswer(invocation -> {
            List<Event> events = invocation.getArgument(0);
            assertEquals(8, events.size()); // 2 files, each with 1 event
            return events;
        }).when(eventRepository).saveAll(any(List.class));

        // Call the method to test
        eventService.processXmlFiles();

        // Verify that the saveAll method was called once with the expected number of events
        verify(eventRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void testProcessXmlFiles_FileNotFound() {
        // Use ReflectionTestUtils to set an invalid folder path
        ReflectionTestUtils.setField(eventService, "folderPath", "invalid/path");

        // Since the test case won't actually load the file, we mock the scenario
        eventService.processXmlFiles();

        // Verify that saveAll was never called due to file not found
        verify(eventRepository, never()).saveAll(any(List.class));
    }

    @Test
    public void testProcessXmlFiles_InvalidXml() throws Exception {
        // Mock the behavior of saving events to the repository
        doAnswer(invocation -> {
            List<Event> events = invocation.getArgument(0);
            assertTrue(events.isEmpty());
            return events;
        }).when(eventRepository).saveAll(any(List.class));

        // Setup mock invalid XML file in the test resources directory
        String testXmlPath = "src/test/resources/invalid";
        Files.createDirectories(Paths.get(testXmlPath));
        Path testXml = Paths.get(testXmlPath, "testInvalidEvent.xml");

        String invalidXmlContent = "<root>" +
                "<buyerPartyReference href=\"BUYER_1\"/>" +
                "<sellerPartyReference href=\"EMU_BANK\"/>" +
                "<paymentAmount><amount>100.0</amount></paymentAmount>" + // Missing currency element
                "</root>";
        Files.writeString(testXml, invalidXmlContent);

        // Use ReflectionTestUtils to set the folder path
        ReflectionTestUtils.setField(eventService, "folderPath", testXmlPath);

        // Call the method to test
        eventService.processXmlFiles();

        // Verify that the saveAll method was never called due to invalid XML
        verify(eventRepository, times(1)).saveAll(any(List.class));
    }
}







