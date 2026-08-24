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

public class CustomTreeSet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node<E> root;

    private int size = 0;

    private final Comparator<? super E> comparator;

    public CustomTreeSet() {
        this.comparator = null;
    }

    public CustomTreeSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    public CustomTreeSet(Collection<? extends E> c) {
        this.comparator = null;
        addAll(c);
    }

    public CustomTreeSet(SortedSet<E> s) {
        this.comparator = s.comparator();
        addAll(s);
    }

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

    public void clear() {
        size = 0;
        root = null;
    }

    public Comparator<? super E> comparator() {
        return comparator;
    }

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

    @Override
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

    public NavigableSet<E> descendingSet() {
        return new DescendingSetView<>(this);
    }

    public E first() {
        if(size == 0)
            throw new NoSuchElementException();
        Node<E> p = getFirstNode();
        return p == null ? null : p.value;
    }

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

    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new SubSetView<>(this, false, null, false, true, toElement, inclusive);
    }

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

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
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

    public E last() {
        if (size == 0)
            throw new NoSuchElementException();
        Node<E> p = getLastNode();
        return p == null ? null : p.value;
    }

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

    public E pollFirst() {
        Node<E> p = getFirstNode();
        if(p == null)
            return null;
        E result = p.value;
        deleteNode(p);
        return result;
    }

    public E pollLast() {
        Node<E> p = getLastNode();
        if (p == null)
            return null;
        E result = p.value;
        deleteNode(p);
        return result;
    }

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

    public int size() {
        return size;
    }

    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new SubSetView<>(this, true, fromElement, fromInclusive, true, toElement, toInclusive);
    }

    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new SubSetView<>(this, true, fromElement, inclusive, false, null, false);
    }

    private boolean colorOf(Node<E> p) {
        return p == null ? BLACK : p.color;
    }

    private int compare(final Object e1, final Object e2) {
        if (comparator != null)
            return comparator.compare((E) e1, (E) e2);
        return ((Comparable<? super E>) e1).compareTo((E) e2);
    }

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

    private Node<E> getFirstNode() {
        Node<E> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    private Node<E> getLastNode() {
        Node<E> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    private Node<E> leftOf(Node<E> p) {
        return p == null ? null : p.left;
    }

    private Node<E> rightOf(Node<E> p) {
        return p == null ? null : p.right;
    }

    private Node<E> parentOf(Node<E> p) {
        return p == null ? null : p.parent;
    }

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

    private void setColor(Node<E> p, boolean color) {
        if(p != null)
            p.color = color;
    }

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

    private static class DescendingSetView<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableSet<E> set;

        DescendingSetView(NavigableSet<E> set) {
            this.set = set;
        }

        public E lower(E e) {
            return set.higher(e);
        }

        public E floor(E e) {
            return set.ceiling(e);
        }

        public E ceiling(E e) {
            return set.floor(e);
        }

        public E higher(E e) {
            return set.lower(e);
        }

        public E first() {
            return set.last();
        }

        public E last() {
            return set.first();
        }

        public E pollFirst() {
            return set.pollLast();
        }

        public E pollLast() {
            return set.pollFirst();
        }

        public Iterator<E> iterator() {
            return set.descendingIterator();
        }

        public Iterator<E> descendingIterator() {
            return set.iterator();
        }

        public NavigableSet<E> descendingSet() {
            return set;
        }

        public boolean contains(Object o) {
            return set.contains(o);
        }

        public boolean remove(Object o) {
            return set.remove(o);
        }

        public void clear() {
            set.clear();
        }

        public int size() {
            return set.size();
        }

        public Comparator<? super E> comparator() {
            return reverseOrder(set.comparator());
        }

        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return set.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
        }

        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return set.tailSet(toElement, inclusive).descendingSet();
        }

        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return set.headSet(fromElement, inclusive).descendingSet();
        }

        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }
    }

    private static class Node<E>  {
        E value;
        Node<E> left;
        Node<E> right;
        Node<E> parent;
        boolean color = BLACK;

        Node(final E value, final Node<E> parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return  "value=" + value;
        }
    }

    private static class SubSetView<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final CustomTreeSet<E> set;
        private final boolean lowBounded;
        private final E fromElement;
        private final boolean fromInclusive;
        private final boolean highBounded;
        private final E toElement;
        private final boolean toInclusive;

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

        public boolean add(E e) {
            if (!inRange(e))
                throw new IllegalArgumentException();
            return set.add(e);
        }

        public E ceiling(E e) {
            if (tooLow(e))
                return firstOrNull();
            if (tooHigh(e))
                return null;
            E candidate = set.ceiling(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        public Comparator<? super E> comparator() {
            return set.comparator();
        }

        public boolean contains(Object o) {
            return inRange(o) && set.contains(o);
        }

        public Iterator<E> descendingIterator() {
            return descendingSet().iterator();
        }

        public NavigableSet<E> descendingSet() {
            return new DescendingSetView<>(this);
        }

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

        private E firstOrNull() {
            try {
                return first();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        public E floor(E e) {
            if (tooHigh(e))
                return lastOrNull();
            if (tooLow(e))
                return null;
            E candidate = set.floor(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            if (tooLowBound(toElement, inclusive) || tooHighBound(toElement, inclusive))
                throw new IllegalArgumentException();
            return new SubSetView<>(set, lowBounded, fromElement, fromInclusive, true, toElement, inclusive);
        }

        public E higher(E e) {
            if (tooLow(e))
                return firstOrNull();
            if (tooHigh(e))
                return null;
            E candidate = set.higher(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        private boolean inRange(Object o) {
            return !tooLow(o) && !tooHigh(o);
        }
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

        public E lower(E e) {
            if (tooHigh(e))
                return lastOrNull();
            if (tooLow(e))
                return null;
            E candidate = set.lower(e);
            return (candidate != null && inRange(candidate)) ? candidate : null;
        }

        public E pollFirst() {
            E e = firstOrNull();
            if (e != null)
                set.remove(e);
            return e;
        }

        public E pollLast() {
            E e = lastOrNull();
            if (e != null)
                set.remove(e);
            return e;
        }

        public boolean remove(Object o) {
            return inRange(o) && set.remove(o);
        }

        public int size() {
            int count = 0;
            for (E ignored : this)
                count++;
            return count;
        }

        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            if (tooLowBound(fromElement, fromInclusive) || tooHighBound(fromElement, fromInclusive) ||
                    tooLowBound(toElement, toInclusive) || tooHighBound(toElement, toInclusive))
                throw new IllegalArgumentException();
            return new SubSetView<>(set, true, fromElement, fromInclusive, true, toElement, toInclusive);
        }

        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            if (tooLowBound(fromElement, inclusive) || tooHighBound(fromElement, inclusive))
                throw new IllegalArgumentException();
            return new SubSetView<>(set, true, fromElement, inclusive, highBounded, toElement, toInclusive);
        }

        private E lastOrNull() {
            try {
                return last();
            } catch (NoSuchElementException e) { return null; }
        }

        private boolean tooHigh(Object o) {
            if (!highBounded)
                return false;
            int cmp = set.compare(o, toElement);
            return cmp > 0 || (cmp == 0 && !toInclusive);
        }

        private boolean tooHighBound(E element, boolean inclusive) {
            if (!highBounded)
                return false;
            int cmp = set.compare(element, toElement);
            if (cmp > 0)
                return true;
            return cmp == 0 && !toInclusive && inclusive;
        }

        private boolean tooLow(Object o) {
            if (!lowBounded)
                return false;
            int cmp = set.compare(o, fromElement);
            return cmp < 0 || (cmp == 0 && !fromInclusive);
        }

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
