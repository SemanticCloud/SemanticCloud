package org.semanticcloud.providers.joyent.triton;

import feign.Param;
import feign.RequestLine;

import java.util.List;


public interface ImageAPI {
    @RequestLine("GET /{login}/packages")
    List<Package> list(@Param("login") String login);
    @RequestLine("GET /{login}/packages/{id}")
    void get(@Param("login") String login, @Param("id") String id);
}
