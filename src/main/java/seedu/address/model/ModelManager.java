package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.model.autocomplete.CommandCompleter;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook versionedAddressBook;
    private FilteredList<Person> filteredPersons;
    private CommandCompleter commandCompleter;
    private List<Person> selectedPersons;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        filteredPersons = new FilteredList<>(versionedAddressBook.getPersonList());
        commandCompleter = new CommandCompleter(this);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        versionedAddressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return versionedAddressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(versionedAddressBook));
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return versionedAddressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        versionedAddressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public void addPerson(Person person) {
        versionedAddressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void updatePerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        versionedAddressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo();
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
        indicateAddressBookChanged();
        //@@author lekoook
        commandCompleter = new CommandCompleter(this);
        //@@author
    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
        indicateAddressBookChanged();
        //@@author lekoook
        commandCompleter = new CommandCompleter(this);
        //@@author
    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedAddressBook.equals(other.versionedAddressBook)
                && filteredPersons.equals(other.filteredPersons);
    }

    //@@author lekoook
    /**
     * Retrieves a list of possible predictions for a command box input
     * @param textInput text input from command box
     * @return a list of predictions
     */
    @Override
    public ArrayList<String> getCmdPrediction(String textInput) {
        return commandCompleter.predictText(textInput);
    }

    /**
     * Adds a Person's attributes to the respective Trie instances for auto complete
     * @param person the person to add
     */
    public void addPersonToTrie(Person person) {
        commandCompleter.addPersonToTrie(person);
    }

    /**
     * Deletes a Person's attributes from the respective Trie instances for auto complete
     * @param person the person to delete
     */
    public void deletePersonFromTrie(Person person) {
        commandCompleter.deletePersonFromTrie(person);
    }

    /**
     * Removes all entries in all Trie instances
     */
    public void clearAllTries() {
        commandCompleter.clearAllTries();
    }

    /**
     * Edits a Person's attributes in each respective Trie instances for auto complete.
     * @param personToEdit the original person.
     * @param editedPerson the new person.
     */
    public void editPersonInTrie(Person personToEdit, Person editedPerson) {
        commandCompleter.editPersonInTrie(personToEdit, editedPerson);
    }

    public void setSelectedPersons(List<Person> selectedPersons) {
        this.selectedPersons = selectedPersons;
    }

    public List<Person> getSelectedPersons() {
        return this.selectedPersons;
    }

    public void setPersons(List<Person> personList) {
        versionedAddressBook.setPersons(personList);
    }
}
