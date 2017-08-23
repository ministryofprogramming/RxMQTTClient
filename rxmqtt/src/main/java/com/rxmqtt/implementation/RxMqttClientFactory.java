package com.rxmqtt.implementation;

import com.rxmqtt.BuildConfig;
import com.rxmqtt.enums.ClientType;
import com.rxmqtt.exceptions.RxMqttException;
import com.rxmqtt.interfaces.IRxMqttClient;
import com.rxmqtt.interfaces.IRxMqttClientFactory;

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
        throw new UnsupportedOperationException();
      }
    }
    return client;
  }

  private String getBrokerUrl(String host, int port, Boolean useSSL) {
    return String.format("%s://%s:%d", useSSL ? BuildConfig.SSL : BuildConfig.TCP, host, port);
  }
}
