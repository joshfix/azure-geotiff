package com.joshfix.geotools.azure.geotiff;

import com.joshfix.geotools.azure.AzureImageInputStreamImpl;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.geotools.parameter.ParameterGroup;
import org.geotools.util.factory.Hints;
import org.opengis.parameter.GeneralParameterDescriptor;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author joshfix
 * Created on 1/19/18
 */
public class AzureGeoTiffFormat extends GeoTiffFormat {

    private static final Logger LOGGER = Logger.getLogger(AzureGeoTiffFormat.class.getName());

    public AzureGeoTiffFormat() {
        writeParameters = null;
        mInfo = new HashMap<>();
        mInfo.put("name", "AzureGeoTiff");
        mInfo.put("description","Tagged Image File Format with Geographic information hosted on Azure");
        mInfo.put("vendor", "Josh Fix");
        mInfo.put("version", "1.0");

        // reading parameters
        readParameters = new ParameterGroup(
                new DefaultParameterDescriptorGroup(
                        mInfo,
                        new GeneralParameterDescriptor[] {
                                READ_GRIDGEOMETRY2D,
                                INPUT_TRANSPARENT_COLOR,
                                SUGGESTED_TILE_SIZE}));

        // writing parameters
        writeParameters = new ParameterGroup(
                new DefaultParameterDescriptorGroup(
                        mInfo,
                        new GeneralParameterDescriptor[] {
                                RETAIN_AXES_ORDER,
                                AbstractGridFormat.GEOTOOLS_WRITE_PARAMS,
                                AbstractGridFormat.PROGRESS_LISTENER }));
    }

    @Override
    public GeoTiffReader getReader(Object source, Hints hints) {
        //in practice here source is probably almost always going to be a string.
        try {
            //big old try block since we can't do anything meaningful with an exception anyway
            AzureImageInputStreamImpl inStream;
            if (source instanceof File) {
                throw new UnsupportedOperationException("Can't instantiate Azure with a File handle");
            }
            else if (source instanceof String) {
                inStream = new AzureImageInputStreamImpl((String)source);
            }
            else if (source instanceof URL) {
                inStream = new AzureImageInputStreamImpl((URL)source);
            }
            else {
                throw new IllegalArgumentException("Can't create AzureImageInputStream from input of "
                        + "type: " + source.getClass());
            }

            return new AzureGeoTiffReader(inStream, hints);
        }
        catch (Exception e) {
            LOGGER.log(Level.FINE, "Exception raised trying to instantiate Azure image input "
                    + "stream from source.", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean accepts(Object o, Hints hints) {
        if (o == null) {
            return false;
        } else {
            boolean accepts = false;
            if (o instanceof String) {
                accepts = ((String) o).startsWith("wasb://") || ((String)o).startsWith("wasbs://");
            }
            else if (o instanceof URL) {
                String protocol = ((URL) o).getProtocol();
                accepts = protocol.equals("wasb") || protocol.equals("wasbs");
            }
            return accepts;
        }
    }

    @Override
    public boolean accepts(Object source) {
        return this.accepts(source, null);
    }
}
