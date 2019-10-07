package io.github.mariushdbk.util;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomCollectorsTest {

    @Test
    public void shouldCollectEmptyList() {
        // given
        List<ComplexType> elements = Collections.emptyList();

        // when
        Triple<Map<String, Long>, Map<String, Long>, Map<String, Long>> result =
                elements.stream().collect(CustomCollectors.countingMultipleGroupingBy(ComplexType::key1, ComplexType::key2, ComplexType::key3));

        // then
        assertThat(result.getLeft().entrySet()).isEmpty();
        assertThat(result.getMiddle().entrySet()).isEmpty();
        assertThat(result.getRight().entrySet()).isEmpty();
    }

    @Test
    public void shouldCountByGrouping() {
        // given
        List<ComplexType> elements = Arrays.asList(
                new ComplexType("A", "1", "Z"),
                new ComplexType("A", "2", "Y"),
                new ComplexType("A", "3", "Y"),
                new ComplexType("B", "1", "X"),
                new ComplexType("B", "2", "X"),
                new ComplexType("C", "3", "X")
        );

        // when
        Triple<Map<String, Long>, Map<String, Long>, Map<String, Long>> result =
                elements.stream().collect(CustomCollectors.countingMultipleGroupingBy(ComplexType::key1, ComplexType::key2, ComplexType::key3));

        // then
        assertThat(result.getLeft()).hasSize(3);
        assertThat(result.getLeft().get("A")).isEqualTo(3);
        assertThat(result.getLeft().get("B")).isEqualTo(2);
        assertThat(result.getLeft().get("C")).isEqualTo(1);
        assertThat(result.getMiddle()).hasSize(3);
        assertThat(result.getMiddle().get("1")).isEqualTo(2);
        assertThat(result.getMiddle().get("2")).isEqualTo(2);
        assertThat(result.getMiddle().get("3")).isEqualTo(2);
        assertThat(result.getRight()).hasSize(3);
        assertThat(result.getRight().get("X")).isEqualTo(3);
        assertThat(result.getRight().get("Y")).isEqualTo(2);
        assertThat(result.getRight().get("Z")).isEqualTo(1);
    }

    private static class ComplexType {
        private String key1;
        private String key2;
        private String key3;

        private ComplexType(String key1, String key2, String key3) {
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
        }

        String key1() {
            return key1;
        }

        String key2() {
            return key2;
        }

        String key3() {
            return key3;
        }

    }
}
