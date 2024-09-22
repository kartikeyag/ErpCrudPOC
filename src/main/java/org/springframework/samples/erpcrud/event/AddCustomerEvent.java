package org.springframework.samples.erpcrud.event;

import jakarta.validation.Valid;
import org.springframework.samples.erpcrud.owner.Owner;

public class AddCustomerEvent  {
    private Owner owner;
    public AddCustomerEvent(@Valid Owner owner) {
        this.owner = owner;
    }

    public Owner getCustomer(){
        return owner;
    }
    @Override
    public String toString() {
        return "ApplicationEvent: New Customer Saved :: " + this.owner;
    }
}
