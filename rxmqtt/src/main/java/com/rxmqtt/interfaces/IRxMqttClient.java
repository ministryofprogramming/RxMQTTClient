package com.rxmqtt.interfaces;

import com.rxmqtt.implementation.RxMqttClientStatus;
import com.rxmqtt.implementation.RxMqttMessage;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import rx.Observable;

public interface IRxMqttClient {
  public Observable<IMqttToken> connect();

  public Observable<IMqttToken> disconnect();

  public void disconnectForcibly();

  public Observable<IMqttToken> subscribeTopic(String topic, int qos);

  public Observable<RxMqttMessage> subscribing(String regularExpression);

  public Observable<RxMqttMessage> subscribing(Pattern pattern);

  public Observable<IMqttToken> publish(String topic, byte[] msg);

  public Observable<RxMqttClientStatus> statusReport();

  public Observable<IMqttToken> checkPing(Object userContext);
}
