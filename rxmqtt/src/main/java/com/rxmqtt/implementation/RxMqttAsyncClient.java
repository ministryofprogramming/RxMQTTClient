package com.rxmqtt.implementation;

import com.rxmqtt.enums.RxMqttClientState;
import com.rxmqtt.enums.RxMqttExceptionType;
import com.rxmqtt.exceptions.RxMqttException;
import com.rxmqtt.exceptions.RxMqttTokenException;
import com.rxmqtt.interfaces.IRxMqttClient;
import java.util.Hashtable;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.subjects.PublishSubject;

public class RxMqttAsyncClient implements IRxMqttClient {
  private MqttConnectOptions conOpt = new MqttConnectOptions();
  private MqttAsyncClient client;
  private Hashtable<String, Pattern> patternHashtable;
  private Hashtable<String, PublishSubject<RxMqttMessage>> subjectHashtable;


  public RxMqttAsyncClient(String brokerUrl, String clientId)
      throws RxMqttException {
    super();
    try {
      client = new MqttAsyncClient(brokerUrl, clientId, new MemoryPersistence());
    } catch (MqttException ex) {
      throw new RxMqttException(RxMqttExceptionType.CLIENT_INIT_ERROR);
    }
  }

  private void connect(final Subscriber<? super IMqttToken> subscriber) {
    try {
      client.connect(this.getConOpt(), "Context", new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          //updateState(RxMqttClientState.Connected);
          subscriber.onNext(asyncActionToken);
          subscriber.onCompleted();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          //updateState(RxMqttClientState.ConnectingFailed);
          subscriber.onError(new RxMqttTokenException(exception, asyncActionToken));
        }
      });
    } catch (MqttException ex) {
      //updateState(RxMqttClientState.ConnectingFailed);
      subscriber.onError(ex);
    }
  }

  @Override
  public Observable<IMqttToken> connect() {
    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (null == client) {
          //updateState(RxMqttClientState.ConnectingFailed);
          subscriber.onError(new
              RxMqttException(RxMqttExceptionType.CLIENT_NULL_ERROR));
        }

        disconnect().subscribe(new Observer<IMqttToken>() {
          @Override
          public void onCompleted() {
            connect(subscriber);
          }

          @Override
          public void onError(Throwable e) {
            connect(subscriber);
          }

          @Override
          public void onNext(IMqttToken iMqttToken) {

          }
        });
      }
    });
  }

  @Override
  public Observable<IMqttToken> disconnect() {
    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (client != null && client.isConnected()) {
          try {
            //updateState(RxMqttClientState.TryDisconnect);
            client.disconnect("Context", new IMqttActionListener() {
              @Override
              public void onSuccess(IMqttToken asyncActionToken) {
                //updateState(RxMqttClientState.Disconnected);
                subscriber.onNext(asyncActionToken);
                subscriber.onCompleted();
              }

              @Override
              public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                //updateState(RxMqttClientState.Disconnected);
                subscriber.onError(new RxMqttTokenException(exception, asyncActionToken));
              }
            });
          } catch (MqttException e) {
            //updateState(RxMqttClientState.Disconnected);
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

    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (null == client) subscriber.onError(new
            RxMqttException(RxMqttExceptionType.CLIENT_NULL_ERROR));

        try {
          client.publish(topic, message, "Context", new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
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
    return null;
  }

  @Override
  public Observable<IMqttToken> subscribeTopic(final String topic, final int qos) {
    return Observable.create(new Observable.OnSubscribe<IMqttToken>() {
      @Override
      public void call(final Subscriber<? super IMqttToken> subscriber) {
        if (null == client) subscriber.onError(new
            RxMqttException(RxMqttExceptionType.CLIENT_NULL_ERROR));

        try {
          client.subscribe(topic, qos, "Context", new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
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

  @Override
  public Observable<RxMqttMessage> subscribing(String regularExpression) {
    return subscribing(Pattern.compile(regularExpression));
  }

  @Override
  public synchronized Observable<RxMqttMessage> subscribing(final Pattern pattern) {
    if (null == patternHashtable && null == subjectHashtable) {
      patternHashtable = new Hashtable<>();
      subjectHashtable = new Hashtable<>();

      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          //updateState(RxMqttClientState.ConnectionLost);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
          if (message.getPayload().length != 0) {
            for (String key : patternHashtable.keySet()) {
              if (patternHashtable.get(key).matcher(topic).matches()) {
                subjectHashtable.get(key).onNext(new RxMqttMessage(topic, message));
              }
            }
          }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
      });
    }
    if (patternHashtable.containsKey(pattern.pattern())) {
      return subjectHashtable.get(pattern.pattern());
    } else {
      patternHashtable.put(pattern.pattern(), pattern);
      PublishSubject<RxMqttMessage> subject = PublishSubject.create();
      subjectHashtable.put(pattern.pattern(), subject);
      return subjectHashtable.get(pattern.pattern());
    }
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
}
