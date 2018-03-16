package org.semanticcloud.providers.joyent.triton;

import feign.Response;
import org.semanticcloud.providers.joyent.triton.domain.Instance;
import org.semanticcloud.providers.joyent.triton.domain.Package;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/{login}/machines")
public interface InstanceAPI {
    @GET
    List<Instance> list(@PathParam("login") String login);
    @GET
    @Path("/{id}")
    Instance get(@PathParam("login") String login, @PathParam("id") String id);
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response create(@PathParam("login") String login, Instance instance);
}
