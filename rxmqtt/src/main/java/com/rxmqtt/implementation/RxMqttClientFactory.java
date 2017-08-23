package com.rxmqtt.implementation;

import android.provider.SyncStateContract;
import com.rxmqtt.Constants;
import com.rxmqtt.enums.ClientType;
import com.rxmqtt.exceptions.RxMqttException;
import com.rxmqtt.interfaces.IRxMqttClient;
import com.rxmqtt.interfaces.IRxMqttClientFactory;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;

public class RxMqttClientFactory implements IRxMqttClientFactory {
  @Override
  public IRxMqttClient create(String host, Integer port, String clientId, Boolean useSSL,
      ClientType type)
      throws RxMqttException {
    IRxMqttClient client = null;
    String brokerUrl = getBrokerUrl(host, port, useSSL);
    switch (type) {
      case Async: {
        client = new RxMqttAsyncClient(brokerUrl, clientId);
        break;
      }
      case Wait: {
        break;
      }
    }
    return client;
  }

  private String getBrokerUrl(String host, int port, Boolean useSSL) {
    return String.format("%s://%s:%d", useSSL ? Constants.SSL : Constants.TCP, host, port);
  }
}
