package org.semanticcloud.providers.joyent.triton;

import com.joyent.http.signature.KeyPairLoader;
import com.joyent.http.signature.Signer;
import com.joyent.http.signature.ThreadLocalSigner;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MyRequestInterceptor implements RequestInterceptor {
    /**
     * Name of the authorization HTTP header.
     */
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * Name of the date HTTP header.
     */
    public static final String DATE_HEADER_NAME = "Date";
    private final String loginName;
    private final KeyPair keyPair;
    private final ThreadLocalSigner signer;

    public MyRequestInterceptor(final String loginName, final String keyPath)
            throws IOException {
        this.loginName = loginName;
        this.keyPair = KeyPairLoader.getKeyPair(Paths.get(keyPath));
        this.signer = new ThreadLocalSigner(new Signer.Builder(keyPair));
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final String dateHeaderValue = DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
        requestTemplate.header(DATE_HEADER_NAME, dateHeaderValue);

                    final String authHeaderValue = signer.get().createAuthorizationHeader(
                            loginName,
                            keyPair,
                            now
                    );
                    requestTemplate.header(AUTHORIZATION_HEADER_NAME, authHeaderValue);

    }
}
