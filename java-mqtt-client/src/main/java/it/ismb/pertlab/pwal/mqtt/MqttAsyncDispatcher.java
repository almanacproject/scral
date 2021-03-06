/*
 * PWAL - MQTT Asynchronous Dispatcher
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.mqtt;

import it.ismb.pertlab.pwal.mqtt.tasks.ReconnectionTimerTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Timer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class aimed at offering an easy interface to asynchronously
 * publishing data towards a given MQTT broker, with the desired QoS level. It
 * exploits a simple persistence layer based on temporary files.
 * 
 * @author bonino
 *
 */
public class MqttAsyncDispatcher implements MqttCallback
{
	// reference example:
	// http://git.eclipse.org/c/paho/org.eclipse.paho.mqtt.java.git/tree/org.eclipse.paho.sample.mqttv3app/src/main/java/org/eclipse/paho/sample/mqttv3app/SampleAsyncCallBack.java
	
	// the default MQTT QoS (deliver and forget)
	private static final MqttQos DEFAULT_QOS = MqttQos.AT_MOST_ONCE;
	
	// the default reconnection timeout
	private static final int RECONNECTION_TIMEOUT = 30000;
	
	// the class-level logger
	private Logger logger;
	
	// the MQTT asynchronous client
	private MqttAsyncClient asyncClient;
	
	// the MQTT connection options
	private MqttConnectOptions connectionOptions;
	
	// the MQTT broker endpoint as full url
	private String brokerUrl;
	
	// this client id
	private String clientId;
	
	// the username to use when setting up the connection
	private String username;
	
	// the password to use when setting up the connection
	private String password;
	
	// the client key file path
	private String clientKeyFilePath;
	
	// the server trust store file path
	private String trustStoreFilePath;
	
	// the client key passphrase
	private String clientKeyPassphrase;
	
	// the connection flag
	private boolean connected;
	
	// the timer for reconnection
	private Timer reconnectionTimer;
	
	// the reconnection flag
	private boolean autoReconnect;
	
	/**
	 * The class constructor. Builds a new instance of
	 * {@link MqttAsyncDispatcher} pointing to the given brokerUrl and adopting
	 * the given username and password for connecting to the broker. It uses the
	 * default Quality of Service (QoS) defined in {@link MqttAsyncDispatcher}
	 * .DEFAULT_QOS or the QoS level defined through the instance setQoS method.
	 * Moreover it does not persist sessions across restarts, i.e., it does not
	 * provide durable subscriptions
	 * 
	 * Currently only "plain" connection is supported although future release
	 * may support ssl-encrypted connections.
	 * 
	 * @param brokerUrl
	 *            The full url of the broker, as a {String}.
	 * @param clientId
	 *            The client id to connect with.
	 * @param username
	 *            The username to adopt for connecting to the broker.
	 * @param password
	 *            The password to adopt for connecting to the broker.
	 */
	public MqttAsyncDispatcher(String brokerUrl, String clientId, String username, String password)
	{
		this.initCommon(brokerUrl, clientId, username, password, true, null, null, null);
	}
	
	/**
	 * The class constructor. Builds a new instance of
	 * {@link MqttAsyncDispatcher} pointing to the given brokerUrl and adopting
	 * the given username and password for connecting to the broker. It uses the
	 * default Quality of Service (QoS) defined in {@link MqttAsyncDispatcher}
	 * .DEFAULT_QOS or the QoS level defined through the instance setQoS method.
	 * Moreover it does not persist sessions across restarts, i.e., it does not
	 * provide durable subscriptions. It supports secure SSL connection to the
	 * broker, given that a client certificate file and server trust store are
	 * provided.
	 * 
	 * @param brokerUrl
	 *            The full url of the broker, as a {String}.
	 * @param clientId
	 *            The client id to connect with.
	 * @param username
	 *            The username to adopt for connecting to the broker.
	 * @param password
	 *            The password to adopt for connecting to the broker.
	 * @param clientKeyPath
	 * 			  The path to the client certificate file
	 * @param trustStorePath
	 * 			The path to the trust store hosting the server certificates
	 * @param passphrase
	 * 			The passphrase of the client key
	 */
	public MqttAsyncDispatcher(String brokerUrl, String clientId, String username, String password,
			String clientKeyPath, String trustStorePath, String passphrase)
	{
		this.initCommon(brokerUrl, clientId, username, password, true, clientKeyPath, trustStorePath, passphrase);
	}
	
