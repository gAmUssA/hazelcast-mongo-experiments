/*
 * Copyright (c) 2008-2010, Hazel Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hazelcast.serialization.benchmarks;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.serialization.SerializationService;
import com.hazelcast.internal.serialization.SerializationServiceBuilder;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.serialization.dto.ShoppingCart;
import com.hazelcast.serialization.dto.ShoppingCartItem;
import com.hazelcast.serialization.serializer.ShoppingCartBson4JacksonSerializer;
import com.hazelcast.simulator.worker.loadsupport.MapStreamer;
import com.hazelcast.simulator.worker.loadsupport.MapStreamerFactory;
import com.hazelcast.test.TestHazelcastInstanceFactory;

import java.util.Date;
import java.util.Random;

public class Bson4JacksonBenchmark implements ShoppingCartBenchmark {

    private HazelcastInstance hz;
    private IMap<Object, Object> cartMap;
    private int maxOrders = 100 * 1000;
    private int maxCartItems = 5;
    private String[] products;
    private HazelcastInstance[] hazelcastInstances;

    @Override
    public void setUp() {

        Config config = new XmlConfigBuilder().build();
        config.getSerializationConfig().getSerializerConfigs().add(
            new SerializerConfig().
                setTypeClass(ShoppingCart.class).
                setImplementation(new ShoppingCartBson4JacksonSerializer()));


        //hz = Hazelcast.newHazelcastInstance(config);
        hazelcastInstances = new TestHazelcastInstanceFactory(3).newInstances(config);
        hz = hazelcastInstances[0];

        cartMap = hz.getMap("carts");
        final MapStreamer<Object, Object> cartMapStreamer =
            MapStreamerFactory.getInstance(hz.getMap("carts"));
        products = new String[100];
        for (int k = 0; k < 100; k++) {
            products[k] = "product-" + k;
        }
        Random random = new Random();
        SerializationServiceBuilder builder = new DefaultSerializationServiceBuilder();
        builder.setConfig(hz.getConfig().getSerializationConfig());
        SerializationService ss = builder.build();
        int total = 0;
        for (int k = 0; k < maxOrders; k++) {
            ShoppingCart cart = createNewShoppingCart(random);
            Data data = ss.toData(cart);
            total += data.totalSize();
            cartMapStreamer.pushEntry(cart.id, cart);
            //cartMap.set(cart.id, cart);
        }
        cartMapStreamer.await();
        System.out.println("Average size is " + total / maxOrders + " bytes");
    }

    @Override
    public void tearDown() {
        for (HazelcastInstance hazelcastInstance : hazelcastInstances) {
            hazelcastInstance.getLifecycleService().terminate();
        }
    }

    @Override
    public void writePerformance() {
        Random random = new Random();
        for (int k = 0; k < OPERATIONS_PER_INVOCATION; k++) {
            ShoppingCart cart = createNewShoppingCart(random);
            cartMap.set(cart.id, cart);
        }
    }

    @Override
    public void readPerformance() {
        Random random = new Random();
        for (int k = 0; k < OPERATIONS_PER_INVOCATION; k++) {
            long orderId = random.nextInt(maxOrders);
            cartMap.get(orderId);
        }
    }

    private ShoppingCart createNewShoppingCart(Random random) {
        ShoppingCart cart = new ShoppingCart();
        cart.id = random.nextInt(maxOrders);
        cart.date = new Date();
        int count = random.nextInt(maxCartItems);
        for (int k = 0; k < count; k++) {
            ShoppingCartItem item = createNewShoppingCartItem(random);
            cart.addItem(item);
        }
        return cart;
    }

    private ShoppingCartItem createNewShoppingCartItem(Random random) {
        int i = random.nextInt(10);
        ShoppingCartItem item = new ShoppingCartItem();
        item.cost = i * 9;
        item.quantity = i % 2;
        item.itemName = "item_" + i;
        item.inStock = (i == 9);
        item.url = "http://www.amazon.com/gp/product/" + i;
        return item;
    }
}
