package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import model.TripObservation;

import java.util.function.Function;

public class WriteShuffledObservationsSimulation extends Simulation {

        int applicationPort = 8081;
        FeederBuilder<String> feeder = csv("shuffled_trip_observations.csv").circular();

        ScenarioBuilder insertAndRetrieve = scenario("Insert")
                .feed(feeder)
                .exec(
                        http("insert")
                                .put(session -> {
                                        var id = session.getString("id");
                                        var timestamp = TripObservation.getFormattedTimestampString(session.getString("timestamp"));
                                        return String.format("/element/%s/timestamp/%s", id, timestamp);
                                })
                                .body(io.gatling.javaapi.core.CoreDsl.StringBody(RequestBodyBuilder.extractJSONString))
                                .check(
                                        status().is(200)
                                )
                );


        HttpProtocolBuilder httpProtocol =
                http.baseUrl("http://localhost:" + applicationPort)
                        .acceptHeader("text/html,application/json")
                        .acceptEncodingHeader("gzip, deflate");

        {
                setUp(
                        insertAndRetrieve.injectOpen(rampUsers(100000).during(200))
//                        insertAndRetrieve.injectOpen(rampUsers(323359).during(60*30))
                ).protocols(httpProtocol);
        }


        static final class RequestBodyBuilder {
                public static final Function<Session, String> extractJSONString =
                        session -> new TripObservation(session).toString();
        }

}
