package com.normie.aiquicktest.common;

import lombok.Data;

/**
 * Pair
 * @param <L>
 * @param <R>
 */
@Data
public class Pair<L, R> {
    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
