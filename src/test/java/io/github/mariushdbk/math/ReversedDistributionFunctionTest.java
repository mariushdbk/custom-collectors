package io.github.mariushdbk.math;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class ReversedDistributionFunctionTest {

    @Test
    public void distributionFunctionHistogramTest() {
        ReversedDistributionFunction.Builder<String> builder = new ReversedDistributionFunction.Builder<>();
        builder.addValueWithWeight("A", 2);
        builder.addValueWithWeight("B", 1);
        builder.addValueWithWeight("C", 7);
        ReversedDistributionFunction<String> reversedDistributionFunction = builder.build();

        List<String> result = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            result.add(reversedDistributionFunction.next());
        }

        Map<String, Long> resultMap = result.stream()
                .collect(groupingBy($ -> $, counting()));

        Percentage acceptedMistake = Percentage.withPercentage(5);
        Assertions.assertThat(resultMap.get("A") + 0.0).isCloseTo(20000, acceptedMistake);
        Assertions.assertThat(resultMap.get("B") + 0.0).isCloseTo(10000, acceptedMistake);
        Assertions.assertThat(resultMap.get("C") + 0.0).isCloseTo(70000, acceptedMistake);
    }
}
