package com.rxmqtt;

import android.util.Log;
import com.rxmqtt.exceptions.RxMqttTokenException;
import com.rxmqtt.models.RxMqttClientStatus;
import com.rxmqtt.models.RxMqttMessage;
import com.rxmqtt.models.RxMqttWill;
import com.rxmqtt.models.enums.RxMqttClientState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

class RxMqttClientImpl implements RxMqttClient {
  private MqttConnectOptions conOpt;
  private MqttAsyncClient client;

  private Hashtable<String, List<PublishSubject<RxMqttMessage>>> subjectHashtable =
      new Hashtable<>();
  private BehaviorSubject<RxMqttClientStatus> clientStatusSubject;
  private RxMqttClientStatus rxMqttClientStatus;
  private BehaviorSubject<IMqttToken> connectSubject;

  RxMqttClientImpl(String brokerUrl, String clientId) throws MqttException {
    super();
    rxMqttClientStatus = new RxMqttClientStatus();
    clientStatusSubject = BehaviorSubject.create();
    connectSubject = BehaviorSubject.create();
    conOpt = new MqttConnectOptions();
    client = new MqttAsyncClient(brokerUrl, clientId, new MemoryPersistence());
    updateState(RxMqttClientState.INIT);
    setMQTTCallback();
  }

  RxMqttClientImpl(String brokerUrl, String clientId, String username, String password,
      RxMqttWill rxMqttWill)
      throws MqttException {
    rxMqttClientStatus = new RxMqttClientStatus();
    conOpt = new MqttConnectOptions();
    conOpt.setPassword(password.toCharArray());
    conOpt.setUserName(username);
    if (rxMqttWill != null) {
      conOpt.setWill(rxMqttWill.getTopic(), rxMqttWill.getPayload(), rxMqttWill.getQos(),
          rxMqttWill.isRetained());
    }

    client = new MqttAsyncClient(brokerUrl, clientId, new MemoryPersistence());
    clientStatusSubject = BehaviorSubject.create();
    connectSubject = BehaviorSubject.create();
    updateState(RxMqttClientState.INIT);
    setMQTTCallback();
  }

  @Override public Observable<IMqttToken> connect() {
    if (client == null) {
      updateState(RxMqttClientState.CONNECTING_FAILED);
      connectSubject.onError(new IllegalStateException("MQTT Client initialization failed"));
    }

    if (client.isConnected()) {
      connectSubject.last();
      updateState(RxMqttClientState.CONNECTED);
    } else if (rxMqttClientStatus.getState() != RxMqttClientState.CONNECTING
        && rxMqttClientStatus.getState() != RxMqttClientState.CONNECTED) {

      try {
        updateState(RxMqttClientState.CONNECTING);
        client.connect(this.getConOpt(), "Context", new IMqttActionListener() {
          @Override public void onSuccess(IMqttToken asyncActionToken) {
            updateState(RxMqttClientState.CONNECTED);
            connectSubject.onNext(asyncActionToken);
          }

          @Override public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            updateState(RxMqttClientState.CONNECTING_FAILED);
            connectSubject.onError(new RxMqttTokenException(exception, asyncActionToken));
          }
        });
      } catch (MqttException ex) {
        updateState(RxMqttClientState.CONNECTING_FAILED);
        connectSubject.onError(ex);
      }
    }

