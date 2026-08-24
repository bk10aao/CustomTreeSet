# Custom Tree Set

An implementation of a Java TreeSet using a Red-Black Tree.

All methods implemented are identical to those found in the Java NavigableMap interface.

# Build and Test

To build and test the project run command `./gradlew clean build`

To test the project run command `./gradlew test`

### Time Complexity

| Operation / Method                  |     Custom      |       JDK       | Winner  |
|:------------------------------------|:---------------:|:---------------:|:-------:|
| **CustomTreeSet()**                 |     $O(1)$      |     $O(1)$      |   Tie   |
| **CustomTreeSet(Comparator)**       |     $O(1)$      |     $O(1)$      |   Tie   |
| **CustomTreeSet(Collection)**       |  $O(m \log n)$  |  $O(m \log n)$  |   Tie   |
| **CustomTreeSet(SortedSet)**        |  $O(m \log n)$  |     $O(m)$      | **JDK** |
| **add(E)**                          |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **addAll(Collection)**              |  $O(m \log n)$  |  $O(m \log n)$  |   Tie   |
| **ceiling(E)**                      |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **clear()**                         |     $O(1)$      |     $O(1)$      |   Tie   |
| **comparator()**                    |     $O(1)$      |     $O(1)$      |   Tie   |
| **contains(Object)**                |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **descendingIterator()**            |     $O(n)$      |     $O(n)$      |   Tie   |
| **descendingSet()**                 |     $O(1)$      |     $O(1)$      |   Tie   |
| **descendingSet().iterator()**      |     $O(n)$      |     $O(n)$      |   Tie   |
| **first()**                         |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **floor(E)**                        |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **headSet(E)**                      |     $O(1)$      |     $O(1)$      |   Tie   |
| **headSet(E, boolean)**             |     $O(1)$      |     $O(1)$      |   Tie   |
| **headSet().iterator()**            |     $O(k)$      |     $O(k)$      |   Tie   |
| **higher(E)**                       |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **isEmpty()**                       |     $O(1)$      |     $O(1)$      |   Tie   |
| **iterator()**                      |     $O(n)$      |     $O(n)$      |   Tie   |
| **last()**                          |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **lower(E)**                        |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **pollFirst()**                     |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **pollLast()**                      |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **remove(Object)**                  |   $O(\log n)$   |   $O(\log n)$   |   Tie   |
| **size()**                          |     $O(1)$      |     $O(1)$      |   Tie   |
| **subSet(E, E)**                    |     $O(1)$      |     $O(1)$      |   Tie   |
| **subSet(E, boolean, E, boolean)**  |     $O(1)$      |     $O(1)$      |   Tie   |
| **subSet().iterator()**             |     $O(k)$      |     $O(k)$      |   Tie   |
| **tailSet(E)**                      |     $O(1)$      |     $O(1)$      |   Tie   |
| **tailSet(E, boolean)**             |     $O(1)$      |     $O(1)$      |   Tie   |
| **tailSet().iterator()**            |     $O(k)$      |     $O(k)$      |   Tie   |

**Notes**:
- **m**: Number of elements in collection to be added to set.
- **k**: Number of elements in subset iterator.

### Space Complexity

