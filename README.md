# gt-azure-geotiff

### Configuration

This project is heavily modeled after (and copied from) from the 
[s3-geotiff plugin](https://github.com/geotools/geotools/tree/master/modules/unsupported/s3-geotiff).

The Azure GeoTIFF image input stream utilizes the Azure API to read GeoTIFF data from Azure blob storage. It is built 
to read WASB style URLs in the following format:

<code>wasb[s]://<containername>@<accountname>.blob.core.windows.net/<path></code>

Authentication to Azure is accomplished by setting a system property or environment variable for any given account.
The plugin will attempt to locate the Azure account name from the requested URI.  The format for the expected variable 
is:

```<account_name>_AZURE_CONNECTION_STRING```