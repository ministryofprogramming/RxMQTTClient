package com.rxmqtt.exceptions;

import com.rxmqtt.enums.RxMqttExceptionType;

public class RxMqttException extends Throwable {
  private RxMqttExceptionType type;

  public RxMqttException() {
  }

  public RxMqttException(RxMqttExceptionType type) {
    this.setType(type);
  }

  public RxMqttException(RxMqttExceptionType type, Throwable cause) {
    super(cause);
    this.type = type;
  }

  public RxMqttExceptionType getType() {
    return type;
  }

  public void setType(RxMqttExceptionType type) {
    this.type = type;
  }
}
