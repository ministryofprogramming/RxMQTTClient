package com.rxmqtt.exceptions;

import org.eclipse.paho.client.mqttv3.IMqttToken;

public class RxMqttTokenException extends RuntimeException {
  private IMqttToken mqttToken;

  public RxMqttTokenException(Throwable cause, IMqttToken mqttToken) {
    super(cause);
    this.mqttToken = mqttToken;
  }

  public IMqttToken getMqttToken() {
    return mqttToken;
  }

  public void setMqttToken(IMqttToken mqttToken) {
    this.mqttToken = mqttToken;
  }
}
