package com.rxmqtt.models.enums;

public enum RxMqttClientState {
  INIT(0),
  CONNECTING(1),
  CONNECTING_FAILED(2),
  CONNECTED(3),
  CONNECTION_LOST(4),
  TRY_DISCONNECT(5),
  DISCONNECTED(6);

  private int value;

  RxMqttClientState(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
