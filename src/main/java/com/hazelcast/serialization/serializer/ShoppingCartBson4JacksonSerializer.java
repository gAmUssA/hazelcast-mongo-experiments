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

package com.hazelcast.serialization.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.hazelcast.serialization.dto.ShoppingCart;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ShoppingCartBson4JacksonSerializer implements ByteArraySerializer<ShoppingCart> {
    private ObjectMapper mapper;

    public ShoppingCartBson4JacksonSerializer() {
        final BsonFactory bsonFactory = new BsonFactory();
        bsonFactory.enable(BsonGenerator.Feature.ENABLE_STREAMING);
        this.mapper = new ObjectMapper(bsonFactory);
    }


    @Override public byte[] write(ShoppingCart object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, object);
        return outputStream.toByteArray();
    }

    @Override public ShoppingCart read(byte[] buffer) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        mapper.readValue(byteArrayInputStream, ShoppingCart.class);
        return null;
    }

    @Override public int getTypeId() {
        return 0;
    }

    @Override public void destroy() {

    }
}
