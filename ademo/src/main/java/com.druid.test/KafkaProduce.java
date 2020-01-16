package com.druid.test;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Properties;
import java.util.UUID;

public class KafkaProduce {

    public static void main(String[] args) throws Exception {


        String s = DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss'Z' 'CTT'");
        System.out.println(s);


        System.out.println("begin produce");
        connectionKafka();
        System.out.println("finish produce");

    }

    public static void connectionKafka() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.100.156.93:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer(props);

        while (true) {
            producer.send(cr(new Event("2025-01-17 18:14:10", "2020-01-17 CTT", "nanjing", "uid-1" + UUID.randomUUID().toString(), 1)));


            // Thread.sleep(1);


        }

        // producer.close();


    }



    public static ProducerRecord cr(Event event) {
        String content = JSON.toJSONString(event);
        return new ProducerRecord("bigdata-rtc-olap-test", content);
    }

}