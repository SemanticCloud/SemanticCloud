package org.semanticcloud.providers.joyent.triton;

import org.semanticcloud.providers.joyent.triton.domain.Package;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/{login}/packages")
public interface PakageAPI {
    @GET
    List<Package> list(@PathParam("login") String login);
    @GET
    @Path("/{id}")
    Package get(@PathParam("login") String login, @PathParam("id") String id);
}
