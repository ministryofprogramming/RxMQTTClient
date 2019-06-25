package com.rxmqtt.models;

public class RxMqttWill {
  String topic;
  byte[] payload;
  int qos;
  boolean retained;

  public RxMqttWill(String topic, byte[] payload, int qos, boolean retained) {
    this.topic = topic;
    this.payload = payload;
    this.qos = qos;
    this.retained = retained;
  }

  public String getTopic() {
    return topic;
  }

  public byte[] getPayload() {
    return payload;
  }

  public int getQos() {
    return qos;
  }

  public boolean isRetained() {
    return retained;
  }
}
