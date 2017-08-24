package com.rxmqtt;

import com.rxmqtt.models.RxMqttClientStatus;
import com.rxmqtt.models.RxMqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import rx.Observable;

public interface RxMqttClient {
  Observable<IMqttToken> connect();

  Observable<IMqttToken> disconnect();

  void disconnectForcibly();

  Observable<RxMqttMessage> subscribeTopic(String topic, int qos);

  Observable<RxMqttMessage> subscribeTopic(String[] topics, int[] qos);

  Observable<IMqttToken> publish(String topic, byte[] msg);

  Observable<RxMqttClientStatus> statusReport();

  Observable<IMqttToken> checkPing(Object userContext);
}