    return connectSubject;
  }

  @Override
  public Observable<IMqttToken> disconnect() {

    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (client != null && client.isConnected()) {
          try {
            updateState(RxMqttClientState.TRY_DISCONNECT);
            client.disconnect("Context", new IMqttActionListener() {
              @Override
              public void onSuccess(IMqttToken asyncActionToken) {
                updateState(RxMqttClientState.DISCONNECTED);
                subscriber.onNext(asyncActionToken);
                subscriber.onCompleted();
                subjectHashtable.clear();
              }

              @Override
              public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                updateState(RxMqttClientState.DISCONNECTED);
                subscriber.onError(new RxMqttTokenException(exception, asyncActionToken));
              }
            });
          } catch (MqttException e) {
            updateState(RxMqttClientState.DISCONNECTED);
            subscriber.onError(e);
          }
        } else {
          subscriber.onCompleted();
        }
      }
    });
  }

  @Override
  public Observable<IMqttToken> publish(final String topic, final byte[] msg) {
    final MqttMessage message = new MqttMessage();
    message.setQos(1);
    message.setPayload(msg);

    Log.e("LOGLOG", "sending message topic: " + topic);

    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (null == client) {
          subscriber.onError(new IllegalStateException(""));
        }

        try {
          client.publish(topic, message, "Context", new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
              Log.e("LOGLOG", "success: " + topic);
              subscriber.onNext(asyncActionToken);
              subscriber.onCompleted();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
              subscriber.onError(new RxMqttTokenException(exception, asyncActionToken));
            }
          });
        } catch (MqttException ex) {
          subscriber.onError(ex);
        }
      }
    });
  }

  @Override public Observable<RxMqttClientStatus> statusReport() {
    return clientStatusSubject;
  }

  @Override
  public Observable<RxMqttMessage> subscribeTopic(final String topic, final int qos) {
    return subscribing(topic)
        .doOnSubscribe(new Action0() {
          @Override public void call() {
            if (client == null) {
              throw new IllegalStateException("MQTT Client initialization failed");
            }

            try {
              client.subscribe(topic, qos, "Context", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                  throw new RxMqttTokenException(exception, asyncActionToken);
                }
              });
            } catch (MqttException e) {
              throw new RxMqttTokenException(e, null);
            }
          }
        });
  }

  @Override
  public Observable<RxMqttMessage> subscribeTopic(final String[] topics, final int[] qos) {
    return subscribing(topics)
        .doOnSubscribe(new Action0() {
          @Override public void call() {
            if (client == null) {
              throw new IllegalStateException("MQTT Client initialization failed");
            }

            try {
              client.subscribe(topics, qos, "Context", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                  throw new RxMqttTokenException(exception, asyncActionToken);
                }
              });
            } catch (MqttException e) {
              throw new RxMqttTokenException(e, null);
            }
          }
        });
  }

  @Override public Observable<IMqttToken> unsubscribeTopic(final String topic) {
    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override public void call(Subscriber<? super IMqttToken> subscriber) {
        if (client != null && client.isConnected()) {
          try {
            client.unsubscribe(topic);
          } catch (MqttException mqttException) {
            subscriber.onError(mqttException);
          }
        }
      }
    });
  }

  @Override
  public void disconnectForcibly() {
    try {
      client.disconnectForcibly();
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Observable<IMqttToken> checkPing(final Object userContext) {
    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (!subscriber.isUnsubscribed()) {
          try {
            IMqttToken mqttToken = client.checkPing(userContext, null);
            subscriber.onNext(mqttToken);
            subscriber.onCompleted();
          } catch (MqttException e) {
            subscriber.onError(new RxMqttTokenException(e, null));
          }
        }
      }
    });
  }

  public MqttConnectOptions getConOpt() {
    return conOpt;
  }

  public void setConOpt(MqttConnectOptions conOpt) {
    this.conOpt = conOpt;
  }

  private void updateState(RxMqttClientState clientState) {
    rxMqttClientStatus.setLogTime(System.currentTimeMillis());
    rxMqttClientStatus.setState(clientState);
    clientStatusSubject.onNext(rxMqttClientStatus);
  }

  private synchronized Observable<RxMqttMessage> subscribing(String topic) {
    PublishSubject<RxMqttMessage> subject = PublishSubject.create();
    if (subjectHashtable.containsKey(topic)) {
      subjectHashtable.get(topic).add(subject);
    } else {
      subjectHashtable.put(topic, new ArrayList<>(Collections.singletonList(subject)));
    }
    return subject;
  }

  private synchronized Observable<RxMqttMessage> subscribing(String[] topics) {
    PublishSubject<RxMqttMessage> subject = PublishSubject.create();
    for (String topic : topics) {
      if (subjectHashtable.containsKey(topic)) {
        subjectHashtable.get(topic).add(subject);
      } else {
        subjectHashtable.put(topic, new ArrayList<>(Collections.singletonList(subject)));
      }
    }
    return subject;
  }

  private synchronized void setMQTTCallback() {
    client.setCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable cause) {
        updateState(RxMqttClientState.CONNECTION_LOST);
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) {
        if (message.getPayload().length != 0 && subjectHashtable.containsKey(topic)) {
          List<PublishSubject<RxMqttMessage>> subjects = subjectHashtable.get(topic);
          for (PublishSubject<RxMqttMessage> publishSubject : subjects) {
            publishSubject.onNext(new RxMqttMessage(topic, message));
          }
        }
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken token) {

      }
    });
  }
}
