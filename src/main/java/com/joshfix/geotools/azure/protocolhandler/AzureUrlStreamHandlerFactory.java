package com.joshfix.geotools.azure.protocolhandler;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Optional;

/**
 * @author joshfix
 * Created on 3/22/18
 */
public class AzureUrlStreamHandlerFactory implements URLStreamHandlerFactory {

    private final Optional<URLStreamHandlerFactory> delegate;

    public AzureUrlStreamHandlerFactory() {
        this(null);
    }

    public AzureUrlStreamHandlerFactory(final URLStreamHandlerFactory delegate) {
        this.delegate = Optional.ofNullable(delegate);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("wasbs".equals(protocol.toLowerCase())) {
            return new sun.net.www.protocol.wasbs.Handler();
        } else if ("wasb".equals(protocol.toLowerCase())) {
            return new sun.net.www.protocol.wasb.Handler();
        }

        return delegate.map(factory -> factory.createURLStreamHandler(protocol)).orElse(null);
    }

}
