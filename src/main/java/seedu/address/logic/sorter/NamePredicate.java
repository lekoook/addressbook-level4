package seedu.address.logic.sorter;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.person.Person;

public class NamePredicate implements Predicate<Person> {
    private List<Person> names;

    public NamePredicate(List<Person> names) {
        this.names = names;
    }

    @Override
    public boolean test(Person person) {
        return names.contains(person);
    }
}
