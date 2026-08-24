package customtreeset;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class CustomTreeSetPerformanceTest {

    @Param({ "10000", "20000", "30000", "40000", "50000", "60000", "70000", "80000", "90000", "100000"})
    private int size;

    private Integer[] elements;
    private CustomTreeSet<Integer> set;
    private Set<Integer> populatedHashSet;
    private SortedSet<Integer> populatedSortedSet;
    private Comparator<Integer> reverseComp;

    private int index;

    private Integer minElement;
    private Integer maxElement;
    private Integer midElement;

    @Setup(Level.Trial)
    public void setupTrial() {
        elements = new Integer[size];
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            elements[i] = random.nextInt();
        }

        populatedHashSet = new HashSet<>();
        populatedSortedSet = new TreeSet<>();
        reverseComp = Comparator.reverseOrder();

        for (Integer element : elements) {
            populatedHashSet.add(element);
            populatedSortedSet.add(element);
        }

        minElement = populatedSortedSet.first();
        maxElement = populatedSortedSet.last();

        List<Integer> sortedList = new ArrayList<>(populatedSortedSet);
        midElement = sortedList.get(size / 2);

        index = 0;
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        set = new CustomTreeSet<>();
        for (Integer element : elements) {
            set.add(element);
        }
    }

    private int getNextElement() {
        return elements[Math.abs(index++ % size)];
    }

    @Benchmark
    public void testDefaultConstructor(Blackhole bh) {
        bh.consume(new CustomTreeSet<Integer>());
    }

    @Benchmark
    public void testComparatorConstructor(Blackhole bh) {
        bh.consume(new CustomTreeSet<Integer>(reverseComp));
    }

    @Benchmark
    public void testCollectionConstructor(Blackhole bh) {
        bh.consume(new CustomTreeSet<>(populatedHashSet));
    }

    @Benchmark
    public void testSortedSetConstructor(Blackhole bh) {
        bh.consume(new CustomTreeSet<>(populatedSortedSet));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        bh.consume(set.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        bh.consume(set.isEmpty());
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        bh.consume(set.contains(getNextElement()));
    }

    @Benchmark
    public void testComparator(Blackhole bh) {
        bh.consume(set.comparator());
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        int e = getNextElement();
        bh.consume(set.add(e));
    }

    @Benchmark
    public void testAddAll(Blackhole bh) {
        CustomTreeSet<Integer> target = new CustomTreeSet<>();
        target.addAll(populatedHashSet);
        bh.consume(target);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        bh.consume(set.remove(getNextElement()));
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        set.clear();
        bh.consume(set);
    }

    @Benchmark
    public void testFirst(Blackhole bh) {
        bh.consume(set.first());
    }

    @Benchmark
    public void testLast(Blackhole bh) {
        bh.consume(set.last());
    }

    @Benchmark
    public void testPollFirst(Blackhole bh) {
        bh.consume(set.pollFirst());
    }

    @Benchmark
    public void testPollLast(Blackhole bh) {
        bh.consume(set.pollLast());
    }

    @Benchmark
    public void testLower(Blackhole bh) {
        bh.consume(set.lower(getNextElement()));
    }

    @Benchmark
    public void testFloor(Blackhole bh) {
        bh.consume(set.floor(getNextElement()));
    }

    @Benchmark
    public void testCeiling(Blackhole bh) {
        bh.consume(set.ceiling(getNextElement()));
    }

    @Benchmark
    public void testHigher(Blackhole bh) {
        bh.consume(set.higher(getNextElement()));
    }

    @Benchmark
    public void testIterator(Blackhole bh) {
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void testDescendingIterator(Blackhole bh) {
        Iterator<Integer> it = set.descendingIterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void testDescendingSet(Blackhole bh) {
        bh.consume(set.descendingSet());
    }

    @Benchmark
    public void testDescendingSetIterator(Blackhole bh) {
        Iterator<Integer> it = set.descendingSet().iterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void testSubSetTwoArgs(Blackhole bh) {
        bh.consume(set.subSet(minElement, midElement));
    }

    @Benchmark
    public void testSubSetFourArgs(Blackhole bh) {
        bh.consume(set.subSet(minElement, true, midElement, false));
    }

    @Benchmark
    public void testHeadSetOneArg(Blackhole bh) {
        bh.consume(set.headSet(midElement));
    }

    @Benchmark
    public void testHeadSetTwoArgs(Blackhole bh) {
        bh.consume(set.headSet(midElement, true));
    }

    @Benchmark
    public void testTailSetOneArg(Blackhole bh) {
        bh.consume(set.tailSet(midElement));
    }

    @Benchmark
    public void testTailSetTwoArgs(Blackhole bh) {
        bh.consume(set.tailSet(midElement, true));
    }

    @Benchmark
    public void testSubSetIterator(Blackhole bh) {
        Iterator<Integer> it = set.subSet(minElement, true, midElement, false).iterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void testHeadSetIterator(Blackhole bh) {
        Iterator<Integer> it = set.headSet(midElement).iterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void testTailSetIterator(Blackhole bh) {
        Iterator<Integer> it = set.tailSet(midElement).iterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CustomTreeSetPerformanceTest.class.getSimpleName())
                .measurementIterations(3)
                .warmupIterations(2)
                .forks(1)
                .result("CustomTreeSet_performance_results.csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        writeCustomCsv(results);
    }

    private static void writeCustomCsv(Collection<RunResult> results) {
        try (FileWriter writer = new FileWriter("CustomTreeSet_jmh_performance.csv")) {
            writer.write("Benchmark;Size;Score (ns/op)\n");
            for (RunResult result : results) {
                String benchmarkName = result.getParams().getBenchmark();
                String shortName = benchmarkName.substring(benchmarkName.lastIndexOf('.') + 1);

                double score = result.getPrimaryResult().getScore();
                String sizeVal = result.getParams().getParam("size");

                writer.write("\"" + shortName + "\";" + (sizeVal != null ? sizeVal : "N/A") + ";" + score + "\n");
            }
            System.out.println("JMH Performance report saved: CustomTreeSet_jmh_performance.csv");
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}