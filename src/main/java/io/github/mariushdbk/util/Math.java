package io.github.mariushdbk.util;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Math {
    private Map<String, ReversedDistributionFunction> reversedDistributionFunctionsByModelYearFrom(List<VisitWithValueRow> visitRows) {
        return visitRows.stream()
                .filter($ -> $.registrationDate != null)
                .collect(groupingBy($ -> new DateAndModelYear($.registrationDate, $.modelYearValue), Collectors.counting()))
                .entrySet()
                .stream()
                .collect(groupingBy(
                        $ -> $.getKey().modelYearValue,
                        mapping(
                                $ -> Pair.of($.getKey().date, $.getValue()),
                                toList())))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, $ -> {
                    ReversedDistributionFunction.Builder functionBuilder = new ReversedDistributionFunction.Builder();
                    $.getValue().forEach(dateWithCount -> functionBuilder.addValueWithWeight(dateWithCount.getLeft(), dateWithCount.getRight()));
                    return functionBuilder.build();
                }));
    }

    private class DateAndModelYear {
        private final Date date;
        private final String modelYearValue;

        DateAndModelYear(Date date, String modelYearValue) {
            this.date = DateUtils.asStartOfDay(date);
            this.modelYearValue = modelYearValue;
        }
    }

    @Test
    public void distributionFunctionHistogramTest() {
        ReversedDistributionFunction.Builder builder = new ReversedDistributionFunction.Builder();
        builder.addValueWithWeight(DateFormat.parseDate("2019-04-01"), 1);
        builder.addValueWithWeight(DateFormat.parseDate("2019-04-02"), 2);
        builder.addValueWithWeight(DateFormat.parseDate("2019-04-03"), 7);
        ReversedDistributionFunction reversedDistributionFunction = builder.build();

        List<Date> result = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            result.add(reversedDistributionFunction.next());
        }

        result.stream()
                .collect(groupingBy($ -> $, Collectors.counting()))
                .forEach((key, value) -> System.out.println(key + ": " + value));
    }

    private static class ReversedDistributionFunction {

        private List<Pair<Date, Range<Double>>> datesAndRanges;
        private ThreadLocalRandom random;

        private ReversedDistributionFunction(List<Pair<Date, Range<Double>>> datesAndRanges) {
            this.datesAndRanges = datesAndRanges;
            this.random = ThreadLocalRandom.current();
        }

        Date next() {
            double randomDouble = random.nextDouble();
            return datesAndRanges.stream()
                    .filter($ -> $.getRight().containsDouble(randomDouble))
                    .findFirst().map(Pair::getLeft)
                    .orElse(datesAndRanges.get(datesAndRanges.size() - 1).getLeft());
        }

        static class Builder {
            private List<Pair<Date, Long>> datesAndWeights = new ArrayList<>();

            void addValueWithWeight(Date value, long weight) {
                datesAndWeights.add(Pair.of(value, weight));
            }

            ReversedDistributionFunction build() {
                if (datesAndWeights.isEmpty()) {
                    throw new IllegalStateException("Cannot build distribution function with no statistics data");
                }
                long sum = datesAndWeights.stream().mapToLong(Pair::getRight).sum();
                double toOneScaleMultiplier = 1.0 / sum;
                AtomicReference<Double> previousRightBound = new AtomicReference<>(0.0);
                return new ReversedDistributionFunction(datesAndWeights.stream()
                        .map($ -> {
                            double leftBound = previousRightBound.get();
                            previousRightBound.set(leftBound + toOneScaleMultiplier * $.getRight());
                            return Pair.of($.getLeft(), new Range<Double>(leftBound, previousRightBound.get().doubleValue()));
                        })
                        .collect(toList()));
            }
        }
    }
}
