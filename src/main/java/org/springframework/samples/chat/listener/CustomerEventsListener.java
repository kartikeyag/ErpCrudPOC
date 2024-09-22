package org.springframework.samples.chat.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.EventListener;
import org.springframework.samples.erpcrud.event.AddCustomerEvent;
import org.springframework.samples.erpcrud.owner.Owner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CustomerEventsListener {
    private final VectorStore vectorStore;

    public CustomerEventsListener(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @EventListener
    void handleAddCustomerEvent(AddCustomerEvent addCustomerEvent){
        Owner owner = addCustomerEvent.getCustomer();
        List<Document> documentList = new ArrayList<Document>();
        log.info(addCustomerEvent.toString());
        var document = new Document("Name is " + owner.getFirstName() + " " + owner.getLastName()+ " Address is " + owner.getAddress()+" Telephone is "+ owner.getTelephone(),
                Map.of("phone", owner.getTelephone(), "id", owner.getId(), "firstname", owner.getFirstName(),
                        "lastname", owner.getLastName(), "address", owner.getAddress()));

        documentList.add(document);
        vectorStore.add(documentList);

    }
}
