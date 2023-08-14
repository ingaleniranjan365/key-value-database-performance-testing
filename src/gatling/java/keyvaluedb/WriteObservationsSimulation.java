package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import model.Observation;

import java.util.function.Function;

public class WriteObservationsSimulation extends Simulation {

        int applicationPort = 8081;
        FeederBuilder<String> feeder = csv("shuffled_trip_observations.csv").circular();

        ScenarioBuilder insertAndRetrieve = scenario("Insert")
                .feed(feeder)
                .exec(
                        http("insert")
                                .put(session -> {
                                        var id = session.getString("id");
                                        var timestamp = Observation.getFormattedTimestampString(session.getString("timestamp"));
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
                        insertAndRetrieve.injectOpen(rampUsers(1000).during(10))
                ).protocols(httpProtocol);
        }


        static final class RequestBodyBuilder {
                public static final Function<Session, String> extractJSONString =
                        session -> new Observation(session).toString();
        }

}
