package com.budgeteer.api.receipts.gcp;

public enum Orientation {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public boolean isVertical() {
        return this.equals(UP) || this.equals(DOWN);
    }
}