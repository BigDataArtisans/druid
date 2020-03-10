package com.druid.test;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
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
        props.put("bootstrap.servers", "10.101.52.38:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer(props);

        int seq = 591;

        while (true) {

            seq += 1;

            DateTime now = DateTime.now();

            // now = now.plusYears(2).plusSeconds(delta);
            // now = now.plusDays(1);

            String dataStr = now.toString("yyyy-MM-dd HH:mm:ss");
            dataStr += " CTT";

            producer.send(cr(new Event(dataStr, seq)));

            Thread.sleep(1000);

            System.out.println(seq);

        }

    }


    public static ProducerRecord cr(Event event) {
        String content = JSON.toJSONString(event);
        return new ProducerRecord("druid-test", content);
    }

}