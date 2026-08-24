package customtreeset;

import java.util.AbstractSet;
import java.util.Collection;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import static java.util.Collections.reverseOrder;
import static java.util.Objects.requireNonNull;

/**
 * A custom implementation of {@link NavigableSet} backed by a self-balancing
 * red-black binary search tree, providing behavior comparable to
 * {@link java.util.TreeSet}.
 *
 * <p>Elements are ordered according to their {@linkplain Comparable natural
 * ordering}, or by a {@link Comparator} supplied at set creation time,
 * depending on which constructor is used. This ordering is used for all
 * element-based navigation operations, such as {@link #first()},
 * {@link #last()}, {@link #ceiling(Object)}, {@link #floor(Object)}, and
 * range views such as {@link #subSet(Object, boolean, Object, boolean)}.
 *
 * <p>The tree maintains the standard red-black invariants (root is black, red
 * nodes have only black children, and every path from a node to its
 * descendant leaves contains the same number of black nodes) so that
 * {@code add}, {@code remove}, and {@code contains} all run in
 * {@code O(log n)} time.
 *
 * <p>This implementation is not synchronized. If multiple threads access a
 * set concurrently, and at least one of the threads modifies the set
 * structurally, it must be synchronized externally.
 *
 * @param <E> the type of elements maintained by this set
 */
public class CustomTreeSet<E> extends AbstractSet<E> implements NavigableSet<E> {

    /**
     * Marker used for the color of a red node.
     */
    private static final boolean RED = true;

    /**
     * Marker used for the color of a black node. Also, the color of a {@code null} (absent) node.
     */
    private static final boolean BLACK = false;

    /**
     * The root node of the tree, or {@code null} if the set is empty.
     */
    private Node<E> root;

    /**
     * The number of elements currently in the set.
     */
    private int size = 0;

    /**
     * The comparator used to order elements, or {@code null} if natural ordering is used.
     */
    private final Comparator<? super E> comparator;

    /**
     * Constructs a new, empty tree set that orders its elements according to
     * their {@linkplain Comparable natural ordering}.
     */
    public CustomTreeSet() {
        this.comparator = null;
    }

    /**
     * Constructs a new, empty tree set that orders its elements according to
     * the given comparator.
     *
     * @param comparator the comparator used to order elements, or {@code null}
     *                   to use the natural ordering of the elements
     */
    public CustomTreeSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Constructs a new tree set containing the elements in the specified
     * collection, ordered according to the natural ordering of its elements.
     *
     * @param c the collection whose elements are to be placed in this set
     * @throws NullPointerException if {@code c} is {@code null}, or if any
     *                               element in {@code c} is {@code null}
     * @throws ClassCastException   if the elements in {@code c} are not mutually comparable
     */
    public CustomTreeSet(Collection<? extends E> c) {
        this.comparator = null;
        addAll(c);
    }

