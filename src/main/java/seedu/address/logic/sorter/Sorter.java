package seedu.address.logic.sorter;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;

/**
 * Provides sorting functionality given a list of contacts.
 */
public class Sorter {
    public static void sortByName(Model model) {
        List<Person> contacts = new ArrayList<>(model.getAddressBook().getPersonList());
        contacts.sort(new NameComparator());
        model.setPersons(contacts);
    }

    private static List<String> extractNames(List<Person> personList) {
        List<String> output = new ArrayList<>();
        for (Person person : personList) {
            output.add(person.getName().fullName);
        }
        return output;
    }
}
