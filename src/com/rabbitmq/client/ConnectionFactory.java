//   The contents of this file are subject to the Mozilla Public License
//   Version 1.1 (the "License"); you may not use this file except in
//   compliance with the License. You may obtain a copy of the License at
//   http://www.mozilla.org/MPL/
//
//   Software distributed under the License is distributed on an "AS IS"
//   basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//   License for the specific language governing rights and limitations
//   under the License.
//
//   The Original Code is RabbitMQ.
//
//   The Initial Developers of the Original Code are LShift Ltd,
//   Cohesive Financial Technologies LLC, and Rabbit Technologies Ltd.
//
//   Portions created before 22-Nov-2008 00:00:00 GMT by LShift Ltd,
//   Cohesive Financial Technologies LLC, or Rabbit Technologies Ltd
//   are Copyright (C) 2007-2008 LShift Ltd, Cohesive Financial
//   Technologies LLC, and Rabbit Technologies Ltd.
//
//   Portions created by LShift Ltd are Copyright (C) 2007-2009 LShift
//   Ltd. Portions created by Cohesive Financial Technologies LLC are
//   Copyright (C) 2007-2009 Cohesive Financial Technologies
//   LLC. Portions created by Rabbit Technologies Ltd are Copyright
//   (C) 2007-2009 Rabbit Technologies Ltd.
//
//   All Rights Reserved.
//
//   Contributor(s): ______________________________________.
//
package com.rabbitmq.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.rabbitmq.client.impl.AMQConnection;
import com.rabbitmq.client.impl.FrameHandler;
import com.rabbitmq.client.impl.SocketFrameHandler;

/**
 * Convenience "factory" class to facilitate opening a {@link Connection} to an AMQP broker.
 */

public class ConnectionFactory {
    /** Default user name */
    public static final String DEFAULT_USER = "guest";

    /** Default password */
    public static final String DEFAULT_PASS = "guest";

    /** Default virtual host */
    public static final String DEFAULT_VHOST = "/";

    /** Default value for the desired maximum channel number; zero for
     * unlimited */
    public static final int DEFAULT_CHANNEL_MAX = 0;

    /** Default value for the desired maximum frame size; zero for
     * unlimited */
    public static final int DEFAULT_FRAME_MAX = 0;

    /** Default value for desired heartbeat interval; zero for none */
    public static final int DEFAULT_HEARTBEAT = 0;

    private String _userName = DEFAULT_USER;
    private String _password = DEFAULT_PASS;
    private String _virtualHost = DEFAULT_VHOST;
    private int _requestedChannelMax = DEFAULT_CHANNEL_MAX;
    private int _requestedFrameMax = DEFAULT_FRAME_MAX;
    private int _requestedHeartbeat = DEFAULT_HEARTBEAT;

    /**
     * Retrieve the user name.
     * @return the AMQP user name to use when connecting to the broker
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * Set the user name.
     * @param userName the AMQP user name to use when connecting to the broker
     */
    public void setUsername(String userName) {
        _userName = userName;
    }

    /**
     * Retrieve the password.
     * @return the password to use when connecting to the broker
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Set the password.
     * @param password the password to use when connecting to the broker
     */
    public void setPassword(String password) {
        _password = password;
    }

    /**
     * Retrieve the virtual host.
     * @return the virtual host to use when connecting to the broker
     */
    public String getVirtualHost() {
        return _virtualHost;
    }

    /**
     * Set the virtual host.
     * @param virtualHost the virtual host to use when connecting to the broker
     */
    public void setVirtualHost(String virtualHost) {
        _virtualHost = virtualHost;
    }

    /**
     * Retrieve the requested maximum channel number
     * @return the initially requested maximum channel number; zero for unlimited
     */
    public int getRequestedChannelMax() {
        return _requestedChannelMax;
    }

    /**
     * Set the requested maximum frame size
     * @param requestedFrameMax initially requested maximum frame size, in octets; zero for unlimited
     */
    public void setRequestedFrameMax(int requestedFrameMax) {
        _requestedFrameMax = requestedFrameMax;
    }

    /**
     * Retrieve the requested maximum frame size
     * @return the initially requested maximum frame size, in octets; zero for unlimited
     */
    public int getRequestedFrameMax() {
        return _requestedFrameMax;
    }

    /**
     * Retrieve the requested heartbeat interval.
     * @return the initially requested heartbeat interval, in seconds; zero for none
     */
    public int getRequestedHeartbeat() {
        return _requestedHeartbeat;
    }

    /**
     * Set the requested heartbeat.
     * @param requestedHeartbeat the initially requested heartbeat interval, in seconds; zero for none
     */
    public void setRequestedHeartbeat(int requestedHeartbeat) {
        _requestedHeartbeat = requestedHeartbeat;
    }

    /**
     * Set the requested maximum channel number
     * @param requestedChannelMax initially requested maximum channel number; zero for unlimited
     */
    public void setRequestedChannelMax(int requestedChannelMax) {
        _requestedChannelMax = requestedChannelMax;
    }
                                          
    /**
     * Holds the SocketFactory used to manufacture outbound sockets.
     */
    private SocketFactory _factory = SocketFactory.getDefault();
    
    /**
     * Instantiate a ConnectionFactory with a default set of parameters.
     */
    public ConnectionFactory() {
    }

    /**
     * Retrieve the socket factory used to make connections with.
     */
    public SocketFactory getSocketFactory() {
        return _factory;
    }

