package com.joshfix.geotools.azure;

import it.geosolutions.imageio.stream.input.spi.URLImageInputStreamSpi;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author joshfix
 * Created on 1/19/18
 */
public class AzureImageInputStreamSpi extends ImageInputStreamSpi {

    /** Logger. */
    private final static Logger LOGGER = Logger
            .getLogger(AzureImageInputStreamSpi.class.getName());

    private static final String vendorName = "Josh Fix";

    private static final String version = "1.0";

    private static final Class<URL> inputClass = URL.class;


    /**
     * Constructs a blank {@link ImageInputStreamSpi}. It is up to the subclass
     * to initialize instance variables and/or override method implementations
     * in order to provide working versions of all methods.
     *
     */
    public AzureImageInputStreamSpi() {
        super(vendorName, version, inputClass);
    }

    /**
     * @see ImageInputStreamSpi#getDescription(Locale).
     */
    public String getDescription(Locale locale) {
        return "Image input stream that reads blobs from Azure";
    }

    public void onRegistration(ServiceRegistry registry, Class<?> category) {
        super.onRegistration(registry, category);
        Class<ImageInputStreamSpi> targetClass = ImageInputStreamSpi.class;
        for (Iterator<? extends ImageInputStreamSpi> i = registry.getServiceProviders(targetClass, true); i.hasNext();) {
            ImageInputStreamSpi other = i.next();

            if (this != other)
                registry.setOrdering(targetClass, this, other);

        }
    }
    /**
     * Returns an instance of the ImageInputStream implementation associated
     * with this service provider.
     *
     * @param input
     *            an object of the class type returned by getInputClass.
     * @param useCache
     *            a boolean indicating whether a cache eraf should be used, in
     *            cases where it is optional.
     *
     * @param cacheDir
     *            a File indicating where the cache eraf should be created, or
     *            null to use the system directory.
     *
     *
     * @return an ImageInputStream instance.
     *
     * @throws IllegalArgumentException
     *             if input is not an instance of the correct class or is null.
     */
    public ImageInputStream createInputStreamInstance(Object input, boolean useCache, File cacheDir) {
        if (input instanceof URL) {
            if (((URL)input).getProtocol().equalsIgnoreCase("file")) {
                URLImageInputStreamSpi fileUrlSpi = new URLImageInputStreamSpi();
                return fileUrlSpi.createInputStreamInstance(input, useCache, cacheDir);
            }
        }

        if (input instanceof AzureImageInputStreamImpl) {
            try {
                return new AzureImageInputStreamImpl(((AzureImageInputStreamImpl)input).getUrl());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            URL url = (URL) input;
            return new AzureImageInputStreamImpl(url);
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
            return null;
        }

    }
}
