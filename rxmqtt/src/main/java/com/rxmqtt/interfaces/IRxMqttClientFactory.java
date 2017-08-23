package com.rxmqtt.interfaces;

import com.rxmqtt.enums.ClientType;
import com.rxmqtt.exceptions.RxMqttException;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;

public interface IRxMqttClientFactory {
  IRxMqttClient create(String host, Integer port, String clientId, Boolean useSSL,
      ClientType type)
      throws RxMqttException;
}
