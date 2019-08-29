package com.joshfix.geotools.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.logging.Logger;

/**
 * @author joshfix
 * Created on 1/18/18
 */
public class AzureConnector {

    private String connectionString;
    private static final Logger LOGGER = Logger.getLogger(AzureConnector.class.getName());

    /**
     * Attempts to discover a connection string in system properties and environment variables based on the account
     * name defined in the WASB URL.
     *
     * @param accountName
     */
    public AzureConnector(String accountName) {
        connectionString = ConnectionDirectory.getConnectionString(accountName);
    }

    public CloudBlobClient getAzureClient() {
        // Retrieve storage account from connection-string.
        CloudStorageAccount storageAccount = null;
        try {
            storageAccount = CloudStorageAccount.parse(connectionString);
        } catch (URISyntaxException|InvalidKeyException e) {
            e.printStackTrace();
        }

        return storageAccount.createCloudBlobClient();
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getConnectionString() {
        return connectionString;
    }

}
