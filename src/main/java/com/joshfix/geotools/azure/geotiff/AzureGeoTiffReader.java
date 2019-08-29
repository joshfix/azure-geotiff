package com.joshfix.geotools.azure.geotiff;

import com.joshfix.geotools.azure.AzureImageInputStreamImpl;
import com.joshfix.geotools.azure.AzureImageInputStreamSpi;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.util.factory.Hints;

/**
 * @author joshfix
 * Created on 1/19/18
 */
public class AzureGeoTiffReader extends GeoTiffReader {

    public AzureGeoTiffReader(Object input) throws DataSourceException {
        super(input);

        /*
         * Because Azure geotiff is always instantiated with an AzureImageInputStreamImpl the SPI never
         * gets set (because the reader doesn't need to look for it). We set it hear so that
         * subsequent calls that rely on it pass.
         */
        this.inStreamSPI = new AzureImageInputStreamSpi();
        //Needs close me, since we're using a stream and it should not be reused.
        closeMe = true;
    }

    public AzureGeoTiffReader(Object input, Hints uHints) throws DataSourceException {
        super(input, uHints);
        closeMe = true;
        this.inStreamSPI = new AzureImageInputStreamSpi();
        if (input instanceof AzureImageInputStreamImpl) {
            String fileName = ((AzureImageInputStreamImpl) input).getFilename();
            final int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1 && dotIndex != fileName.length()) {
                this.coverageName = fileName.substring(0, dotIndex);
            }
        }
    }
}
