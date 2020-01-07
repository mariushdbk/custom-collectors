package io.github.mariushdbk.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

public class ManyToOneMapTest {

    public static void main(String[] args) {
        List<Something> somethings = Arrays.asList(new Something("A", null), new Something("A", 1L), new Something("B", 2L));
        Map<String, Long> collect = somethings.stream().collect(groupingBy($ -> $.key, summingLong($ -> $.value)));
        System.out.println(collect);
        String x20;
        String x21;
        String x22;
        String x23;
        String x24;
        String x25;
        String x26;
        String x27;
        String x28;
        String x29;
        String x30;
        String x31;
        String x32;
    }

    private static class Something {
        private final String key;
        private final Long value;

        public Something(String key, Long value) {
            this.key = key;
            this.value = value;
        }
    }
}
