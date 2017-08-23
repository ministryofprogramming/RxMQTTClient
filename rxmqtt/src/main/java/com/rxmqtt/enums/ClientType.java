package com.rxmqtt.enums;

public enum ClientType {
  Async(0),
  Wait(1);

  private int code;

  ClientType(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
