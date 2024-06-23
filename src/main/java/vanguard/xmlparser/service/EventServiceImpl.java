package vanguard.xmlparser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import vanguard.xmlparser.repository.EventRepository;
import vanguard.xmlparser.repository.modal.Event;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    private String folderPath = "src/main/resources/xmlfiles";

    @Override
    public void processXmlFiles() {
        List<Event> events = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            // Folder containing XML files

            Files.list(Paths.get(folderPath)).forEach(path -> {
                try (InputStream inputStream = Files.newInputStream(path)) {
                    Document document = builder.parse(inputStream);

                    NodeList buyerPartyNodes = (NodeList) xPath.compile("//buyerPartyReference/@href").evaluate(document, XPathConstants.NODESET);
                    NodeList sellerPartyNodes = (NodeList) xPath.compile("//sellerPartyReference/@href").evaluate(document, XPathConstants.NODESET);
                    NodeList premiumAmountNodes = (NodeList) xPath.compile("//paymentAmount/amount").evaluate(document, XPathConstants.NODESET);
                    NodeList premiumCurrencyNodes = (NodeList) xPath.compile("//paymentAmount/currency").evaluate(document, XPathConstants.NODESET);

                    for (int i = 0; i < buyerPartyNodes.getLength(); i++) {
                        String buyerParty = buyerPartyNodes.item(i).getNodeValue();
                        String sellerParty = sellerPartyNodes.item(i).getNodeValue();
                        double premiumAmount = Double.parseDouble(premiumAmountNodes.item(i).getTextContent());
                        String premiumCurrency = premiumCurrencyNodes.item(i).getTextContent();

                        Event event = new Event(buyerParty, sellerParty, premiumAmount, premiumCurrency);
                        events.add(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            eventRepository.saveAll(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> filterEvents() {
        List<Event> allEvents = eventRepository.findAll();
        return allEvents.stream()
                .filter(event -> (("EMU_BANK".equals(event.getSellerParty()) && "AUD".equals(event.getPremiumCurrency())) ||
                        ("BISON_BANK".equals(event.getSellerParty()) && "USD".equals(event.getPremiumCurrency()))))
                .filter(event -> !areAnagrams(event.getBuyerParty(), event.getSellerParty()))
                .toList();
    }

    private boolean areAnagrams(String str1, String str2) {
        if (str1.length() != str2.length()) return false;
        char[] charArray1 = str1.toCharArray();
        char[] charArray2 = str2.toCharArray();
        java.util.Arrays.sort(charArray1);
        java.util.Arrays.sort(charArray2);
        return java.util.Arrays.equals(charArray1, charArray2);
    }
}

