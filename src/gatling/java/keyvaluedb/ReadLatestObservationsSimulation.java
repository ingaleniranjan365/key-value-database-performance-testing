package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import model.TripObservation;

import java.util.concurrent.atomic.AtomicInteger;

public class ReadLatestObservationsSimulation extends Simulation {

        int applicationPort = 8080;
        AtomicInteger failedSessionCounter = new AtomicInteger(0);
        FeederBuilder<String> feeder = csv("latest_trip_observations.csv").circular();

        ScenarioBuilder retrieve = scenario("Match latest observations with trips")
                .feed(feeder)
                .exec(http("GET latest observations")
                        .get("/latest/element/#{id}")
                        .check(status().is(200))
                        .check(bodyString().saveAs("responseBody")))
                .exec(session -> {
                        var expectedTripJSONString = new TripObservation(session).toString();
                        var latestObservationJSONString = session.getString("responseBody");

                        if(expectedTripJSONString.equals(latestObservationJSONString)) return session;
                        session.markAsFailed();
                        System.out.printf("So far, %d sessions failed!!!%n", failedSessionCounter.incrementAndGet());
                        throw new RuntimeException("Latest record did not match with actual trip data !");
                });

        HttpProtocolBuilder httpProtocol =
                http.baseUrl("http://localhost:" + applicationPort)
                        .acceptHeader("text/html,application/json")
                        .acceptEncodingHeader("gzip, deflate");

        {
                setUp(
                        retrieve.injectOpen(rampUsers(25000).during(3*60))
                ).protocols(httpProtocol);
        }
}
