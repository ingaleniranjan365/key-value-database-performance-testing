package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import model.TripObservation;

public class ReadLatestObservationsSimulation extends Simulation {

        int applicationPort = 8081;
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
                        throw new RuntimeException("Latest record did not match with actual trip data !");
                });

        HttpProtocolBuilder httpProtocol =
                http.baseUrl("http://localhost:" + applicationPort)
                        .acceptHeader("text/html,application/json")
                        .acceptEncodingHeader("gzip, deflate");

        {
                setUp(
                        retrieve.injectOpen(rampUsers(1000).during(10))
                ).protocols(httpProtocol);
        }
}
