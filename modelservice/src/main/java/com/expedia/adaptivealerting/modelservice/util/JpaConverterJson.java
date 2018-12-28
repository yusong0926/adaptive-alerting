/*
 * Copyright 2018 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.modelservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kashah
 */

@Component
public class JpaConverterJson implements AttributeConverter<Object, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    //Map<String,String> map = new HashMap<String, String>();
    //private String tag_value;


    @Override
    public String convertToDatabaseColumn(Object data) {
        try {
            //System.out.println("Value is: "+data);
//            tag_value = objectMapper.writeValueAsString(data);
//            System.out.println(tag_value);
            // String[] arrStr = tag_value.split(":",-1);
            // for (String a : arrStr)
               // System.out.println(a);
//            Map<String, String> map = objectMapper.convertValue(data, Map.class);
//            System.out.println("Map data: "+map);
            //System.out.println("Key Value pair is: "+map.keySet());
            //System.out.println("Value is: "+map.values());

            //tag_value=objectMapper.writeValueAsString(data);
            //System.out.println(objectMapper.writeValueAsString(data));

            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert to Json", e);
        }
    }



    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData))
            return new HashMap<>();
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            throw new HibernateException("unable to read object from result set", e);
        }
    }
}
