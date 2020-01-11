This library contains some common helpful classes for Java.
There are groups of functions described below.

# `util` package
It gives some additional features similar to `java.util` package

# `math` package
It gives classes from math domain.

## Examples
### `ReversedDistributionFunction`
Allows to create a distribution function from values with related weights and generate values with probability corresponding to these weights.

Let's create some function:
```java
ReversedDistributionFunction<String> reversedDistributionFunction = new ReversedDistributionFunction.Builder<String>()
                .withValueOfWeight("A", 2)
                .withValueOfWeight("B", 1)
                .withValueOfWeight("C", 7)
                .build();
```
Now if you call `next()` method 100 000 times
```java
reversedDistributionFunction.next()
```

you will get `A`, `B` and `C` strings with probabilities corresponding to weights `2`, `1` and `7`. 
On my machine I got results:
- A - 20015
- B - 10089
- C - 69896
