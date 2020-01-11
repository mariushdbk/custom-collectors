package io.github.mariushdbk.math;

import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;

public class ReversedDistributionFunction<T> {

    private List<Builder.ValueWithRange<T>> valuesWithMatchRanges;
    private ThreadLocalRandom random;

    private ReversedDistributionFunction(List<Builder.ValueWithRange<T>> valuesWithMatchRanges) {
        this.valuesWithMatchRanges = valuesWithMatchRanges;
        this.random = ThreadLocalRandom.current();
    }

    T next() {
        double randomDouble = random.nextDouble();
        return valuesWithMatchRanges.stream()
                .filter($ -> $.range.contains(randomDouble))
                .findFirst().map($ -> $.value)
                .orElse(valuesWithMatchRanges.get(valuesWithMatchRanges.size() - 1).value);
    }

    static class Builder<T> {
        private List<ValueWithWeight<T>> valuesWithWeights = new ArrayList<>();

        Builder<T> withValueOfWeight(T value, long weight) {
            valuesWithWeights.add(ValueWithWeight.of(value, weight));
            return this;
        }

        ReversedDistributionFunction<T> build() {
            if (valuesWithWeights.isEmpty()) {
                throw new IllegalStateException("Cannot build distribution function with no statistics data");
            }
            double sum = valuesWithWeights.stream().mapToDouble($ -> $.weight).sum();
            double normalizationFactor = 1.0 / sum;
            AtomicReference<Double> previousRightBound = new AtomicReference<>(0.0);
            return new ReversedDistributionFunction<T>(valuesWithWeights.stream()
                    .map($ -> {
                        double leftBound = previousRightBound.get();
                        previousRightBound.set(leftBound + normalizationFactor * $.weight);
                        return ValueWithRange.of($.value, Range.between(leftBound, previousRightBound.get()));
                    })
                    .collect(toList()));
        }

        private static class ValueWithWeight<T> {
            private final T value;
            private final double weight;

            private ValueWithWeight(T value, double weight) {
                this.value = value;
                this.weight = weight;
            }

            public static <T> ValueWithWeight<T> of(T value, double weight) {
                return new ValueWithWeight<>(value, weight);
            }
        }

        private static class ValueWithRange<T> {
            private final T value;
            private final Range<Double> range;

            private ValueWithRange(T value, Range<Double> range) {
                this.value = value;
                this.range = range;
            }

            public static <T> ValueWithRange<T> of(T value, Range<Double> range) {
                return new ValueWithRange<>(value, range);
            }
        }
    }
}
