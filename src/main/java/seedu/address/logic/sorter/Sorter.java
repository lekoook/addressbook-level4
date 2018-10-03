package seedu.address.logic.sorter;

import java.util.Collections;
import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Provides sorting functionality given a list of contacts.
 */
public class Sorter {
    public void sortByName(Model model) {
        List<Person> contacts = model.getFilteredPersonList();
        Collections.sort(contacts, new NameComparator());
    }
}
