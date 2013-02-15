package org.xcare.kidpcctrl;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/track")
public class KompiTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(KompiTrackerService.class);

    Repository repo = new Repository();

    @POST
    @Path("/update")
    public void update(@FormParam("username") String username) {
        logger.debug("User {}: Update", username);
        repo.update(username);
    }

    @POST
    @Path("/notrack")
    public void doNotTrackForNextMinutes(@FormParam("username") String username, @FormParam("minutes") int minutes) {
        logger.info("User {}: Do not track for next {} minutes", username, minutes);
        repo.doNotTrackForNextMinutes(username, minutes);
    }

    @POST
    @Path("/extracredit")
    public void extraCredit(@FormParam("username") String username, @FormParam("minutes") int minutes) {
        logger.info("User {}: Extra credit: {} minutes", username, minutes);
        repo.extraCredit(username, minutes);
    }

    @POST
    @Path("/reset")
    public void reset(@FormParam("username") String username) {
        logger.info("User {}: Reset", username);
        repo.reset(username);
    }

    @GET
    @Path("/info/{username}")
    @Produces({ MediaType.APPLICATION_JSON })
    public KompiAccess getInfo(@PathParam("username") String username) {
        KompiAccess info = repo.getInfo(username);
        return info;
        //        String output = "Hi from jersey";
        //        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/infos")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<KompiAccess> getInfos() {
        List<KompiAccess> infos = repo.getInfos();
        return infos;
    }
}
