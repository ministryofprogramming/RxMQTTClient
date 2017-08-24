package com.rxmqtt.models;

import com.rxmqtt.models.enums.RxMqttClientState;

public class RxMqttClientStatus {
  private long logTime;
  private RxMqttClientState state;

  public RxMqttClientStatus() {
    this.state = RxMqttClientState.INIT;
  }

  public long getLogTime() {
    return logTime;
  }

  public void setLogTime(long logTime) {
    this.logTime = logTime;
  }

  public RxMqttClientState getState() {
    return state;
  }

  public void setState(RxMqttClientState state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return String.format("time:%sm state:%s", getLogTime(), getState());
  }
}
