package com.hazelcast.loader;

import java.io.Serializable;

/**
 * TODO
 *
 * @author Viktor Gamov on 11/4/15.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
public class Supplement implements Serializable{
    private final String name;

    public Supplement(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    private final Integer price;

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }
}