    /**
     * Constructs a new tree set containing the same elements and using the
     * same ordering as the given sorted set.
     *
     * @param s the sorted set whose elements and comparator are to be used
     * @throws NullPointerException if {@code s} is {@code null}
     */
    public CustomTreeSet(SortedSet<E> s) {
        this.comparator = s.comparator();
        addAll(s);
    }

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * <p>If the element is new, a new red node is inserted at the
     * appropriate leaf position via standard binary-search-tree insertion,
     * and {@link #fixAfterInsertion(Node)} is called to restore the
     * red-black invariants.
     *
     * @param e the element to add
     * @return {@code true} if this set did not already contain the specified element
     * @throws ClassCastException if {@code e} cannot be compared with the elements already in this set
     */
    public boolean add(E e) {
        if(root == null) {
            compare(e, e);
            root = new Node<>(e, null);
            size = 1;
            return true;
        }
        Node<E> t = root;
        Node<E> parent;
        int cmp;
        do {
            parent = t;
            cmp = compare(e, parent.value);
            if(cmp < 0)
                t = t.left;
            else if (cmp > 0)
                t = t.right;
            else
                return false;
        } while(t != null);
        Node<E> node = new Node<>(e, parent);
        node.color = RED;
        if(cmp < 0)
            parent.left = node;
        else
            parent.right = node;
        fixAfterInsertion(node);
        size++;
        return true;
    }

    /**
     * Returns the least element in this set greater than or equal to the
     * given element, or {@code null} if there is no such element.
     *
     * @param item the value to match
     * @return the least element greater than or equal to {@code item},
     *         or {@code null} if no such element exists
     * @throws NullPointerException if {@code item} is {@code null}
     */
    public E ceiling(E item) {
        requireNonNull(item);
        Node<E> p = root;
        Node<E> candidate = null;
        while (p != null) {
            int cmp = compare(item, p.value);
            if (cmp < 0) {
                candidate = p;
                p = p.left;
            } else if (cmp == 0)
                return p.value;
            else
                p = p.right;
        }
        return candidate == null ? null : candidate.value;
    }

    /**
     * Removes all elements from this set. The set will be empty after this
     * call returns.
     */
    public void clear() {
        size = 0;
        root = null;
    }

    /**
     * Returns the comparator used to order the elements in this set, or
     * {@code null} if this set uses the {@linkplain Comparable natural
     * ordering} of its elements.
     *
     * @return the comparator used to order this set, or {@code null} if
     *         natural ordering is used
     */
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /**
     * Returns {@code true} if this set contains the specified element.
     *
     * @param o the object whose presence in this set is to be tested
     * @return {@code true} if this set contains the specified element
     * @throws NullPointerException if {@code o} is {@code null}
     * @throws ClassCastException   if {@code o} cannot be compared with the elements in this set
     */
    public boolean contains(Object o) {
        requireNonNull(o);
        Node<E> p = root;
        while (p != null) {
            int cmp = compare(o, p.value);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return true;
        }
        return false;
    }

    /**
     * Returns an iterator over the elements in this set, in descending
     * order. Iteration is driven by repeated calls to
     * {@link #predecessor(Node)}, starting from {@link #getLastNode()}.
     *
     * @return an iterator over the elements in this set, in descending order
     */
    public Iterator<E> descendingIterator() {
        return new Iterator<>() {
            private Node<E> next = getLastNode();
            private Node<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (next == null)
                    throw new NoSuchElementException();
                lastReturned = next;
                next = predecessor(next);
                return lastReturned.value;
            }

            @Override
            public void remove() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                deleteNode(lastReturned);
                lastReturned = null;
            }
        };
    }

    /**
     * Returns a reverse-order view of the elements contained in this set.
     * The returned set is backed by this set, so changes to one are
     * reflected in the other.
     *
     * @return a reverse-order view of this set
     */
    public NavigableSet<E> descendingSet() {
        return new DescendingSetView<>(this);
    }

    /**
     * Returns the least element currently in this set.
     *
     * @return the least element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    public E first() {
        if(size == 0)
            throw new NoSuchElementException();
        Node<E> p = getFirstNode();
        return p == null ? null : p.value;
    }

    /**
     * Returns the greatest element in this set less than or equal to the
     * given element, or {@code null} if there is no such element.
     *
     * @param item the value to match
     * @return the greatest element less than or equal to {@code item},
     *         or {@code null} if no such element exists
     * @throws NullPointerException if {@code item} is {@code null}
     */
    public E floor(E item) {
        requireNonNull(item);
        Node<E> p = root;
        Node<E> candidate = null;
        while (p != null) {
            int cmp = compare(item, p.value);
            if (cmp > 0) {
                candidate = p;
                p = p.right;
            } else if (cmp < 0)
                p = p.left;
            else
                return p.value;
        }
        return candidate == null ? null : candidate.value;
    }

    /**
     * Returns a view of the portion of this set whose elements are strictly
     * less than {@code toElement}.
     *
     * @param toElement the exclusive upper bound of the returned set
     * @return a view of the portion of this set whose elements are strictly
     *         less than {@code toElement}
     * @throws NullPointerException if {@code toElement} is {@code null}
     */
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    /**
     * Returns a view of the portion of this set whose elements are less
     * than (or equal to, if {@code inclusive} is {@code true})
     * {@code toElement}. The returned set is backed by this set.
     *
     * @param toElement the upper bound of the returned set
     * @param inclusive {@code true} if {@code toElement} is to be included in the returned view
     * @return a view of the portion of this set whose elements are less
     *         than (or equal to) {@code toElement}
     * @throws NullPointerException if {@code toElement} is {@code null}
     */
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new SubSetView<>(this, false, null, false, true, toElement, inclusive);
    }

    /**
     * Returns the least element in this set strictly greater than the given
     * element, or {@code null} if there is no such element.
     *
     * @param item the value to match
     * @return the least element strictly greater than {@code item},
     *         or {@code null} if no such element exists
     * @throws NullPointerException if {@code item} is {@code null}
     */
    public E higher(E item) {
        requireNonNull(item);
        Node<E> p = root;
        Node<E> candidate = null;
        while (p != null)
            if (compare(item, p.value) < 0) {
                candidate = p;
                p = p.left;
            } else
                p = p.right;
        return candidate == null ? null : candidate.value;
    }

    /**
     * Returns {@code true} if this set contains no elements.
     *
     * @return {@code true} if this set contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an iterator over the elements in this set, in ascending order.
     * Iteration is driven by repeated calls to {@link #successor(Node)},
     * starting from {@link #getFirstNode()}.
     *
     * @return an iterator over the elements in this set, in ascending order
     */
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private Node<E> next = getFirstNode();
            private Node<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (next == null)
                    throw new NoSuchElementException();
                lastReturned = next;
                next = successor(next);
                return lastReturned.value;
            }

            @Override
            public void remove() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                if (lastReturned.left != null && lastReturned.right != null)
                    next = lastReturned;
                deleteNode(lastReturned);
                lastReturned = null;
            }
        };
    }

    /**
     * Returns the greatest element currently in this set.
     *
     * @return the greatest element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    public E last() {
        if (size == 0)
            throw new NoSuchElementException();
        Node<E> p = getLastNode();
        return p == null ? null : p.value;
    }

    /**
     * Returns the greatest element in this set strictly less than the given
     * element, or {@code null} if there is no such element.
     *
     * @param item the value to match
     * @return the greatest element strictly less than {@code item},
     *         or {@code null} if no such element exists
     * @throws NullPointerException if {@code item} is {@code null}
     */

    public E lower(E item) {
        requireNonNull(item);
        Node<E> p = root;
        Node<E> candidate = null;
        while (p != null)
            if (compare(item, p.value) > 0) {
                candidate = p;
                p = p.right;
            } else
                p = p.left;
        return candidate == null ? null : candidate.value;
    }

    /**
     * Removes and returns the least element in this set, or returns
     * {@code null} if the set is empty.
     *
     * @return the removed first element, or {@code null} if this set is empty
     */
    public E pollFirst() {
        Node<E> p = getFirstNode();
        if(p == null)
            return null;
        E result = p.value;
        deleteNode(p);
        return result;
    }

    /**
     * Removes and returns the greatest element in this set, or returns
     * {@code null} if the set is empty.
     *
     * @return the removed last element, or {@code null} if this set is empty
     */
    public E pollLast() {
        Node<E> p = getLastNode();
        if (p == null)
            return null;
        E result = p.value;
        deleteNode(p);
        return result;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o the object to be removed from this set
     * @return {@code true} if this set contained the specified element
     * @throws NullPointerException if {@code o} is {@code null}
     * @throws ClassCastException   if {@code o} cannot be compared with the elements in this set
     */
    public boolean remove(Object o) {
        requireNonNull(o);
        Node<E> p = root;
        while (p != null) {
            int cmp = compare(o, p.value);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else {
                deleteNode(p);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of elements in this set
     */
    public int size() {
        return size;
    }

    /**
     * Returns a view of the portion of this set whose elements range from
     * {@code fromElement}, inclusive, to {@code toElement}, exclusive.
     *
     * @param fromElement the inclusive lower bound of the returned set
     * @param toElement   the exclusive upper bound of the returned set
     * @return a view of the specified range of this set
     * @throws NullPointerException     if {@code fromElement} or {@code toElement} is {@code null}
     * @throws IllegalArgumentException if {@code fromElement} is greater than {@code toElement}
     */
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    /**
     * Returns a view of the portion of this set whose elements range from
     * {@code fromElement} to {@code toElement}, with inclusivity of each
     * bound controlled by the corresponding boolean argument. The returned
     * set is backed by this set.
     *
     * @param fromElement   the lower bound of the returned set
     * @param fromInclusive {@code true} if {@code fromElement} is to be included in the returned view
     * @param toElement     the upper bound of the returned set
     * @param toInclusive   {@code true} if {@code toElement} is to be included in the returned view
     * @return a view of the specified range of this set
     * @throws NullPointerException     if {@code fromElement} or {@code toElement} is {@code null}
     * @throws IllegalArgumentException if {@code fromElement} is greater than {@code toElement}
     *                                   (or equal to it while either bound is exclusive)
     */
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new SubSetView<>(this, true, fromElement, fromInclusive, true, toElement, toInclusive);
    }

    /**
     * Returns a view of the portion of this set whose elements are greater
     * than or equal to {@code fromElement}.
     *
     * @param fromElement the inclusive lower bound of the returned set
     * @return a view of the portion of this set whose elements are greater
     *         than or equal to {@code fromElement}
     * @throws NullPointerException if {@code fromElement} is {@code null}
     */
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    /**
     * Returns a view of the portion of this set whose elements are greater
     * than (or equal to, if {@code inclusive} is {@code true})
     * {@code fromElement}. The returned set is backed by this set.
     *
     * @param fromElement the lower bound of the returned set
     * @param inclusive   {@code true} if {@code fromElement} is to be included in the returned view
     * @return a view of the portion of this set whose elements are greater
     *         than (or equal to) {@code fromElement}
     * @throws NullPointerException if {@code fromElement} is {@code null}
     */
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new SubSetView<>(this, true, fromElement, inclusive, false, null, false);
    }

    /**
     * Returns the color of the given node, treating a {@code null} node
     * (an external/leaf position) as {@link #BLACK}, per red-black tree convention.
     *
     * @param p the node whose color is queried, may be {@code null}
     * @return {@link #RED} or {@link #BLACK}
     */
    private boolean colorOf(Node<E> p) {
        return p == null ? BLACK : p.color;
    }

    /**
     * Compares two elements using this set's comparator, or their natural
     * ordering if no comparator was supplied.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @return a negative integer, zero, or a positive integer as {@code e1} is
     *         less than, equal to, or greater than {@code e2}
     */
    private int compare(final Object e1, final Object e2) {
        if (comparator != null)
            return comparator.compare((E) e1, (E) e2);
        return ((Comparable<? super E>) e1).compareTo((E) e2);
    }

    /**
     * Removes the given node from the tree, rewiring child/parent links and
     * balancing the tree afterwards if a black node was removed.
     *
     * <p>If the node to delete has two children, its position is instead
     * relabeled with its in-order {@link #successor(Node)}'s value, and the
     * successor node (which has at most one child) is physically unlinked
     * in its place.
     *
     * @param p the node to remove; must not be {@code null}
     */
    private void deleteNode(Node<E> p) {
        size--;
        if(p.left != null && p.right != null) {
            Node<E> s = successor(p);
            p.value = s.value;
            p = s;
        }

        Node<E> replacement = p.left != null ? p.left : p.right;
        if(replacement != null) {
            replacement.parent = p.parent;
            if(p.parent == null)
                root = replacement;
            else if(p == p.parent.left)
                p.parent.left = replacement;
            else
                p.parent.right = replacement;
            p.left = p.right = p.parent = null;
            if(p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if(p.parent == null)
            root = null;
        else {
            if(p.color == BLACK)
                fixAfterDeletion(p);
            if(p.parent != null) {
                if(p == p.parent.left)
                    p.parent.left = null;
                else if(p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    /**
     * Restores the red-black tree invariants after a node has been physically
     * removed from the tree, in the case where the removed node (or its
     * replacement) was black. {@code x} is the node that now occupies the
     * position of the removed node (possibly a "phantom" double-black node),
     * and rotations/recoloring are applied up the tree until the invariants
     * are satisfied.
     *
     * @param x the node (or replacement) at which to begin balancing
     */
    private void fixAfterDeletion(Node<E> x) {
        while (x != root && colorOf(x) == BLACK)
            if (x == leftOf(parentOf(x))) {
                Node<E> sib = rightOf(parentOf(x));
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }
                if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else {
                Node<E> sib = leftOf(parentOf(x));
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }
                if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        setColor(x, BLACK);
    }

    /**
     * Restores the red-black tree invariants after a new red node {@code x}
     * has been inserted, by recoloring and rotating up the tree to resolve
     * any red-red parent/child violations, and finally ensuring the root is black.
     *
     * @param x the newly inserted node
     */
    private void fixAfterInsertion(Node<E> x) {
        x.color = RED;
        while(x != null && x != root && x.parent.color == RED)
            if(parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Node<E> y = rightOf(parentOf(parentOf(x)));
                if(colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if(x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));

                }
            } else {
                Node<E> y = leftOf(parentOf(parentOf(x)));
                if(colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if(x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        root.color = BLACK;
    }

    /**
     * Returns the node holding the least element in the tree, found by
     * following left-child links from the root.
     *
     * @return the leftmost node in the tree, or {@code null} if the tree is empty
     */
    private Node<E> getFirstNode() {
        Node<E> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    /**
     * Returns the node holding the greatest element in the tree, found by
     * following right-child links from the root.
     *
     * @return the rightmost node in the tree, or {@code null} if the tree is empty
     */
    private Node<E> getLastNode() {
        Node<E> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    /**
     * Returns the left child of the given node, treating a {@code null}
     * node as having no children.
     *
     * @param p the node whose left child is queried, may be {@code null}
     * @return {@code p.left}, or {@code null} if {@code p} is {@code null}
     */
    private Node<E> leftOf(Node<E> p) {
        return p == null ? null : p.left;
    }

    /**
     * Returns the right child of the given node, treating a {@code null}
     * node as having no children.
     *
     * @param p the node whose right child is queried, may be {@code null}
     * @return {@code p.right}, or {@code null} if {@code p} is {@code null}
     */

    private Node<E> rightOf(Node<E> p) {
        return p == null ? null : p.right;
    }

    /**
     * Returns the parent of the given node, treating a {@code null} node as
     * having no parent.
     *
     * @param p the node whose parent is queried, may be {@code null}
     * @return {@code p.parent}, or {@code null} if {@code p} is {@code null}
     */
    private Node<E> parentOf(Node<E> p) {
        return p == null ? null : p.parent;
    }

    /**
     * Returns the node whose element immediately precedes the given node's
     * element in this set's ordering (the in-order predecessor).
     *
     * @param t the node whose predecessor is sought, may be {@code null}
     * @return the in-order predecessor of {@code t}, or {@code null} if
     *         {@code t} is {@code null} or has no predecessor
     */
    private Node<E> predecessor(final Node<E> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Node<E> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Node<E> p = t.parent;
            Node<E> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Performs a standard left tree rotation around the given node, promoting
     * its right child into its position while preserving in-order element
     * ordering. Does nothing if {@code p} is {@code null}.
     *
     * @param p the node to rotate around
     */
    private void rotateLeft(Node<E> p) {
        if(p != null) {
            Node<E> r = p.right;
            p.right = r.left;
            if(r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if(p.parent == null)
                root = r;
            else if(p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
    }

    /**
     * Performs a standard right tree rotation around the given node,
     * promoting its left child into its position while preserving in-order
     * element ordering. Does nothing if {@code p} is {@code null}.
     *
     * @param p the node to rotate around
     */
    private void rotateRight(Node<E> p) {
        if(p != null) {
            Node<E> l = p.left;
            p.left = l.right;
            if (l.right != null)
                l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null)
                root = l;
            else if (p.parent.right == p)
                p.parent.right = l;
            else
                p.parent.left = l;
            l.right = p;
            p.parent = l;
        }
    }

    /**
     * Sets the color of the given node, doing nothing if the node is
     * {@code null} (since {@code null} nodes are conventionally black and
     * cannot be recolored).
     *
     * @param p     the node to recolor, may be {@code null}
     * @param color the new color, {@link #RED} or {@link #BLACK}
     */
    private void setColor(Node<E> p, boolean color) {
        if(p != null)
            p.color = color;
    }

    /**
     * Returns the node whose element immediately follows the given node's
     * element in this set's ordering (the in-order successor).
     *
     * @param t the node whose successor is sought, may be {@code null}
     * @return the in-order successor of {@code t}, or {@code null} if
     *         {@code t} is {@code null} or has no successor
     */
    private Node<E> successor(Node<E> t) {
        if(t == null)
            return null;
        else if(t.right != null) {
            Node<E> p = t.right;
            while(p.left != null)
                p = p.left;
            return p;
        } else {
            Node<E> p = t.parent;
            Node<E> ch = t;
            while(p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * A {@link NavigableSet} view that iterates and compares elements in the
     * reverse order of the backing set. This is the view returned by
     * {@link CustomTreeSet#descendingSet()} and by
     * {@link SubSetView#descendingSet()}.
     *
     * <p>Every operation is delegated to the corresponding operation on
     * {@link #set}, swapping the direction-dependent counterparts (e.g.
     * {@code lower} delegates to {@code higher}), so the view stays live:
     * modifications made through it, or directly on the backing set, are
     * visible through both.
     *
     * @param <E> the type of elements held by this view (and by the backing set)
     */
    private static class DescendingSetView<E> extends AbstractSet<E> implements NavigableSet<E> {

        /**
         * The set whose elements this view presents in reverse order.
         */
        private final NavigableSet<E> set;

        /**
         * Constructs a descending view backed by the given set.
         *
         * @param set the set to present in reverse order
         */
        DescendingSetView(NavigableSet<E> set) {
            this.set = set;
        }

        /**
         * {@inheritDoc} In reverse order, "lower" corresponds to the backing set's {@code higher}.
         */
        public E lower(E e) {
            return set.higher(e);
        }

        /**
         * {@inheritDoc} In reverse order, "floor" corresponds to the backing set's {@code ceiling}.
         */
        public E floor(E e) {
            return set.ceiling(e);
        }

        /**
         * {@inheritDoc} In reverse order, "ceiling" corresponds to the backing set's {@code floor}.
         */
        public E ceiling(E e) {
            return set.floor(e);
        }

        /**
         * {@inheritDoc} In reverse order, "higher" corresponds to the backing set's {@code lower}.
         */

        public E higher(E e) {
            return set.lower(e);
        }

        /**
         * {@inheritDoc}
         */
        public E first() {
            return set.last();
        }

        /**
         * {@inheritDoc}
         */
        public E last() {
            return set.first();
        }

        /**
         * {@inheritDoc}
         */
        public E pollFirst() {
            return set.pollLast();
        }

        /**
         * {@inheritDoc}
         */
        public E pollLast() {
            return set.pollFirst();
        }

        /**
         * {@inheritDoc} Delegates to the backing set's descending iterator, so
         * ascending iteration of this view is descending iteration of {@link #set}.
         */
        public Iterator<E> iterator() {
            return set.descendingIterator();
        }

        /**
         * {@inheritDoc} Delegates to the backing set's ascending iterator, so
         * descending iteration of this view is ascending iteration of {@link #set}.
         */
        public Iterator<E> descendingIterator() {
            return set.iterator();
        }

        /**
         * {@inheritDoc}
         *
         * @return the backing set, which is itself the ascending-order view of this view
         */
        public NavigableSet<E> descendingSet() {
            return set;
        }

        /**
         * {@inheritDoc}
         */
        public boolean contains(Object o) {
            return set.contains(o);
        }

        /**
         * {@inheritDoc}
         */
        public boolean remove(Object o) {
            return set.remove(o);
        }

        /**
         * {@inheritDoc}
         */
        public void clear() {
            set.clear();
        }

        /**
         * {@inheritDoc}
         */
        public int size() {
            return set.size();
        }

        /**
         * {@inheritDoc}
         *
         * @return the backing set's comparator reversed, or the reverse
         *         natural ordering if the backing set uses natural ordering
         */
        public Comparator<? super E> comparator() {
            return reverseOrder(set.comparator());
        }

        /**
         * {@inheritDoc} Since iteration is reversed, the range is obtained by
         * requesting the mirrored bounds from the backing set and reversing
         * the resulting view.
         */
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return set.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
        }

        /**
         * {@inheritDoc} Since iteration is reversed, a "head" (the largest
         * elements) is implemented as the backing set's tail, reversed.
         */
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return set.tailSet(toElement, inclusive).descendingSet();
        }

        /**
         * {@inheritDoc} Since iteration is reversed, a "tail" (the smallest
         * elements) is implemented as the backing set's head, reversed.
         */
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return set.headSet(fromElement, inclusive).descendingSet();
        }

        /**
         * {@inheritDoc}
         */
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        /**
         * {@inheritDoc}
         */
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        /**
         * {@inheritDoc}
         */
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }
    }

    /**
     * An internal, mutable red-black tree node holding a single element of
     * the enclosing {@link CustomTreeSet}. A {@code Node} is directly wired
     * into the tree structure via its {@link #left}, {@link #right}, and
     * {@link #parent} links and should never be exposed outside the
     * enclosing {@link CustomTreeSet}.
     *
     * @param <E> the type of the element held by this node
     */
    private static class Node<E>  {
        /**
         * This node's element.
         */
        E value;

        /**
         * This node's left child, or {@code null} if none.
         */
        Node<E> left;

        /**
         * This node's right child, or {@code null} if none.
         */
        Node<E> right;

        /**
         * This node's parent, or {@code null} if this is the root.
         */
        Node<E> parent;

        /**
         * This node's color; defaults to {@link CustomTreeSet#BLACK} until set otherwise (e.g. on insertion).
         */
        boolean color = BLACK;

        /**
         * Constructs a new node with the given element and parent. Its color
         * defaults to {@link CustomTreeSet#BLACK} and it has no children.
         *
         * @param value  this node's element
         * @param parent this node's parent, or {@code null} if it will be the root
         */
        Node(final E value, final Node<E> parent) {
            this.value = value;
            this.parent = parent;
        }

        /**
         * Returns a string representation of this node in the form
         * {@code value=<element>}.
         *
         * @return a string representation of this node
         */
        @Override
        public String toString() {
            return  "value=" + value;
        }
    }

    /**
     * A {@link NavigableSet} range view over a portion of a
     * {@link CustomTreeSet}, restricted to the {@code [fromElement,
     * toElement)}-style range described by the bound fields. This is the
     * view returned by {@link CustomTreeSet#subSet(Object, boolean, Object, boolean)},
     * {@link CustomTreeSet#headSet(Object, boolean)}, and
     * {@link CustomTreeSet#tailSet(Object, boolean)}.
     *
     * <p>Every operation is delegated to the backing {@link #set}, so the
     * view stays live: modifications made through it, or directly on the
     * backing set, are visible through both.
     *
     * @param <E> the type of elements maintained by the backing set
     */
    private static class SubSetView<E> extends AbstractSet<E> implements NavigableSet<E> {

        /**
         * The set that this view delegates to.
         */
        private final CustomTreeSet<E> set;

        /**
         * Whether this view has a lower bound at all.
         */
        private final boolean lowBounded;

        /**
         * The lower bound of this view, or {@code null} if {@link #lowBounded} is {@code false}.
         */
        private final E fromElement;

        /**
         * Whether {@link #fromElement} itself is included in this view.
         */
        private final boolean fromInclusive;

        /**
         * Whether this view has an upper bound at all.
         */
        private final boolean highBounded;

        /**
         * Whether {@link #toElement} itself is included in this view.
         */
        private final E toElement;

        /**
         * Whether {@link #toElement} itself is included in this view.
         */
        private final boolean toInclusive;

        /**
         * Constructs a view of {@code set} restricted to the range described
         * by the given bounds.
         *
         * @param set           the backing set
         * @param lowBounded    whether a lower bound is in effect
         * @param fromElement   the lower bound, ignored if {@code lowBounded} is {@code false}
         * @param fromInclusive whether the lower bound is inclusive
         * @param highBounded   whether an upper bound is in effect
         * @param toElement     the upper bound, ignored if {@code highBounded} is {@code false}
         * @param toInclusive   whether the upper bound is inclusive
         * @throws IllegalArgumentException if both bounds are in effect and
         *                                   {@code fromElement} is greater than {@code toElement}
         */
        SubSetView(CustomTreeSet<E> set,
                   boolean lowBounded, E fromElement, boolean fromInclusive,
                   boolean highBounded, E toElement, boolean toInclusive) {
            if (lowBounded && highBounded)
                if (set.compare(fromElement, toElement) > 0)
                    throw new IllegalArgumentException();
            this.set = set;
            this.lowBounded = lowBounded;
            this.fromElement = fromElement;
            this.fromInclusive = fromInclusive;
            this.highBounded = highBounded;
            this.toElement = toElement;
            this.toInclusive = toInclusive;
        }

        /**
         * {@inheritDoc}
         *
         * @throws IllegalArgumentException if {@code e} lies outside this view's range
         */
        public boolean add(E e) {
            if (!inRange(e))
                throw new IllegalArgumentException();
            return set.add(e);
        }

        /**
         * {@inheritDoc}
         */
        public E ceiling(E e) {
            if (tooLow(e))
                return firstOrNull();
            if (tooHigh(e))
                return null;
            E candidate = set.ceiling(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        /**
         * {@inheritDoc}
         */
        public Comparator<? super E> comparator() {
            return set.comparator();
        }

        /**
         * {@inheritDoc}
         */
        public boolean contains(Object o) {
            return inRange(o) && set.contains(o);
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<E> descendingIterator() {
            return descendingSet().iterator();
        }

        /**
         * {@inheritDoc}
         */
        public NavigableSet<E> descendingSet() {
            return new DescendingSetView<>(this);
        }

        /**
         * {@inheritDoc}
         *
         * @throws NoSuchElementException if this view is empty
         */
        public E first() {
            E nodeVal;
            if (!lowBounded)
                nodeVal = set.first();
            else if (fromInclusive)
                nodeVal = set.ceiling(fromElement);
            else
                nodeVal = set.higher(fromElement);
            if (nodeVal == null || tooHigh(nodeVal))
                throw new NoSuchElementException();
            return nodeVal;
        }

        /**
         * {@inheritDoc}
         */
        public E floor(E e) {
            if (tooHigh(e))
                return lastOrNull();
            if (tooLow(e))
                return null;
            E candidate = set.floor(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        /**
         * {@inheritDoc}
         */
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        /**
         * {@inheritDoc}
         *
         * @throws IllegalArgumentException if {@code toElement} lies outside this view's range
         */
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            if (tooLowBound(toElement, inclusive) || tooHighBound(toElement, inclusive))
                throw new IllegalArgumentException();
            return new SubSetView<>(set, lowBounded, fromElement, fromInclusive, true, toElement, inclusive);
        }

        /**
         * {@inheritDoc}
         */
        public E higher(E e) {
            if (tooLow(e))
                return firstOrNull();
            if (tooHigh(e))
                return null;
            E candidate = set.higher(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        /**
         * Returns an iterator over the elements in this view, in ascending
         * order, stepping via repeated calls to {@link #higher(Object)}.
         *
         * @return an iterator over the elements in this view
         */
        @Override
        public Iterator<E> iterator() {
            return new Iterator<>() {
                private E nextVal = firstOrNull();
                private E lastReturned = null;

                @Override
                public boolean hasNext() {
                    return nextVal != null;
                }

                @Override
                public E next() {
                    if (nextVal == null)
                        throw new NoSuchElementException();
                    lastReturned = nextVal;
                    nextVal = higher(nextVal);
                    return lastReturned;
                }

                @Override
                public void remove() {
                    if (lastReturned == null)
                        throw new IllegalStateException();
                    set.remove(lastReturned);
                    lastReturned = null;
                }
            };
        }

        /**
         * {@inheritDoc}
         *
         * @throws NoSuchElementException if this view is empty
         */
        public E last() {
            E nodeVal;
            if (!highBounded)
                nodeVal = set.last();
            else if (toInclusive)
                nodeVal = set.floor(toElement);
            else
                nodeVal = set.lower(toElement);
            if (nodeVal == null || tooLow(nodeVal))
                throw new NoSuchElementException();
            return nodeVal;
        }

        /**
         * {@inheritDoc}
         */
        public E lower(E e) {
            if (tooHigh(e))
                return lastOrNull();
            if (tooLow(e))
                return null;
            E candidate = set.lower(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        /**
         * {@inheritDoc}
         */
        public E pollFirst() {
            E e = firstOrNull();
            if (e != null)
                set.remove(e);
            return e;
        }

        /**
         * {@inheritDoc}
         */
        public E pollLast() {
            E e = lastOrNull();
            if (e != null)
                set.remove(e);
            return e;
        }

        /**
         * {@inheritDoc}
         */
        public boolean remove(Object o) {
            return inRange(o) && set.remove(o);
        }

        /**
         * {@inheritDoc} Computed by walking this view's iterator, since the
         * backing tree does not track counts per range.
         */
        public int size() {
            int count = 0;
            for (E ignored : this)
                count++;
            return count;
        }

        /**
         * {@inheritDoc}
         */
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        /**
         * {@inheritDoc}
         *
         * @throws IllegalArgumentException if the bounds lie outside this view's range,
         *                                   or {@code fromElement} is greater than {@code toElement}
         */
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            if (tooLowBound(fromElement, fromInclusive) || tooHighBound(fromElement, fromInclusive) ||
                    tooLowBound(toElement, toInclusive) || tooHighBound(toElement, toInclusive))
                throw new IllegalArgumentException();
            return new SubSetView<>(set, true, fromElement, fromInclusive, true, toElement, toInclusive);
        }

        /**
         * {@inheritDoc}
         */
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        /**
         * {@inheritDoc}
         *
         * @throws IllegalArgumentException if {@code fromElement} lies outside this view's range
         */
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            if (tooLowBound(fromElement, inclusive) || tooHighBound(fromElement, inclusive))
                throw new IllegalArgumentException();
            return new SubSetView<>(set, true, fromElement, inclusive, highBounded, toElement, toInclusive);
        }

        /**
         * Returns this view's first element, or {@code null} if the view is empty.
         *
         * @return this view's first element, or {@code null} if empty
         */
        private E firstOrNull() {
            try {
                return first();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        /**
         * Returns whether the given object lies within this view's bounds,
         * treating both bounds as inclusive for this check.
         *
         * @param o the object to test
         * @return {@code true} if {@code o} is in range
         */
        private boolean inRange(Object o) {
            return !tooLow(o) && !tooHigh(o);
        }

        /**
         * Returns this view's last element, or {@code null} if the view is empty.
         *
         * @return this view's last element, or {@code null} if empty
         */
        private E lastOrNull() {
            try {
                return last();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        /**
         * Returns whether {@code o} falls above this view's upper bound
         * (strictly, or at the bound itself when the bound is exclusive).
         *
         * @param o the object to test
         * @return {@code true} if {@code o} is out of range on the high side
         */
        private boolean tooHigh(Object o) {
            if (!highBounded)
                return false;
            int cmp = set.compare(o, toElement);
            return cmp > 0 || (cmp == 0 && !toInclusive);
        }

        /**
         * Returns whether the given candidate bound would fall outside this
         * view's upper bound if used to construct a narrower range view,
         * accounting for the inclusivity of both bounds.
         *
         * @param element   the candidate bound to test
         * @param inclusive whether the candidate bound would be inclusive
         * @return {@code true} if {@code element} is out of range on the high side
         */
        private boolean tooHighBound(E element, boolean inclusive) {
            if (!highBounded)
                return false;
            int cmp = set.compare(element, toElement);
            if (cmp > 0)
                return true;
            return cmp == 0 && !toInclusive && inclusive;
        }

        /**
         * Returns whether {@code o} falls below this view's lower bound
         * (strictly, or at the bound itself when the bound is exclusive).
         *
         * @param o the object to test
         * @return {@code true} if {@code o} is out of range on the low side
         */
        private boolean tooLow(Object o) {
            if (!lowBounded)
                return false;
            int cmp = set.compare(o, fromElement);
            return cmp < 0 || (cmp == 0 && !fromInclusive);
        }

        /**
         * Returns whether the given candidate bound would fall outside this
         * view's lower bound if used to construct a narrower range view,
         * accounting for the inclusivity of both bounds.
         *
         * @param element   the candidate bound to test
         * @param inclusive whether the candidate bound would be inclusive
         * @return {@code true} if {@code element} is out of range on the low side
         */
        private boolean tooLowBound(E element, boolean inclusive) {
            if (!lowBounded)
                return false;
            int cmp = set.compare(element, fromElement);
            if (cmp < 0)
                return true;
            return cmp == 0 && !fromInclusive && inclusive;
        }
    }
}
