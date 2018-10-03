package seedu.address.logic.sorter;

import java.util.Comparator;

import seedu.address.model.person.Person;

public class NameComparator implements Comparator<Person> {
    @Override
    public int compare(Person p1, Person p2) {
        return p1.getName().fullName.compareTo(p2.getName().fullName);
    }
}
