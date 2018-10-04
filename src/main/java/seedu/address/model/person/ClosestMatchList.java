//@@author lws803
package seedu.address.model.person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.collections.ObservableList;

import seedu.address.commons.util.LevenshteinDistanceUtil;
import seedu.address.model.Model;

/**
 * To generate a list of closest matches
 * Description: We run thru all arguments given together will all the names
 * (First and last)
 * Then attach a Levensthein distance to each of them to form a pair
 * The pairs are then stored in a treemap which we will generate another list
 * from the first few
 */
public class ClosestMatchList {
    private int lowestDist = Integer.MAX_VALUE;
    private ObservableList<Person> listToFilter;
    private List<String> approvedNames = new ArrayList<String>();
    private Map<String, Integer> discoveredNames = new TreeMap<String, Integer>();

    /**
     * Pair of integer and string
     */
    private static class Pair {
        private int dist;
        private String nameSegment;

        private Pair(int a, String b) {
            this.dist = a;
            this.nameSegment = b;
        }

        private int getDist () {
            return this.dist;
        }

        private String getNameSegment () {
            return nameSegment;
        }
    }

    private Set <Pair> nameMap = new TreeSet<Pair>(new Comparator<Pair>() {
        @Override
        public int compare(Pair o1, Pair o2) {
            if (o1.getDist() - o2.getDist() == 0) {
                if (o1.getDist() == o2.getDist()) {
                    return 1;
                } else {
                    return o1.getNameSegment().compareTo(o2.getNameSegment());
                }
            }
            return o1.getDist() - o2.getDist();
        }
    });

    /**
     * Enumerator for attributes of a person
     */
    private enum PersonAttribute {
        NAME, PHONENUMER, EMAIL, ADDRESS, TAGS
    }

    /**
     * Filters and generates maps from names from model
     * and arguments
     */
    public ClosestMatchList (Model model, String argument, String[] searchKeys) {
        this.listToFilter = model.getAddressBook().getPersonList();

        for (Person person: listToFilter) {
            generateNameMapFromAttrib(searchKeys, person, obtainAttribute(argument));
        }

        addToApprovedNamesList();
    }

    /**
     * Obtains the argument in string form and translates it to enumerator PersonAttribute
     * @param argument obtains the string argument from findCommand class
     * @return PersonAttribute
     */
    // TODO: Add the complete list of attribs
    private PersonAttribute obtainAttribute (String argument) {
        if (argument.compareTo("NAME") == 0) {
            return PersonAttribute.NAME;
        } else if (argument.compareTo("PHONENUMBER") == 0) {
            return PersonAttribute.PHONENUMER;
        }
        return PersonAttribute.NAME;
    }

    /**
     * Bulk of the computation
     * Runs thru model and stores the pairs in a tree out of
     * similarity indexes using levensthein distances together with nameSegment
     */
    private void generateNameMapFromAttrib (String[] searchKey, Person person, PersonAttribute attribute) {
        String compareString = person.getName().fullName;
        switch (attribute) {
        case NAME:
            compareString = person.getName().fullName;
            break;
        case PHONENUMER:
            compareString = person.getPhone().value;
            break;
        case ADDRESS:
            compareString = person.getAddress().value;
            break;
        case EMAIL:
            compareString = person.getEmail().value;
            break;
        default:
            compareString = person.getName().fullName;
        }

        generateNameMap(searchKey, compareString);
    }


    /**
     * Generate the namemap from the compareString provided
     * @param searchKey obtained from arguments in FindCommand
     * @param compareString obtained from personList as per attribute
     */
    // TODO: Add it such that when accurate names are typed out, the results wont show the rest.
    private void generateNameMap(String[] searchKey, String compareString) {
        String[] stringSplitted = compareString.split("\\s+");
        for (String nameSegment: stringSplitted) {

            for (String nameArg: searchKey) {
                int dist = LevenshteinDistanceUtil.levenshteinDistance(nameArg.toLowerCase(),
                        nameSegment.toLowerCase());

                if (dist < lowestDist) {
                    lowestDist = dist;
                }

                Pair distNamePair = new Pair(dist, nameSegment);

                if (!discoveredNames.containsKey(nameSegment)) {
                    nameMap.add(distNamePair);
                    discoveredNames.put(nameSegment, dist);
                } else if (discoveredNames.get(nameSegment) > dist) {
                    discoveredNames.replace(nameSegment, dist); // Replace with the new dist
                    nameMap.add(distNamePair); // Check to see if this will replace
                }
            }

        }
    }

    /**
     * Add the contents in the tree to a name list
     */
    private void addToApprovedNamesList() {
        for (Pair pair: nameMap) {
            if (pair.getDist() - lowestDist > 1) {
                // Break the loop when distances get too far
                return;
            }
            approvedNames.add(pair.getNameSegment());
        }
    }

    /**
     * Gets the approved list
     */
    public String[] getApprovedList () {
        return approvedNames.toArray(new String[0]);
    }

}
