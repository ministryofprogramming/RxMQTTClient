package com.rxmqtt.interfaces;

public interface IRxMqttMessage {
  String getTopic();

  String getMessage();

  int getQos();
}
