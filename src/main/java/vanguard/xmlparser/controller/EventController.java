package vanguard.xmlparser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/process")
    public ResponseEntity<Void> processXmlFiles() {
        eventService.processXmlFiles();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Event>> filterEvents() {
        List<Event> filteredEvents = eventService.filterEvents();
        return ResponseEntity.ok(filteredEvents);
    }
}