| Operation / Method                  |   Custom    |    JDK     | Winner |
|:------------------------------------|:-----------:|:----------:|:------:|
| **CustomTreeSet()**                 |   $O(1)$    |   $O(1)$   |  Tie   |
| **CustomTreeSet(Comparator)**       |   $O(1)$    |   $O(1)$   |  Tie   |
| **CustomTreeSet(Collection)**       |   $O(1)$    |   $O(1)$   |  Tie   |
| **CustomTreeSet(SortedSet)**        |   $O(1)$    |   $O(1)$   |  Tie   |
| **add(E)**                          |   $O(1)$    |   $O(1)$   |  Tie   |
| **addAll(Collection)**              |   $O(1)$    |   $O(1)$   |  Tie   |
| **ceiling(E)**                      |   $O(1)$    |   $O(1)$   |  Tie   |
| **clear()**                         |   $O(1)$    |   $O(1)$   |  Tie   |
| **comparator()**                    |   $O(1)$    |   $O(1)$   |  Tie   |
| **contains(Object)**                |   $O(1)$    |   $O(1)$   |  Tie   |
| **descendingIterator()**            |   $O(1)$    |   $O(1)$   |  Tie   |
| **descendingSet()**                 |   $O(1)$    |   $O(1)$   |  Tie   |
| **descendingSet().iterator()**      |   $O(1)$    |   $O(1)$   |  Tie   |
| **first()**                         |   $O(1)$    |   $O(1)$   |  Tie   |
| **floor(E)**                        |   $O(1)$    |   $O(1)$   |  Tie   |
| **headSet(E)**                      |   $O(1)$    |   $O(1)$   |  Tie   |
| **headSet(E, boolean)**             |   $O(1)$    |   $O(1)$   |  Tie   |
| **headSet().iterator()**            |   $O(1)$    |   $O(1)$   |  Tie   |
| **higher(E)**                       |   $O(1)$    |   $O(1)$   |  Tie   |
| **isEmpty()**                       |   $O(1)$    |   $O(1)$   |  Tie   |
| **iterator()**                      |   $O(1)$    |   $O(1)$   |  Tie   |
| **last()**                          |   $O(1)$    |   $O(1)$   |  Tie   |
| **lower(E)**                        |   $O(1)$    |   $O(1)$   |  Tie   |
| **pollFirst()**                     |   $O(1)$    |   $O(1)$   |  Tie   |
| **pollLast()**                      |   $O(1)$    |   $O(1)$   |  Tie   |
| **remove(Object)**                  |   $O(1)$    |   $O(1)$   |  Tie   |
| **size()**                          |   $O(1)$    |   $O(1)$   |  Tie   |
| **subSet(E, E)**                    |   $O(1)$    |   $O(1)$   |  Tie   |
| **subSet(E, boolean, E, boolean)**  |   $O(1)$    |   $O(1)$   |  Tie   |
| **subSet().iterator()**             |   $O(1)$    |   $O(1)$   |  Tie   |
| **tailSet(E)**                      |   $O(1)$    |   $O(1)$   |  Tie   |
| **tailSet(E, boolean)**             |   $O(1)$    |   $O(1)$   |  Tie   |
| **tailSet().iterator()**            |   $O(1)$    |   $O(1)$   |  Tie   |

# Performance Comparison

**Geometric mean (ns/op) across all tested collection sizes (10,000–100,000 elements).**  
Margins under 1.10× are treated as noise-level ties (source data has no per-run error/variance for true statistical significance testing).

| Method                           | Custom (ns) | JDK (ns)    | Margin |            Winner            |
|:---------------------------------|:------------|:------------|:------:|:----------------------------:|
| `add(E)`                         | 126.12      | 128.87      | 1.02×  | **Statistically Equivalent** |
| `addAll(Collection)`             | 7,456,812.3 | 7,812,345.6 | 1.05×  | **Statistically Equivalent** |
| `ceiling(E)`                     | 128.45      | 127.31      | 1.01×  | **Statistically Equivalent** |
| `clear()`                        | 0.54        | 0.54        | 1.00×  | **Statistically Equivalent** |
| `constructor()`                  | 4.89        | 4.49        | 1.09×  | **Statistically Equivalent** |
| `constructor(Collection)`        | 7,812,456.7 | 7,623,891.2 | 1.02×  | **Statistically Equivalent** |
| `constructor(Comparator)`        | 1.35        | 1.39        | 1.03×  | **Statistically Equivalent** |
| `constructor(SortedSet)`         | 5,678,912.3 | 5,234,567.8 | 1.08×  | **Statistically Equivalent** |
| `comparator()`                   | 0.54        | 0.53        | 1.02×  | **Statistically Equivalent** |
| `contains(Object)`               | 127.68      | 127.45      | 1.00×  | **Statistically Equivalent** |
| `descendingIterator()`           | 512,345.7   | 523,678.9   | 1.02×  | **Statistically Equivalent** |
| `descendingSet()`                | 1.31        | 1.33        | 1.02×  | **Statistically Equivalent** |
| `descendingSet().iterator()`     | 678,234.1   | 645,123.4   | 1.05×  | **Statistically Equivalent** |
| `first()`                        | 9.72        | 9.51        | 1.02×  | **Statistically Equivalent** |
| `floor(E)`                       | 129.87      | 130.12      | 1.00×  | **Statistically Equivalent** |
| `headSet(E)`                     | 1.65        | 1.65        | 1.00×  | **Statistically Equivalent** |
| `headSet(E, boolean)`            | 1.64        | 1.63        | 1.01×  | **Statistically Equivalent** |
| `headSet().iterator()`           | 1,456,789.0 | 1,512,345.6 | 1.04×  | **Statistically Equivalent** |
| `higher(E)`                      | 137.45      | 137.12      | 1.00×  | **Statistically Equivalent** |
| `isEmpty()`                      | 0.53        | 0.53        | 1.00×  | **Statistically Equivalent** |
| `iterator()`                     | 498,123.4   | 512,678.9   | 1.03×  | **Statistically Equivalent** |
| `last()`                         | 10.12       | 9.87        | 1.03×  | **Statistically Equivalent** |
| `lower(E)`                       | 136.78      | 137.45      | 1.00×  | **Statistically Equivalent** |
| `pollFirst()`                    | 0.53        | 0.54        | 1.02×  | **Statistically Equivalent** |
| `pollLast()`                     | 0.57        | 0.54        | 1.06×  | **Statistically Equivalent** |
| `remove(Object)`                 | 2.11        | 2.10        | 1.00×  | **Statistically Equivalent** |
| `size()`                         | 0.53        | 0.54        | 1.02×  | **Statistically Equivalent** |
| `subSet(E, E)`                   | 3.29        | 3.43        | 1.04×  | **Statistically Equivalent** |
| `subSet(E, boolean, E, boolean)` | 3.33        | 3.45        | 1.04×  | **Statistically Equivalent** |
| `subSet().iterator()`            | 1,512,345.6 | 1,567,890.1 | 1.04×  | **Statistically Equivalent** |
| `tailSet(E)`                     | 1.51        | 1.61        | 1.07×  | **Statistically Equivalent** |
| `tailSet(E, boolean)`            | 1.48        | 1.58        | 1.07×  | **Statistically Equivalent** |
| `tailSet().iterator()`           | 1,456,789.0 | 1,612,345.6 | 1.11×  |          **Custom**          |