	/**
	 * The class constructor. Builds a new instance of
	 * {@link MqttAsyncDispatcher} pointing to the given brokerUrl and adopting
	 * the given username and password for connecting to the broker. It uses the
	 * default Quality of Service (QoS) defined in {@link MqttAsyncDispatcher}
	 * .DEFAULT_QOS or the QoS level defined through the instance setQoS method.
	 * Moreover it does not persist sessions across restarts, i.e., it does not
	 * provide durable subscriptions
	 * 
	 * Currently only "plain" connection is supported although future release
	 * may support ssl-encrypted connections.
	 * 
	 * @param brokerUrl
	 *            The full url of the broker, as a {String}.
	 * @param clientId
	 *            The client id to connect with.
	 * @param username
	 *            The username to adopt for connecting to the broker.
	 * @param password
	 *            The password to adopt for connecting to the broker.
	 * @param autoReconnect
	 *            The auto-reconnection mode, is true the client automatically
	 *            attempts re-connection.
	 */
	public MqttAsyncDispatcher(String brokerUrl, String clientId, String username, String password,
			boolean autoReconnect)
	{
		this.initCommon(brokerUrl, clientId, username, password, autoReconnect, null, null, null);
	}
	
	
	/**
	 * The class constructor. Builds a new instance of
	 * {@link MqttAsyncDispatcher} pointing to the given brokerUrl and adopting
	 * the given username and password for connecting to the broker. It uses the
	 * default Quality of Service (QoS) defined in {@link MqttAsyncDispatcher}
	 * .DEFAULT_QOS or the QoS level defined through the instance setQoS method.
	 * Moreover it does not persist sessions across restarts, i.e., it does not
	 * provide durable subscriptions. It supports secure SSL connection to the
	 * broker, given that a client certificate file and server trust store are
	 * provided.
	 * 
	 * @param brokerUrl
	 *            The full url of the broker, as a {String}.
	 * @param clientId
	 *            The client id to connect with.
	 * @param username
	 *            The username to adopt for connecting to the broker.
	 * @param password
	 *            The password to adopt for connecting to the broker.
	 * @param autoReconnect
	 *            The auto-reconnection mode, is true the client automatically
	 *            attempts re-connection.
	 * @param clientKeyPath
	 * 			  The path to the client certificate file
	 * @param trustStorePath
	 * 			The path to the trust store hosting the server certificates
	 * @param passphrase
	 * 			The passphrase of the client key
	 */
	public MqttAsyncDispatcher(String brokerUrl, String clientId, String username, String password,
			boolean autoReconnect, String clientKeyPath, String trustStorePath, String passphrase)
	{
		this.initCommon(brokerUrl, clientId, username, password, autoReconnect, clientKeyPath, trustStorePath,
				passphrase);
	}
	
