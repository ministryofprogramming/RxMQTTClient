package com.rxmqtt.implementation;

import com.rxmqtt.enums.RxMqttClientState;
import java.security.Timestamp;

public class RxMqttClientStatus implements Cloneable {
  private long logTime;
  private RxMqttClientState state;

  public RxMqttClientStatus() {
    this.state = RxMqttClientState.Init;
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

  @Override
  protected Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

}
