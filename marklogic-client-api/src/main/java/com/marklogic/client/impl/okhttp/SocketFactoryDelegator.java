/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

class SocketFactoryDelegator extends SocketFactory {
    private final SocketFactory delegate;

    SocketFactoryDelegator(SocketFactory delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return configureSocket(delegate.createSocket(host, port));
    }
    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        return configureSocket(delegate.createSocket(host, port, localAddress, localPort));
    }
    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return configureSocket(delegate.createSocket(address, port));
    }
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return configureSocket(delegate.createSocket(address, port, localAddress, localPort));
    }
    @Override
    public Socket createSocket() throws IOException {
        return configureSocket(delegate.createSocket());
    }

    private Socket configureSocket(Socket socket) throws SocketException {
        socket.setKeepAlive(true);
        return socket;
    }
}
