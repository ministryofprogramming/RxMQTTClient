package com.rxmqtt.models;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.util.Arrays;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RxMqttMessage {
  private String topic;
  private MqttMessage rxMessage;
  private String decrypted_payload = null;

  public RxMqttMessage(String topic, MqttMessage rxMessage) {
    this.topic = topic;
    this.rxMessage = rxMessage;
  }

  public String getTopic() {
    return topic;
  }

  public String getMessage() {
    return null == rxMessage ? null : (decrypted_payload != null ? decrypted_payload : new String(rxMessage.getPayload()));
  }

  public String decrypt(Cipher dcipher, SecretKey key) {
    try {
      byte[] payload = rxMessage.getPayload();
      dcipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(payload, 0, 16)));
      decrypted_payload = new String(dcipher.doFinal(Arrays.copyOfRange(payload, 16, payload.length)));
      return decrypted_payload;
    } catch (Exception e) {
      return null;
    }
  }

  public int getQos() {
    return null == rxMessage ? -1 : rxMessage.getQos();
  }

  @Override
  public String toString() {
    return String.format("topic: %s; msg: %s; qos: %d", getTopic(), getMessage(), getQos());
  }
}
