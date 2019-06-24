package com.rxmqtt.models;

public class RxMqttUserStatusPayload {
  private long user_id;
  private String username;
  private String device_id;
  private String platform;
  private boolean is_online;
  private String client_id;

  public RxMqttUserStatusPayload(long user_id, String username, String device_id,
      String platform, boolean is_online, String client_id) {
    this.user_id = user_id;
    this.username = username;
    this.device_id = device_id;
    this.platform = platform;
    this.is_online = is_online;
    this.client_id = client_id;
  }

  public long getUser_id() {
    return user_id;
  }

  public RxMqttUserStatusPayload setUser_id(long user_id) {
    this.user_id = user_id;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public RxMqttUserStatusPayload setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getDevice_id() {
    return device_id;
  }

  public RxMqttUserStatusPayload setDevice_id(String device_id) {
    this.device_id = device_id;
    return this;
  }

  public String getPlatform() {
    return platform;
  }

  public RxMqttUserStatusPayload setPlatform(String platform) {
    this.platform = platform;
    return this;
  }

  public boolean isIs_online() {
    return is_online;
  }

  public RxMqttUserStatusPayload setIs_online(boolean is_online) {
    this.is_online = is_online;
    return this;
  }

  public String getClient_id() {
    return client_id;
  }

  public RxMqttUserStatusPayload setClient_id(String client_id) {
    this.client_id = client_id;
    return this;
  }
}