package com.joshfix.geotools.azure.utility;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author joshfix
 * Created on 2/23/18
 */
public class AzureUrlParser {

    private static final Logger LOGGER = Logger.getLogger(AzureUrlParser.class.getName());

    public static String getAccountName(URL url) {
                String host = url.getHost();
                int firstPeriod = host.indexOf(".");
                if (firstPeriod == -1) {
                    return host;
                }
                return host.substring(0, firstPeriod);
    }

    public static String getContainerName(URL url) {
        switch (url.getProtocol()) {
            case "WASB":
            case "WASBS":
            case "wasb":
            case "wasbs":
                return url.getUserInfo();
            default:
                String path = url.getPath().startsWith("/") ? url.getPath().substring(1) : url.getPath();
                String containerName = null;
                try {
                    path.substring(0, path.indexOf("/"));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error parsing container name from Azure URL: " + url.toString(), e);
                    return null;
                }
                return path.substring(0, path.indexOf("/"));
        }
    }

    public static String getFilename(URL url) {
        switch (url.getProtocol()) {
            case "WASB":
            case "WASBS":
            case "wasb":
            case "wasbs":
                return url.getPath().startsWith("/") ? url.getPath().substring(1) : url.getPath();
            default:
                String containerName = getContainerName(url);
                String path = url.getPath();
                return path.split(containerName + "/")[1];
        }
    }

}
