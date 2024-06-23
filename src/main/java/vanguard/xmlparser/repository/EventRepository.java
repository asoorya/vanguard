package vanguard.xmlparser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vanguard.xmlparser.repository.modal.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}

