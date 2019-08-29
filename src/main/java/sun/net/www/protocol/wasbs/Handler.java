package sun.net.www.protocol.wasbs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new URLConnection(url) {
			
			@Override
			public void connect() throws IOException {
				
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
		        return new InputStream() {

					@Override
					public int read() throws IOException {
						throw new UnsupportedOperationException();
					}
		        	
		        };
		    }
		};
	}

}
