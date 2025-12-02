package dev.kvnmtz.mercantile.common.data;

import java.util.*;

public class CoinType implements Comparable<CoinType> {

    private static final Map<String, CoinType> BY_NAME = new HashMap<>();

    private static final SortedSet<CoinType> BY_VALUE = new TreeSet<>(
            Comparator.comparingInt(CoinType::getValue).reversed().thenComparing(CoinType::getName)
    );

    private static final List<CoinType> BY_INDEX = new ArrayList<>();

    private final String name;
    private final int value;
    private final int index;

    private CoinType(String name, int value, int index) {
        this.name = name;
        this.value = value;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int compareTo(CoinType other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var coinType = (CoinType) o;
        return name.equals(coinType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static void register(String name, int value) {
        var lookupName = name.toLowerCase().trim();
        if (lookupName.isEmpty()) {
            throw new IllegalArgumentException("Coin name cannot be empty");
        }

        if (BY_NAME.containsKey(lookupName)) {
            throw new IllegalArgumentException("Coin type with name '" + lookupName + "' already registered");
        }

        var newIndex = BY_INDEX.size();
        var newType = new CoinType(lookupName, value, newIndex);

        BY_NAME.put(lookupName, newType);
        BY_VALUE.add(newType);
        BY_INDEX.add(newType);
    }

    /**
     * Sorted by value (descending, highest first)
     */
    public static TreeSet<CoinType> getAllSorted() {
        return new TreeSet<>(BY_VALUE);
    }

    public static CoinType getByIndex(int index) {
        return BY_INDEX.get(index);
    }
}