	private void initCommon(String brokerUrl, String clientId, String username, String password, boolean autoReconnect,
			String clientKeyPath, String trustStorePath, String passphrase)
	{
		this.brokerUrl = brokerUrl;
		this.clientId = clientId;
		this.username = username;
		this.password = password;
		this.clientKeyFilePath = clientKeyPath;
		this.trustStoreFilePath = trustStorePath;
		this.clientKeyPassphrase = passphrase;
		this.logger = LoggerFactory.getLogger(this.getClass());
		
		// initialize the reconnection timer
		this.reconnectionTimer = new Timer();
		
		// store the auto-reconnection flag
		this.autoReconnect = autoReconnect;
		
		// initially disconnected
		this.connected = false;
		
		// get the default Java temporary files directory
		String tmpDir = System.getProperty("java.io.tmpdir");
		
		// prepare a simple persistence layer that stores data as files in the
		// temporary directory
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
		
		// build the connection options
		this.connectionOptions = new MqttConnectOptions();
		
		// check if ssl
		if (this.brokerUrl.startsWith("ssl://"))
		{
			// check the client key location and truststore location
			if ((this.clientKeyFilePath != null) && (!this.clientKeyFilePath.isEmpty())
					&& (this.trustStoreFilePath != null) && (!this.trustStoreFilePath.isEmpty()))
			{
				
				// client key
				KeyStore ks;
				try
				{
					// create an instance of keystore
					// TODO: check whether keystore type shall be configurable
					ks = KeyStore.getInstance("PKCS12");
					
					// load the client key and unlock it using the passphrase
					ks.load(new FileInputStream(this.clientKeyFilePath), this.clientKeyPassphrase.toCharArray());
					
					// get a key manager factory instance
					KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
					
					// initialize the key manager
					kmf.init(ks, this.clientKeyPassphrase.toCharArray());
					
					// server certificate
					KeyStore tks = KeyStore.getInstance("JKS");
					
					// created the key store with
					// keytool -importcert -alias rmq -file
					// ./server_certificate.pem -keystore ./jvm_keystore
					tks.load(new FileInputStream(this.trustStoreFilePath), this.clientKeyPassphrase.toCharArray());
					
					// set-up a trust manager factory instance and initialize it
					// with the keystore instance holding the server
					// certificates.
					TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
					tmf.init(tks);
					
					// create SSL context for sending requests
					SSLContext ctx = SSLContext.getInstance("SSLv3");
					ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
					
					// set the socket factory used by the mqtt client
					this.connectionOptions.setSocketFactory(ctx.getSocketFactory());
				}
				catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException | CertificateException
						| IOException | UnrecoverableKeyException e)
				{
					this.logger.error("Error while setting up TLS connection to the broker", e);
				}
			}
		}
		
		// set the clean session parameters
		this.connectionOptions.setCleanSession(true);
		
		// set the password, if provided
		if ((this.password != null) && (!this.password.isEmpty()))
		{
			this.connectionOptions.setPassword(this.password.toCharArray());
		}
		
		// set the username, if provided
		if ((this.username != null) && (!this.username.isEmpty()))
		{
			this.connectionOptions.setUserName(username);
		}
		
