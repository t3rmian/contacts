package io.github.t3rmian.contacts.data;

public class Contact {
    private Contact.Type type;
    private Long customerId;
    private String contact;

    public enum Type {
        UNKNOWN, EMAIL, PHONE, JABBER
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
