package com.druid.test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Event {

    private transient DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private long timestamp;
    private String country;
    private String city;
    private String uid;
    private int age;

    public Event(String timestamp, String country, String city, String uid, int age) {

        LocalDateTime dateTime = LocalDateTime.parse(timestamp, dtf2);
        this.timestamp = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        System.out.println(this.timestamp);
        System.out.println(System.currentTimeMillis());

        this.country = country;
        this.city = city;
        this.uid = uid;
        this.age = age;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