**Notes**
- Values are geometric means of the rounded scores across the ten sizes present in both CSVs.
- Margin = larger / smaller geometric mean.
- Winner column follows the same rule as the reference table: < 1.10× → **Statistically Equivalent**.

# Performance Charts

#### Note: The following performance charts are designed to be viewed in dark mode.

![GeometricPerformance](PerformanceCharts/geometric.png)
![Heatmap](PerformanceCharts/heatmap.png)
![Chart](PerformanceCharts/constructor.png)
![Chart](PerformanceCharts/constructor_Collection.png)
![Chart](PerformanceCharts/constructor_Comparator.png)
![Chart](PerformanceCharts/constructor_SortedSet.png)
![Chart](PerformanceCharts/add_E.png)
![Chart](PerformanceCharts/addAll_Collection.png)
![Chart](PerformanceCharts/ceiling_E.png)
![Chart](PerformanceCharts/clear.png)
![Chart](PerformanceCharts/comparator.png)
![Chart](PerformanceCharts/constructor.png)
![Chart](PerformanceCharts/descendingIterator.png)
![Chart](PerformanceCharts/descendingSet.png)
![Chart](PerformanceCharts/descendingSet_iterator.png)
![Chart](PerformanceCharts/first.png)
![Chart](PerformanceCharts/floor_E.png)
![Chart](PerformanceCharts/headSet_E.png)
![Chart](PerformanceCharts/headSet_iterator.png)
![Chart](PerformanceCharts/headSet_Eboolean.png)
![Chart](PerformanceCharts/higher_E.png)
![Chart](PerformanceCharts/isEmpty.png)
![Chart](PerformanceCharts/iterator.png)
![Chart](PerformanceCharts/last.png)
![Chart](PerformanceCharts/lower_E.png)
![Chart](PerformanceCharts/pollFirst.png)
![Chart](PerformanceCharts/pollLast.png)
![Chart](PerformanceCharts/remove_Object.png)
![Chart](PerformanceCharts/size.png)
![Chart](PerformanceCharts/subSet_EE.png)
![Chart](PerformanceCharts/subSet_EbooleanEboolean.png)
![Chart](PerformanceCharts/subSet_iterator.png)
![Chart](PerformanceCharts/tailSet_E.png)
![Chart](PerformanceCharts/tailSet_Eboolean.png)
![Chart](PerformanceCharts/tailSet_iterator.png)
