package customtreeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomTreeSetTest {

    private CustomTreeSet<Integer> set;

    @BeforeEach
    void setUp() {
        set = new CustomTreeSet<>();
    }


    @Test
    void testAddSingleElement() {
        assertTrue(set.add(10), "Adding a new element should return true");
    }

    @Test
    void testAddDuplicateElement() {
        set.add(10);
        assertFalse(set.add(10), "Adding a duplicate element should return false");
        assertEquals(1, set.size(), "Size should not increase on duplicate addition");
    }

    @Test
    void testAddMultipleUniqueElements() {
        assertTrue(set.add(10));
        assertTrue(set.add(5));
        assertTrue(set.add(15));
        assertEquals(3, set.size());
    }

    @Test
    void testAddWithCustomComparator() {
        CustomTreeSet<String> lengthSet = new CustomTreeSet<>(Comparator.comparingInt(String::length));

        assertTrue(lengthSet.add("Apple"));
        assertEquals(1, lengthSet.size());
        assertFalse(lengthSet.add("Peach"),
                "Elements with the same comparator weight should be treated as duplicates");
        assertEquals(1, lengthSet.size());
    }


    @Test
    void testAddNullWithNaturalOrderingThrowsNPE() {
        assertThrows(NullPointerException.class, () -> set.add(null), "Adding null to a natural-ordered TreeSet should throw NPE");
    }

    @Test
    void testAddNullWithNullSafeComparator() {
        CustomTreeSet<Integer> nullSafeSet = new CustomTreeSet<>(
                Comparator.nullsFirst(Integer::compareTo)
        );

        assertTrue(nullSafeSet.add(null), "Should allow null if comparator supports it");
        assertFalse(nullSafeSet.add(null), "Should reject duplicate nulls");
    }

    @Test
    @DisplayName("addAll should return true and add all unique elements from collection")
    void testAddAllNewElements() {
        List<Integer> list = List.of(5, 2, 8, 1);

        boolean modified = set.addAll(list);

        assertTrue(modified, "addAll should return true when set is modified");
        assertEquals(4, set.size(), "Set size should equal the number of unique elements added");
    }

    @Test
    @DisplayName("addAll should return false when adding an empty collection")
    void testAddAllEmptyCollection() {
        boolean modified = set.addAll(Collections.emptyList());

        assertFalse(modified, "addAll should return false when collection is empty");
        assertEquals(0, set.size(), "Set size should remain 0");
    }

    @Test
    @DisplayName("addAll should handle duplicate elements within the incoming collection")
    void testAddAllWithInternalDuplicates() {
        List<Integer> list = List.of(10, 20, 10, 30, 20);

        boolean modified = set.addAll(list);

        assertTrue(modified);
        assertEquals(3, set.size(), "Set should filter out internal duplicates from incoming collection");
    }


    @Test
    @DisplayName("addAll should return true when only some elements are new")
    void testAddAllPartialDuplicates() {
        set.add(10);
        set.add(20);

        List<Integer> list = List.of(20, 30, 40);

        boolean modified = set.addAll(list);

        assertTrue(modified, "addAll should return true if at least one new element is added");
        assertEquals(4, set.size(), "Size should reflect total distinct elements (10, 20, 30, 40)");
    }

    @Test
    @DisplayName("addAll should return false when all elements already exist in the set")
    void testAddAllAllDuplicates() {
        set.add(10);
        set.add(20);

        List<Integer> list = List.of(10, 20);

        boolean modified = set.addAll(list);

        assertFalse(modified, "addAll should return false when no new elements are added");
        assertEquals(2, set.size(), "Size should remain unchanged");
    }

    @Test
    @DisplayName("addAll should throw NullPointerException if target collection is null")
    void testAddAllNullCollectionThrowsNPE() {
        assertThrows(NullPointerException.class, () -> set.addAll(null));
    }

    @Test
    @DisplayName("addAll should throw NullPointerException when collection contains null (natural order)")
    void testAddAllCollectionContainingNullThrowsNPE() {
        List<Integer> list = Arrays.asList(5, null, 10);
        assertThatThrownBy(() -> set.addAll(list))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Constructor CustomTreeSet(Collection) should populate tree via addAll")
    void testConstructorWithCollection() {
        List<Integer> list = List.of(40, 10, 20);
        CustomTreeSet<Integer> populatedSet = new CustomTreeSet<>(list);

        assertEquals(3, populatedSet.size());
    }

    @Test
    @DisplayName("Default constructor initializes empty set with natural ordering")
    void testDefaultConstructor() {
        CustomTreeSet<String> set = new CustomTreeSet<>();

        assertEquals(0, set.size());
        assertNull(set.comparator());
    }

    @Test
    @DisplayName("Comparator constructor assigns custom comparator and leaves set empty")
    void testComparatorConstructor() {
        Comparator<String> customComp = String.CASE_INSENSITIVE_ORDER;
        CustomTreeSet<String> set = new CustomTreeSet<>(customComp);

        assertEquals(0, set.size());
        assertEquals(customComp, set.comparator());

        assertTrue(set.add("apple"));
        assertFalse(set.add("APPLE"));
    }

    @Test
    @DisplayName("Collection constructor populates unique elements with natural ordering")
    void testCollectionConstructor() {
        List<Integer> list = List.of(5, 1, 3, 1, 5);
        CustomTreeSet<Integer> set = new CustomTreeSet<>(list);

        assertEquals(3, set.size());
        assertNull(set.comparator());
    }

    @Test
    @DisplayName("Collection constructor throws NullPointerException when collection is null")
    void testCollectionConstructorWithNullCollection() {
        assertThrows(NullPointerException.class, () -> new CustomTreeSet<>((Collection<Integer>) null));
    }

    @Test
    @DisplayName("SortedSet constructor inherits comparator and populates elements")
    void testSortedSetConstructorWithCustomComparator() {
        SortedSet<String> sourceSet = new TreeSet<>(Comparator.reverseOrder());
        sourceSet.add("banana");
        sourceSet.add("apple");

        CustomTreeSet<String> set = new CustomTreeSet<>(sourceSet);

        assertEquals(2, set.size());
        assertEquals(Comparator.reverseOrder(), set.comparator());
    }

    @Test
    @DisplayName("SortedSet constructor handles natural ordering SortedSet (null comparator)")
    void testSortedSetConstructorWithNaturalOrdering() {
        SortedSet<Integer> sourceSet = new TreeSet<>();
        sourceSet.add(10);
        sourceSet.add(20);

        CustomTreeSet<Integer> set = new CustomTreeSet<>(sourceSet);

        assertEquals(2, set.size());
        assertNull(set.comparator());
    }

    @Test
    @DisplayName("SortedSet constructor throws NullPointerException when argument is null")
    void testSortedSetConstructorWithNull() {
        assertThrows(NullPointerException.class, () -> new CustomTreeSet<>((SortedSet<Integer>) null));
    }

    @Test
    @DisplayName("ceiling returns exact element when element exists in set")
    void testCeilingExactMatch() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(20, set.ceiling(20));
    }

    @Test
    @DisplayName("ceiling returns least element strictly greater than given item when exact match does not exist")
    void testCeilingIntermediateValue() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(20, set.ceiling(15));
    }

    @Test
    @DisplayName("ceiling returns smallest element when given item is smaller than all set elements")
    void testCeilingLessThanMinimum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(10, set.ceiling(5));
    }

    @Test
    @DisplayName("ceiling returns null when given item is greater than all set elements")
    void testCeilingGreaterThanMaximum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertNull(set.ceiling(35));
    }

    @Test
    @DisplayName("ceiling returns null on an empty set")
    void testCeilingEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertNull(set.ceiling(10));
    }

    @Test
    @DisplayName("ceiling throws NullPointerException when item is null under natural ordering")
    void testCeilingNullItemThrowsNPE() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(NullPointerException.class, () -> set.ceiling(null));
    }

    @Test
    @DisplayName("ceiling respects custom comparator ordering")
    void testCeilingWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(10, 20, 30));

        assertEquals(20, set.ceiling(25));
    }

    @Test
    @DisplayName("clear empties a populated set and resets size to zero")
    void testClearPopulatedSet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        set.clear();

        assertTrue(set.isEmpty());
        assertNull(set.ceiling(10));
    }

    @Test
    @DisplayName("clear on an already empty set has no effect")
    void testClearEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        set.clear();

        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("set functions correctly when adding elements after clear")
    void testAddAfterClear() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        set.clear();
        assertTrue(set.add(50));
        assertEquals(1, set.size());
        assertEquals(50, set.ceiling(10));
    }

    @Test
    @DisplayName("lower returns greatest element strictly less than given item when exact match exists")
    void testLowerExactMatch() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(10, set.lower(20));
    }

    @Test
    @DisplayName("lower returns greatest element strictly less than given item when exact match does not exist")
    void testLowerIntermediateValue() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(20, set.lower(25));
    }

    @Test
    @DisplayName("lower returns maximum element when given item is greater than all set elements")
    void testLowerGreaterThanMaximum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(30, set.lower(35));
    }

    @Test
    @DisplayName("lower returns null when given item is less than or equal to minimum element")
    void testLowerLessThanOrEqualToMinimum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertNull(set.lower(10));
        assertNull(set.lower(5));
    }

    @Test
    @DisplayName("lower returns null on an empty set")
    void testLowerEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertNull(set.lower(10));
    }

    @Test
    @DisplayName("lower throws NullPointerException when item is null under natural ordering")
    void testLowerNullItemThrowsNPE() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(NullPointerException.class, () -> set.lower(null));
    }

    @Test
    @DisplayName("lower respects custom comparator ordering")
    void testLowerWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(10, 20, 30));

        assertEquals(30, set.lower(20));
    }

    @Test
    @DisplayName("floor returns exact element when element exists in set")
    void testFloorExactMatch() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(20, set.floor(20));
    }

    @Test
    @DisplayName("floor returns greatest element less than given item when exact match does not exist")
    void testFloorIntermediateValue() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(20, set.floor(25));
    }

    @Test
    @DisplayName("floor returns maximum element when given item is greater than all set elements")
    void testFloorGreaterThanMaximum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(30, set.floor(35));
    }

    @Test
    @DisplayName("floor returns null when given item is less than minimum element")
    void testFloorLessThanMinimum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertNull(set.floor(5));
    }

    @Test
    @DisplayName("floor returns null on an empty set")
    void testFloorEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertNull(set.floor(10));
    }

    @Test
    @DisplayName("floor throws NullPointerException when item is null under natural ordering")
    void testFloorNullItemThrowsNPE() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(NullPointerException.class, () -> set.floor(null));
    }

    @Test
    @DisplayName("floor respects custom comparator ordering")
    void testFloorWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(10, 20, 30));

        assertEquals(30, set.floor(25));
    }

    @Test
    @DisplayName("first returns lowest element in natural order")
    void testFirstNaturalOrder() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30));

        assertEquals(10, set.first());
    }

    @Test
    @DisplayName("first returns the single element when set size is 1")
    void testFirstSingleElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();
        set.add(42);

        assertEquals(42, set.first());
    }

    @Test
    @DisplayName("first throws NoSuchElementException when set is empty")
    void testFirstEmptySetThrowsException() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertThrows(NoSuchElementException.class, set::first);
    }

    @Test
    @DisplayName("first respects custom comparator ordering")
    void testFirstCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(20, 10, 30));

        assertEquals(30, set.first());
    }

    @Test
    @DisplayName("first updates dynamically after inserting a smaller element")
    void testFirstAfterModification() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 30));
        assertEquals(20, set.first());

        set.add(5);
        assertEquals(5, set.first());
    }

    @Test
    @DisplayName("higher returns least element strictly greater than given item when exact match exists")
    void testHigherExactMatch() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(30, set.higher(20));
    }

    @Test
    @DisplayName("higher returns least element strictly greater than given item when exact match does not exist")
    void testHigherIntermediateValue() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(20, set.higher(15));
    }

    @Test
    @DisplayName("higher returns minimum element when given item is less than all set elements")
    void testHigherLessThanMinimum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertEquals(10, set.higher(5));
    }

    @Test
    @DisplayName("higher returns null when given item is greater than or equal to maximum element")
    void testHigherGreaterThanOrEqualToMaximum() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertNull(set.higher(30));
        assertNull(set.higher(35));
    }

    @Test
    @DisplayName("higher returns null on an empty set")
    void testHigherEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertNull(set.higher(10));
    }

    @Test
    @DisplayName("higher throws NullPointerException when item is null under natural ordering")
    void testHigherNullItemThrowsNPE() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(NullPointerException.class, () -> set.higher(null));
    }

    @Test
    @DisplayName("higher respects custom comparator ordering")
    void testHigherWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(10, 20, 30));

        assertEquals(10, set.higher(20));
    }

    @Test
    @DisplayName("isEmpty returns true for a newly created set")
    void testIsEmptyOnNewSet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("isEmpty returns false after adding an element")
    void testIsEmptyAfterAddingElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();
        set.add(10);

        assertFalse(set.isEmpty());
    }

    @Test
    @DisplayName("isEmpty returns true after clearing a populated set")
    void testIsEmptyAfterClear() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        set.clear();

        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("contains returns true when element exists in set")
    void testContainsExistingElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertTrue(set.contains(20));
    }

    @Test
    @DisplayName("contains returns false when element does not exist in set")
    void testContainsNonExistingElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertFalse(set.contains(25));
        assertFalse(set.contains(5));
        assertFalse(set.contains(35));
    }

    @Test
    @DisplayName("contains returns false on an empty set")
    void testContainsEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertFalse(set.contains(10));
    }

    @Test
    @DisplayName("contains returns false after set is cleared")
    void testContainsAfterClear() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        set.clear();

        assertFalse(set.contains(10));
    }

    @Test
    @DisplayName("contains respects custom comparator ordering")
    void testContainsWithCustomComparator() {
        CustomTreeSet<String> set = new CustomTreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.add("apple");

        assertTrue(set.contains("APPLE"));
        assertFalse(set.contains("banana"));
    }

    @Test
    @DisplayName("contains throws NullPointerException when checking null under natural ordering")
    void testContainsNullThrowsNPE() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(NullPointerException.class, () -> set.contains(null));
    }

    @Test
    @DisplayName("remove returns true and updates size when removing a leaf node")
    void testRemoveLeafNode() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertTrue(set.remove(30));
        assertEquals(2, set.size());
        assertFalse(set.contains(30));
    }

    @Test
    @DisplayName("remove returns true and maintains structure when removing a node with one child")
    void testRemoveNodeWithOneChild() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30, 40));

        assertTrue(set.remove(30));
        assertEquals(3, set.size());
        assertFalse(set.contains(30));
        assertTrue(set.contains(40));
    }

    @Test
    @DisplayName("remove returns true and maintains correct order when removing a node with two children")
    void testRemoveNodeWithTwoChildren() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30, 25, 35));

        assertTrue(set.remove(30));
        assertEquals(4, set.size());
        assertFalse(set.contains(30));
        assertTrue(set.contains(25));
        assertTrue(set.contains(35));
    }

    @Test
    @DisplayName("remove correctly updates root when removing root element")
    void testRemoveRootElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30));

        assertTrue(set.remove(20));
        assertEquals(2, set.size());
        assertFalse(set.contains(20));
        assertEquals(10, set.first());
    }

    @Test
    @DisplayName("remove empties set when removing single remaining element")
    void testRemoveSingleElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10));

        assertTrue(set.remove(10));
        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("remove returns false when element does not exist in set")
    void testRemoveNonExistentElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertFalse(set.remove(25));
        assertEquals(3, set.size());
    }

    @Test
    @DisplayName("remove returns false on an empty set")
    void testRemoveFromEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertFalse(set.remove(10));
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("remove throws NullPointerException when element is null under natural ordering")
    void testRemoveNullThrowsNPE() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(NullPointerException.class, () -> set.remove(null));
    }

    @Test
    @DisplayName("remove respects custom comparator ordering")
    void testRemoveWithCustomComparator() {
        CustomTreeSet<String> set = new CustomTreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(List.of("apple", "banana", "cherry"));

        assertTrue(set.remove("BANANA"));
        assertEquals(2, set.size());
        assertFalse(set.contains("banana"));
    }

    @Test
    @DisplayName("pollFirst retrieves and removes the lowest element in natural order")
    void testPollFirstPopulatedSet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30));

        assertEquals(10, set.pollFirst());
        assertEquals(2, set.size());
        assertFalse(set.contains(10));
        assertEquals(20, set.first());
    }

    @Test
    @DisplayName("pollFirst returns null on an empty set without throwing an exception")
    void testPollFirstEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertNull(set.pollFirst());
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("pollFirst empties the set when removing the single remaining element")
    void testPollFirstSingleElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(42));

        assertEquals(42, set.pollFirst());
        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
        assertNull(set.pollFirst());
    }

    @Test
    @DisplayName("pollFirst sequentially drains elements in ascending order until empty")
    void testPollFirstSequentialCalls() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(30, 10, 20));

        assertEquals(10, set.pollFirst());
        assertEquals(20, set.pollFirst());
        assertEquals(30, set.pollFirst());
        assertNull(set.pollFirst());
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("pollFirst respects custom comparator ordering")
    void testPollFirstWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(20, 10, 30));

        assertEquals(30, set.pollFirst());
        assertEquals(2, set.size());
        assertFalse(set.contains(30));
        assertEquals(20, set.first());
    }

    @Test
    @DisplayName("pollLast retrieves and removes the highest element in natural order")
    void testPollLastPopulatedSet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30));

        assertEquals(30, set.pollLast());
        assertEquals(2, set.size());
        assertFalse(set.contains(30));
        assertEquals(20, set.last());
    }

    @Test
    @DisplayName("pollLast returns null on an empty set without throwing an exception")
    void testPollLastEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertNull(set.pollLast());
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("pollLast empties the set when removing the single remaining element")
    void testPollLastSingleElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(42));

        assertEquals(42, set.pollLast());
        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
        assertNull(set.pollLast());
    }

    @Test
    @DisplayName("pollLast sequentially drains elements in descending order until empty")
    void testPollLastSequentialCalls() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(30, 10, 20));

        assertEquals(30, set.pollLast());
        assertEquals(20, set.pollLast());
        assertEquals(10, set.pollLast());
        assertNull(set.pollLast());
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("pollLast respects custom comparator ordering")
    void testPollLastWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(20, 10, 30));

        assertEquals(10, set.pollLast());
        assertEquals(2, set.size());
        assertFalse(set.contains(10));
        assertEquals(20, set.last());
    }

    @Test
    @DisplayName("last returns highest element in natural order")
    void testLastNaturalOrder() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30));

        assertEquals(30, set.last());
    }

    @Test
    @DisplayName("last returns single element when set size is 1")
    void testLastSingleElement() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();
        set.add(42);

        assertEquals(42, set.last());
    }

    @Test
    @DisplayName("last throws NoSuchElementException when set is empty")
    void testLastEmptySetThrowsException() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();

        assertThrows(NoSuchElementException.class, set::last);
    }

    @Test
    @DisplayName("last respects custom comparator ordering")
    void testLastCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(20, 10, 30));

        assertEquals(10, set.last());
    }

    @Test
    @DisplayName("last updates dynamically after adding and removing elements")
    void testLastAfterModification() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20));
        assertEquals(20, set.last());

        set.add(50);
        assertEquals(50, set.last());

        set.remove(50);
        assertEquals(20, set.last());
    }

    @Test
    @DisplayName("iterator traverses elements in ascending order")
    void testIteratorInOrderTraversal() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30, 5, 15));
        Iterator<Integer> it = set.iterator();

        List<Integer> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }

        assertEquals(List.of(5, 10, 15, 20, 30), result);
    }

    @Test
    @DisplayName("iterator hasNext returns false and next throws exception on empty set")
    void testIteratorEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();
        Iterator<Integer> it = set.iterator();

        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    @DisplayName("iterator next throws NoSuchElementException after exhausting elements")
    void testIteratorExhaustion() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10));
        Iterator<Integer> it = set.iterator();

        assertEquals(10, it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    @DisplayName("iterator remove correctly deletes the last returned element")
    void testIteratorRemove() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        Iterator<Integer> it = set.iterator();

        assertEquals(10, it.next());
        assertEquals(20, it.next());
        it.remove();

        assertEquals(2, set.size());
        assertFalse(set.contains(20));
        assertTrue(it.hasNext());
        assertEquals(30, it.next());
    }

    @Test
    @DisplayName("iterator remove throws IllegalStateException when called twice or before next")
    void testIteratorRemoveIllegalState() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20));
        Iterator<Integer> it = set.iterator();

        assertThrows(IllegalStateException.class, it::remove);

        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    @DisplayName("iterator remove works correctly when deleting a node with two children")
    void testIteratorRemoveNodeWithTwoChildren() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30, 25, 35));
        Iterator<Integer> it = set.iterator();

        while (it.hasNext()) {
            if (it.next() == 30) {
                it.remove();
            }
        }

        assertEquals(4, set.size());
        assertFalse(set.contains(30));
        assertTrue(set.contains(25));
        assertTrue(set.contains(35));
    }

    @Test
    @DisplayName("iterator respects custom comparator ordering")
    void testIteratorWithCustomComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(Comparator.reverseOrder());
        set.addAll(List.of(20, 10, 30));

        Iterator<Integer> it = set.iterator();
        List<Integer> result = new ArrayList<>();
        it.forEachRemaining(result::add);

        assertEquals(List.of(30, 20, 10), result);
    }

    @Test
    @DisplayName("descendingIterator traverses elements in reverse sorted order")
    void testDescendingIteratorOrder() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(20, 10, 30, 5, 15));
        Iterator<Integer> it = set.descendingIterator();

        List<Integer> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }

        assertEquals(List.of(30, 20, 15, 10, 5), result);
    }

    @Test
    @DisplayName("descendingIterator hasNext returns false and next throws exception on empty set")
    void testDescendingIteratorEmptySet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>();
        Iterator<Integer> it = set.descendingIterator();

        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    @DisplayName("descendingIterator remove correctly deletes the last returned element")
    void testDescendingIteratorRemove() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        Iterator<Integer> it = set.descendingIterator();

        assertEquals(30, it.next());
        assertEquals(20, it.next());
        it.remove();

        assertEquals(2, set.size());
        assertFalse(set.contains(20));
        assertTrue(it.hasNext());
        assertEquals(10, it.next());
    }

    @Test
    @DisplayName("descendingIterator remove throws IllegalStateException when called twice or before next")
    void testDescendingIteratorRemoveIllegalState() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20));
        Iterator<Integer> it = set.descendingIterator();

        assertThrows(IllegalStateException.class, it::remove);

        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    @DisplayName("descendingSet inverts navigation method behavior")
    void testDescendingSetNavigation() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        NavigableSet<Integer> descendingSet = set.descendingSet();

        assertEquals(30, descendingSet.first());
        assertEquals(10, descendingSet.last());

        assertEquals(10, descendingSet.higher(20));
        assertEquals(30, descendingSet.lower(20));
        assertEquals(20, descendingSet.ceiling(20));
        assertEquals(20, descendingSet.floor(20));
    }

    @Test
    @DisplayName("descendingSet reflects changes in the backing set and vice versa")
    void testDescendingSetMutations() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        NavigableSet<Integer> descendingSet = set.descendingSet();

        set.add(40);
        assertEquals(40, descendingSet.first());

        assertTrue(descendingSet.remove(20));
        assertFalse(set.contains(20));
        assertEquals(3, set.size());
    }

    @Test
    @DisplayName("descendingSet pollFirst and pollLast reflect inverted order")
    void testDescendingSetPolling() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        NavigableSet<Integer> descendingSet = set.descendingSet();

        assertEquals(30, descendingSet.pollFirst());
        assertFalse(set.contains(30));

        assertEquals(10, descendingSet.pollLast());
        assertFalse(set.contains(10));

        assertEquals(1, set.size());
        assertTrue(set.contains(20));
    }

    @Test
    @DisplayName("descendingSet of a descendingSet returns the original set view")
    void testDoubleDescendingSet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        NavigableSet<Integer> doubleDescending = set.descendingSet().descendingSet();

        assertEquals(10, doubleDescending.first());
        assertEquals(30, doubleDescending.last());
        assertSame(set, doubleDescending);
    }

    @Test
    @DisplayName("descendingSet comparator returns reverse ordering")
    void testDescendingSetComparator() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));
        Comparator<? super Integer> comp = set.descendingSet().comparator();

        assertNotNull(comp);
        assertTrue(comp.compare(30, 10) < 0);
    }

    @Test
    @DisplayName("subSet with boolean bounds filters elements correctly")
    void testSubSetWithInclusiveFlags() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40, 50));
        NavigableSet<Integer> sub = set.subSet(20, true, 40, false);

        assertEquals(2, sub.size());
        assertTrue(sub.contains(20));
        assertTrue(sub.contains(30));
        assertFalse(sub.contains(40));
    }

    @Test
    @DisplayName("subSet range views reflect mutations in the backing set")
    void testSubSetMutationReflectsInBackingSet() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40, 50));
        NavigableSet<Integer> sub = set.subSet(20, true, 40, true);

        sub.add(25);
        assertTrue(set.contains(25));

        sub.remove(30);
        assertFalse(set.contains(30));
    }

    @Test
    @DisplayName("subSet throws IllegalArgumentException when adding element outside range bounds")
    void testSubSetAddOutOfBoundsThrowsException() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40, 50));
        NavigableSet<Integer> sub = set.subSet(20, true, 40, true);

        assertThrows(IllegalArgumentException.class, () -> sub.add(5));
        assertThrows(IllegalArgumentException.class, () -> sub.add(50));
    }

    @Test
    @DisplayName("subSet throws IllegalArgumentException when fromElement is greater than toElement")
    void testSubSetInvalidRangeThrowsException() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30));

        assertThrows(IllegalArgumentException.class, () -> set.subSet(30, true, 10, true));
    }

    @Test
    @DisplayName("headSet returns elements strictly less than toElement by default")
    void testHeadSetDefaultExclusive() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));
        SortedSet<Integer> head = set.headSet(30);

        assertEquals(2, head.size());
        assertTrue(head.contains(10));
        assertTrue(head.contains(20));
        assertFalse(head.contains(30));
    }

    @Test
    @DisplayName("headSet with inclusive flag true includes the boundary element")
    void testHeadSetInclusive() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));
        NavigableSet<Integer> head = set.headSet(30, true);

        assertEquals(3, head.size());
        assertTrue(head.contains(30));
    }

    @Test
    @DisplayName("tailSet returns elements greater than or equal to fromElement by default")
    void testTailSetDefaultInclusive() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));
        SortedSet<Integer> tail = set.tailSet(20);

        assertEquals(3, tail.size());
        assertTrue(tail.contains(20));
        assertTrue(tail.contains(30));
        assertTrue(tail.contains(40));
        assertFalse(tail.contains(10));
    }

    @Test
    @DisplayName("tailSet with inclusive flag false excludes the starting element")
    void testTailSetExclusive() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));
        NavigableSet<Integer> tail = set.tailSet(20, false);

        assertEquals(2, tail.size());
        assertFalse(tail.contains(20));
        assertTrue(tail.contains(30));
    }

    @Test
    @DisplayName("containsAll returns true only when set contains all target elements")
    void testContainsAll() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));

        assertTrue(set.containsAll(List.of(10, 30)));
        assertFalse(set.containsAll(List.of(10, 50)));
    }

    @Test
    @DisplayName("removeAll removes specified elements and returns true if set was modified")
    void testRemoveAll() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));

        assertTrue(set.removeAll(List.of(20, 40, 50)));
        assertEquals(2, set.size());
        assertFalse(set.contains(20));
        assertFalse(set.contains(40));
    }

    @Test
    @DisplayName("retainAll retains only matching elements and removes all others")
    void testRetainAll() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(10, 20, 30, 40));

        boolean modified = set.retainAll(List.of(20, 40, 50));

        assertTrue(modified);
        assertEquals(2, set.size());
        assertTrue(set.contains(20));
        assertTrue(set.contains(40));
        assertFalse(set.contains(10));
    }

    @Test
    @DisplayName("toArray converts set elements into an ordered Object array")
    void testToArrayObject() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(30, 10, 20));
        Object[] array = set.toArray();

        assertArrayEquals(new Object[]{10, 20, 30}, array);
    }

    @Test
    @DisplayName("toArray typed creates/populates target array in sorted order")
    void testToArrayTyped() {
        CustomTreeSet<Integer> set = new CustomTreeSet<>(List.of(30, 10, 20));
        Integer[] array = set.toArray(new Integer[0]);

        assertArrayEquals(new Integer[]{10, 20, 30}, array);
    }
}