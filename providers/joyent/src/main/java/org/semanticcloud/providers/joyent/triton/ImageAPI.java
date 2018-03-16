package org.semanticcloud.providers.joyent.triton;

import feign.Param;
import org.semanticcloud.providers.joyent.triton.domain.Image;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/{login}/images")
public interface ImageAPI {
    @GET
    List<Image> list(@PathParam("login")String login);
    @GET
    @Path("/{id}")
    Image get(@PathParam("login") String login, @Param("id") String id);
}
