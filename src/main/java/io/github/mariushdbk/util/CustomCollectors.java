package io.github.mariushdbk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Triple;

import static java.util.stream.Collectors.toMap;

public class CustomCollectors {
    public static <V, K> Collector<V,
            Triple<Map<K, Long>,Map<K, Long>,Map<K, Long>>,
            Triple<Map<K, Long>,Map<K, Long>,Map<K, Long>>> countingMultipleGroupingBy(
            Function<V, K> classifier1,
            Function<V, K> classifier2,
            Function<V, K> classifier3) {
        return Collector.of(
                () -> Triple.of(new HashMap<>(), new HashMap<>(), new HashMap<>()),
                (maps, element) -> {
                    incrementGrouping(maps.getLeft(), classifier1, element);
                    incrementGrouping(maps.getMiddle(), classifier2, element);
                    incrementGrouping(maps.getRight(), classifier3, element);
                },
                (partial1, partial2) -> Triple.of(
                        mergeLongMaps(partial1.getLeft(),  partial2.getLeft()),
                        mergeLongMaps(partial1.getMiddle(),  partial2.getMiddle()),
                        mergeLongMaps(partial1.getRight(),  partial2.getRight())));
    }

    private static <K> Map<K, Long> mergeLongMaps(Map<K, Long> left, Map<K, Long> right) {
        return Stream.concat(
                left.entrySet().stream(),
                right.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }

    private static <V, K> void incrementGrouping(Map<K, Long> map, Function<V, K> classifier, V element) {
        K key = classifier.apply(element);
        Long value = map.computeIfAbsent(key, $ -> 0L);
        map.put(key, value + 1);
    }
}
