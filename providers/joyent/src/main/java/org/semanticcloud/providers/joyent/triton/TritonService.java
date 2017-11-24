package org.semanticcloud.providers.joyent.triton;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jaxrs.JAXRSContract;
import org.semanticcloud.providers.joyent.triton.domain.Package;

import java.io.IOException;
import java.util.List;

public class TritonService {
    private final InstanceAPI instanceAPI;
    private final PakageAPI pakageAPI;
    private String url;
    private String user;
    private String key;

    public TritonService(String url, String user, String key) throws IOException {
        this.url = url;
        this.user = user;
        this.key = key;
        instanceAPI = Feign.builder()
                .contract(new JAXRSContract())
                .decoder(new JacksonDecoder())
                .requestInterceptor(new MyRequestInterceptor(this.user, this.key))
                .target(InstanceAPI.class, this.url);
        pakageAPI = Feign.builder()
                .contract(new JAXRSContract())
                .decoder(new JacksonDecoder())
                .requestInterceptor(new MyRequestInterceptor(this.user, this.key))
                .target(PakageAPI.class, this.url);
    }

    public List<Package> listPakages(){
        return pakageAPI.list(this.user);
    }

}
