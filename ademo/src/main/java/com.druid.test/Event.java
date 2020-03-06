package com.druid.test;

import java.util.Random;

public class Event {

    private String timestamp;
    private String uid;
    private int age;
    private int seq;

    public Event(String timestamp, int seq) {

        this.timestamp = timestamp;
        int i = new Random().nextInt(50);

        // i = 66;

        this.uid = "uid-" + i;
        this.age = i;

        this.seq = seq;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
