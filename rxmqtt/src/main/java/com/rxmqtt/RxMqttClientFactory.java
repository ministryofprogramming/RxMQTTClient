package com.rxmqtt;

import android.annotation.SuppressLint;
import org.eclipse.paho.client.mqttv3.MqttException;

public class RxMqttClientFactory {

  public RxMqttClient create(String host, Integer port, String clientId, Boolean useSSL) throws
      MqttException {
    return new RxMqttClientImpl(getBrokerUrl(host, port, useSSL), clientId);
  }

  public RxMqttClient create(String host, Integer port, String clientId, Boolean useSSL, String username, String password)
      throws MqttException {
    return new RxMqttClientImpl(getBrokerUrl(host, port, useSSL), clientId, username, password);
  }

  private String getBrokerUrl(String host, int port, Boolean useSSL) {
    return String.format("%s://%s:%d", useSSL ? BuildConfig.SSL : BuildConfig.TCP, host, port);
  }
}
