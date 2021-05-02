package dev.lazurite.dropz.util;

public enum YeetType {
    SMALL(5),
    MEDIUM(15),
    LARGE(25);

    private final int multiplier;

    YeetType(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return this.multiplier;
    }
}