    /**
     * Set the socket factory used to make connections with. Can be
     * used to enable SSL connections by passing in a
     * javax.net.ssl.SSLSocketFactory instance.
     *
     * @see #useSslProtocol
     */
    public void setSocketFactory(SocketFactory factory) {
        _factory = factory;
    }

    /**
     * Convenience method for setting up a SSL socket factory, using
     * the DEFAULT_SSL_PROTOCOL and a trusting TrustManager.
     */
    public void useSslProtocol()
        throws NoSuchAlgorithmException, KeyManagementException
    {
        useSslProtocol(DEFAULT_SSL_PROTOCOL);
    }

    /**
     * Convenience method for setting up a SSL socket factory, using
     * the DEFAULT_SSL_PROTOCOL and a trusting TrustManager.
     */
    public void useSslProtocol(String protocol)
        throws NoSuchAlgorithmException, KeyManagementException
    {
        useSslProtocol(protocol, new NullTrustManager());
    }

    /**
     * Convenience method for setting up an SSL socket factory.
     * Pass in the SSL protocol to use, e.g. "TLS" or "SSLv3".
     *
     * @param protocol SSL protocol to use.
     */
    public void useSslProtocol(String protocol, TrustManager trustManager)
        throws NoSuchAlgorithmException, KeyManagementException
    {
        SSLContext c = SSLContext.getInstance(protocol);
        c.init(null, new TrustManager[] { trustManager }, null);
        useSslProtocol(c);
    }

    /**
     * Convenience method for setting up an SSL socket factory.
     * Pass in an initialized SSLContext.
     *
     * @param context An initialized SSLContext
     */
    public void useSslProtocol(SSLContext context)
    {
        setSocketFactory(context.getSocketFactory());
    }

    /**
     * The default SSL protocol (currently "SSLv3").
     */
    public static final String DEFAULT_SSL_PROTOCOL = "SSLv3";

    protected FrameHandler createFrameHandler(Address addr)
        throws IOException {

        String hostName = addr.getHost();
        int portNumber = addr.getPort();
        if (portNumber == -1) portNumber = AMQP.PROTOCOL.PORT;
        return new SocketFrameHandler(_factory, hostName, portNumber);
    }

    private Connection newConnection(Address[] addrs,
                                     int maxRedirects,
                                     Map<Address,Integer> redirectAttempts)
        throws IOException
    {
        IOException lastException = null;

        for (Address addr : addrs) {
            Address[] lastKnownAddresses = new Address[0];
            try {
                while(true) {
                    FrameHandler frameHandler = createFrameHandler(addr);
                    Integer redirectCount = redirectAttempts.get(addr);
                    if (redirectCount == null)
                        redirectCount = 0;
                    boolean allowRedirects = redirectCount < maxRedirects;
                    try {
                        AMQConnection conn = new AMQConnection(this,
                                    frameHandler);
                        conn.start(!allowRedirects);
                        return conn;
                    } catch (RedirectException e) {
                        if (!allowRedirects) {
                            //this should never happen with a well-behaved server
                            throw new IOException("server ignored 'insist'");
                        } else {
                            redirectAttempts.put(addr, redirectCount+1);
                            lastKnownAddresses = e.getKnownAddresses();
                            addr = e.getAddress();
                            //TODO: we may want to log redirection attempts.
                        }
                    }
                }
            } catch (IOException e) {
                lastException = e;
                if (lastKnownAddresses.length > 0) {
                    // If there aren't any, don't bother trying, since
                    // a recursive call with empty lastKnownAddresses
                    // will cause our lastException to be stomped on
                    // by an uninformative IOException. See bug 16273.
                    try {
                        return newConnection(lastKnownAddresses,
                                             maxRedirects,
                                             redirectAttempts);
                    } catch (IOException e1) {
                        lastException = e1;
                    }
                }
            }
        }

        if (lastException == null) {
            throw new IOException("failed to connect");
        } else {
            throw lastException;
        }
    }

    /**
     * Create a new broker connection
     * @param addrs an array of known broker addresses (hostname/port pairs) to try in order
     * @param maxRedirects the maximum allowable number of redirects
     * @return an interface to the connection
     * @throws IOException if it encounters a problem
     */
    public Connection newConnection(Address[] addrs, int maxRedirects)
        throws IOException
    {
        return newConnection(addrs,
                             maxRedirects,
                             new HashMap<Address,Integer>());
    }

    /**
     * Create a new broker connection (no redirects allowed)
     * @param addrs an array of known broker addresses (hostname/port pairs) to try in order
     * @return an interface to the connection
     * @throws IOException if it encounters a problem
     */
    public Connection newConnection(Address[] addrs)
        throws IOException
    {
        return newConnection(addrs, 0);
    }

    /**
     * Instantiates a connection and return an interface to it.
     * @param hostName the host to connect to
     * @param portNumber the port number to use
     * @return an interface to the connection
     * @throws IOException if it encounters a problem
     */
    public Connection newConnection(String hostName, int portNumber) throws IOException {
        return newConnection(new Address[] {
                                 new Address(hostName, portNumber)
                             });
    }

    /**
     * Create a new broker connection, using the default AMQP port
     * @param hostName the host to connect to
     * @return an interface to the connection
     * @throws IOException if it encounters a problem
     */
    public Connection newConnection(String hostName) throws IOException {
        return newConnection(hostName, -1);
    }
}