		// build the MqttClient instance
		try
		{
			// build the client instance
			this.asyncClient = new MqttAsyncClient(this.brokerUrl, this.clientId, dataStore);
			
			// set this call instance as callback for the asynchronous
			// delivery
			// task
			this.asyncClient.setCallback(this);
		}
		catch (MqttException e)
		{
			// log the error
			this.logger.error("Error while creating the MQTT asynchronous client", e);
		}
		
	}
	
	public void connectionLost(Throwable t)
	{
		// Called when the connection to the server has been lost.
		// An application may choose to implement reconnection
		// logic at this point. This preliminary implementation simply logs the
		// error.
		this.logger.warn("Lost connection with the given MQTT broker...Reconnecting in 30s", t);
		
		// set the connection status
		this.setConnected(false);
		
	}
	
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		// Called when a message has been delivered to the
		// server. The token passed in here is the same one
		// that was returned from the original call to publish.
		
		this.logger.info("Delivery complete callback: Publish Completed " + Arrays.toString(token.getTopics()));
		
	}
	
	public void messageArrived(String arg0, MqttMessage mqttMessage) throws Exception
	{
		// // Called when a message arrives from the server that matches any
		// subscription made by the client
		
		// empty in this case as this dispatcher only publishes data...
		this.logger.info("Unknow arg0: {}", arg0);
		this.logger.info("MqttMessage: ", new String(mqttMessage.getPayload()));
	}
	
	/**
	 * Checks if the dispatcher is currently connected or not
	 * 
	 * @return the current connection status
	 */
	public boolean isConnected()
	{
		return connected;
	}
	
	/**
	 * Sets the dispatcher status to either connected or not.
	 * 
	 * @param connected
	 *            the connection status to be set
	 */
	public void setConnected(boolean connected)
	{
		this.connected = connected;
		
		// if disconnected, and auto-reconnection is enabled, try to reconnect
		if ((!this.connected) && (this.autoReconnect))
		{
			// start the timer
			this.reconnectionTimer.schedule(new ReconnectionTimerTask(this, true),
					MqttAsyncDispatcher.RECONNECTION_TIMEOUT);
		}
	}
	
	/**
	 * Connect asynchronously to the given MQTT broker, when connected sets the
	 * connection flag at true;
	 * 
	 * The returned IMqttToken might be used to wrap the asynchronous call to a
	 * synchronous invocation.
	 * 
	 * @return The {@link IMqttToken} related to the connection request
	 */
	public IMqttToken connect()
	{
		IMqttToken token = null;
		try
		{
			token = this.asyncClient.connect(this.connectionOptions, "", new MqttConnectionListener(this));
		}
		catch (MqttException e)
		{
			this.logger.warn("Error while performing connection to the given MQTT broker", e);
		}
		
		return token;
	}
	
	/**
	 * Connect synchronously with the given MQTT broker
	 */
	public void syncConnect()
	{
		try
		{
			this.connect().waitForCompletion();
		}
		catch (MqttException e)
		{
			this.logger.warn("Error while performing synchronous connection to the given MQTT broker", e);
		}
	}
	
	/**
	 * Publishes the given message payload to the given topic using the
	 * class-level default QoS
	 * 
	 * @param topic
	 *            The topic to which the event should be published
	 * @param payload
	 *            The payload to publish
	 */
	public void publish(String topic, byte[] payload)
	{
		
		// asynchronously publishes the given payload to the given topic
		this.publish(topic, payload, MqttAsyncDispatcher.DEFAULT_QOS);
		
	}
	
	/**
	 * Publishes the given message payload to the given topic using the given
	 * QoS level
	 * 
	 * @param topic
	 *            The topic to which the event should be published
	 * @param payload
	 *            The payload to publish
	 * @param qos
	 *            The QoS to use
	 */
	public void publish(String topic, byte[] payload, MqttQos qos)
	{
		if (this.connected)
		{
			try
			{
				// asynchronously publishes the given payload to the given topic
				this.asyncClient.publish(topic, payload, qos.getQoS(), false);
			}
			catch (MqttException e)
			{
				// TODO can be checked in a more refined manner
				this.logger.warn("Error while delivering message", e);
			}
		}
	}
	
	/**
	 * Asynchronously disconnects from the given message broker when
	 * disconnected sets the connection flag at false;
	 * 
	 * The returned IMqttToken might be used to wrap the asynchronous call to a
	 * synchronous invocation.
	 * 
	 * @return The {@link IMqttToken} related to the connection request
	 */
	public IMqttToken disconnect()
	{
		IMqttToken token = null;
		
		try
		{
			token = this.asyncClient.disconnect("", new MqttDisconnectionListener(this));
		}
		catch (MqttException e)
		{
			this.logger.warn("Error while performing connection to the given MQTT broker", e);
		}
		
		return token;
	}
	
	/**
	 * Disconnects synchronously from the given MQTT broker
	 */
	public void syncDisconnect()
	{
		try
		{
			this.disconnect().waitForCompletion();
		}
		catch (MqttException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void subscribe(String topicFilter, int qos) // , IMqttActionListener
														// listener)
	{
		try
		{
			this.asyncClient.subscribe(topicFilter, qos, null, (IMqttActionListener) this);
		}
		catch (MqttException e)
		{
			this.logger.error("Mqtt subscribe exception: ", e);
		}
	}
	
}
