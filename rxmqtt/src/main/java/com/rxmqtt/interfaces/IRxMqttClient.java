package com.rxmqtt.interfaces;

import com.rxmqtt.implementation.RxMqttClientStatus;
import com.rxmqtt.implementation.RxMqttMessage;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import rx.Observable;

public interface IRxMqttClient {
  Observable<IMqttToken> connect();

  Observable<IMqttToken> disconnect();

  void disconnectForcibly();

  Observable<RxMqttMessage> subscribeTopic(String topic, int qos);

  Observable<IMqttToken> publish(String topic, byte[] msg);

  Observable<RxMqttClientStatus> statusReport();

  Observable<IMqttToken> checkPing(Object userContext);
}
