package com.kite9.k9server.docker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.adl.StreamHelp;

/**
 * Contains a single extra method so we can return an InputStream response.
 * 
 * @author robmoffat
 *
 */
public class MediaHandlingRestTemplate extends RestTemplate {

	public MediaHandlingRestTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}
	
	public byte[] exchange(RequestEntity<?> requestEntity) throws RestClientException {

		Assert.notNull(requestEntity, "'requestEntity' must not be null");

		RequestCallback requestCallback = httpEntityCallback(requestEntity, null);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, new ResponseExtractor<byte[]>() {

			@Override
			public byte[] extractData(ClientHttpResponse response) throws IOException {
				int contentLength = (int) response.getHeaders().getContentLength();
				ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(contentLength, 6000));
				StreamHelp.streamCopy(response.getBody(), baos, true);
				return baos.toByteArray();
			}
		});
	}

}
