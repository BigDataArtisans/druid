package com.druid.test;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProduce {

    public static void main(String[] args) throws Exception {

        System.out.println("begin produce");
        connectionKafka();
        System.out.println("finish produce");

    }

    public static void connectionKafka() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer(props);

        producer.send(cr(
                new Event("2020-01-12 18:14:10", "china", "nanjing", "uid-1", 1)
        ));

        producer.close();
    }

    public static ProducerRecord cr(Event event) {
        String content = JSON.toJSONString(event);
        return new ProducerRecord("bigdata-rtc-olap", content);
    }

}