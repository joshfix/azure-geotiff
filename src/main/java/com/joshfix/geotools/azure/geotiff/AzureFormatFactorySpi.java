package com.joshfix.geotools.azure.geotiff;

import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;

import java.awt.*;
import java.util.Collections;
import java.util.Map;

/**
 * @author joshfix
 * Created on 1/19/18
 */
public class AzureFormatFactorySpi implements GridFormatFactorySpi {

    public AbstractGridFormat createFormat() {
        return new AzureGeoTiffFormat();
    }

    public boolean isAvailable() {
        return true;
    }

    public Map<RenderingHints.Key, ?> getImplementationHints() {
        return Collections.EMPTY_MAP;
    }

}
