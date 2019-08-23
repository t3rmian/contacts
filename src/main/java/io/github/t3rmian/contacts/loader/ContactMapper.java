package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.model.Contact;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactMapper {
    public Contact mapToContact(String unknownContact) {
        Contact contact = new Contact();
        contact.setContact(unknownContact);
        contact.setType(guessContactType(unknownContact));
        return contact;
    }

    private Contact.Type guessContactType(String unknownContact) {
        if (EmailValidator.getInstance(false).isValid(unknownContact)) {
            return Contact.Type.EMAIL;
        }

        String phoneRegex = "^\\+?[0-9. ()-]{10,25}$";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher phoneMatcher = phonePattern.matcher(unknownContact);
        if (phoneMatcher.matches()) {
            return Contact.Type.PHONE;
        }

        String icqRegex = "^([0-9]-?){7,8}[0-9]$";
        Pattern icqPattern = Pattern.compile(icqRegex);
        Matcher icqMatcher = icqPattern.matcher(icqRegex);
        if (icqMatcher.matches()) {
            return Contact.Type.UNKNOWN;
        }

        String jabberRegex = "^(?:([^@/<>'\\\"]+)@)?([^@/<>'\\\"]+)(?:/([^<>'\\\"]*))?$\n";
        Pattern jabberPattern = Pattern.compile(jabberRegex);
        Matcher jabberMatcher = jabberPattern.matcher(jabberRegex);
        if (jabberMatcher.matches()) {
            return Contact.Type.JABBER;
        }

        return Contact.Type.UNKNOWN;
        /**
         * TODO: There are overlapping regexes, test and consider setting them to unknown?
         * TODO: Maybe it will be selected by the user? Think about it.
         */
    }
}
