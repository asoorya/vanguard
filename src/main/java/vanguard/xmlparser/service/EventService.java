package vanguard.xmlparser.service;

import vanguard.xmlparser.repository.modal.Event;

import java.util.List;

public interface EventService {
    void processXmlFiles();
    List<Event> filterEvents();
}
