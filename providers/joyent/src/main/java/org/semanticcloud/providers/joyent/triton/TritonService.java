package org.semanticcloud.providers.joyent.triton;

import feign.Feign;
import feign.Response;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import org.semanticcloud.providers.joyent.triton.domain.Image;
import org.semanticcloud.providers.joyent.triton.domain.Instance;
import org.semanticcloud.providers.joyent.triton.domain.Package;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TritonService {
    private final InstanceAPI instanceAPI;
    private final PakageAPI pakageAPI;
    private final ImageAPI imageAPI;
    private String url;
    private String user;
    private String key;

    public TritonService(String url, String user, String key) throws IOException {
        this.url = url;
        this.user = user;
        this.key = key;
        instanceAPI = buildAPI(InstanceAPI.class);
        pakageAPI = buildAPI(PakageAPI.class);
        imageAPI = buildAPI(ImageAPI.class);
    }

    public List<Package> listPakages(){
        return pakageAPI.list(this.user);
    }

    public Response createInstance() {
        Instance instance = new Instance();
        instance.setImage("822d0acc-1ba7-11e7-873f-971cd2728133");
        instance.setPackageId("14b4ff36-d0f8-11e5-a8b1-e343c129d7f0");
        return instanceAPI.create(this.user, instance);

    }

    public List<Instance> listInstances() {
        return instanceAPI.list(this.user);
    }

    public List<Image> listImages(){
        return imageAPI.list(this.user);
    }

    private <T> T buildAPI(Class<T> apiType) throws IOException {
        return Feign.builder()
                .contract(new JAXRSContract())
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .requestInterceptor(new MyRequestInterceptor(this.user, this.key))
                .target(apiType, this.url);
    }
}
