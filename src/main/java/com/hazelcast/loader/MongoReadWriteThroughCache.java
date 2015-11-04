package com.hazelcast.loader;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * TODO
 *
 * @author Viktor Gamov on 11/2/15.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
public class MongoReadWriteThroughCache {
    public static void main(String[] args) throws FileNotFoundException {
        final InputStream resourceAsStream =
            MongoMapStore.class.getClassLoader().getResourceAsStream("hazelcast-mongo.xml");
        Config config = new XmlConfigBuilder(resourceAsStream).build();
        final HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        final IMap<String, Supplement> supplements = instance.getMap("supplements");
        System.out.println(supplements.size());

        supplements.set("1", new Supplement("bcaa", 10));
        supplements.set("2", new Supplement("protein", 100));
        supplements.set("3", new Supplement("glucosamine", 200));

        System.out.println(supplements.size());

        supplements.evictAll();

        System.out.println(supplements.size());

        supplements.loadAll(true);

        System.out.println(supplements.size());
    }
}
