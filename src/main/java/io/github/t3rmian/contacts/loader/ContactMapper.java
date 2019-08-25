package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.data.Contact;
import org.apache.commons.validator.routines.EmailValidator;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class ContactMapper {

    public Contact mapToContact(String type, String value) {
        Contact contact = new Contact();
        try {
            contact.setType(Contact.Type.valueOf(type.toUpperCase()));
        } catch (IllegalArgumentException iae) {
            contact.setType(Contact.Type.UNKNOWN);
        }
        contact.setContact(value);
        return contact;
    }

    public Contact mapToContact(String unknownContact) {
        Contact contact = new Contact();
        contact.setType(guessContactType(unknownContact));
        contact.setContact(unknownContact);
        return contact;
    }

    private Contact.Type guessContactType(String unknownContact) {
        if (EmailValidator.getInstance(false).isValid(unknownContact)) {
            return Contact.Type.EMAIL;
        } else if (containsPhoneNumber(unknownContact)) {
            return Contact.Type.PHONE;
        } else if (isJid(unknownContact)) {
            return Contact.Type.JABBER;
        } else {
            return Contact.Type.UNKNOWN;
        }
    }

    /**
     * @param unknownContact customer contact
     * @return true if unknownContact contains valid local or international number (5-15 digits) after removing any
     * context (non numeric characters).
     */
    private boolean containsPhoneNumber(String unknownContact) {
        String sanitizedContact = unknownContact.replaceAll("[^0-9]", "");
        return sanitizedContact.length() >= 5 && sanitizedContact.length() <= 15;
    }

    private boolean isJid(String unknownContact) {
        try {
            JidCreate.from(unknownContact);
            return true;
        } catch (IllegalArgumentException | XmppStringprepException e) {
            return false;
        }
    }
}
