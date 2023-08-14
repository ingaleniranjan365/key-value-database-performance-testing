package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import model.Observation;
import model.Trip;

import java.util.Objects;

import java.util.function.Function;

public class WriteSimulation extends Simulation {

        int applicationPort = 8081;
        FeederBuilder<String> feeder = csv("trips.csv").circular();

        ScenarioBuilder insertAndRetrieve = scenario("Insert")
                .feed(feeder)
                .exec(
                        http("insert")
                                .put(session -> {
                                        var timestamp = Trip.getFormattedTimestampString(session.getString(
                                                "dropoff_datetime"));
                                        var id= session.getString("id");
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
                        insertAndRetrieve.injectOpen(rampUsers(10).during(1))
                ).protocols(httpProtocol);
        }

        public static final class RequestBodyBuilder {
                public static final Function<Session, String> extractJSONString =
                        session -> new Trip(session).toString();
        }

}
