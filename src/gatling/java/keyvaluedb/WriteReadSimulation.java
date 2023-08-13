package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Objects;

import java.util.function.Function;

public class WriteReadSimulation extends Simulation {

        int applicationPort = 8081;
        FeederBuilder<String> feeder = csv("trips.csv").circular();

        ScenarioBuilder insertAndRetrieve = scenario("Insert")
                .feed(feeder)
                .exec(
                        http("insert")
                                .put(session -> {
                                        var timestamp = Objects.requireNonNull(session.getString("dropoff_datetime"))
                                                .replace(" ", "T") + 'Z';
                                        var id= session.getString("id");
                                        return String.format("/element/%s/timestamp/%s", id, timestamp);
                                })
                                .body(io.gatling.javaapi.core.CoreDsl.StringBody(RequestBodyBuilder.extractJSONString))
                                .check(
                                        status().is(200)
                                )
                )
                .pause("1")
                .exec(
                        http("retrieve")
                                .get("/latest/element/#{id}")
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
                        insertAndRetrieve.injectOpen(rampUsers(360000).during(3600))
                ).protocols(httpProtocol);
        }

        static final class RequestBodyBuilder {
                public static final Function<Session, String> extractJSONString = session -> {

                        int vendorId = session.getInt("vendor_id");
                        int passengerCount = session.getInt("passenger_count");
                        double pickupLongitude = session.getDouble("pickup_longitude");
                        double pickupLatitude = session.getDouble("pickup_latitude");
                        double dropoffLongitude = session.getDouble("dropoff_longitude");
                        double dropoffLatitude = session.getDouble("dropoff_latitude");
                        String id = session.getString("id");
                        String pickupDatetime = Objects.requireNonNull(session.getString("pickup_datetime"))
                                .replace(" ", "T")+'Z';
                        String dropoffDatetime = Objects.requireNonNull(session.getString("dropoff_datetime"))
                                .replace(" ", "T")+'Z';
                        String storeAndFwdFlag = session.getString("store_and_fwd_flag");
                        String tripDuration = session.getString("trip_duration");

                        String jsonString = "{"
                                + "\"id\":\"" + id + "\","
                                + "\"vendor_id\":" + vendorId + ","
                                + "\"pickup_datetime\":\"" + pickupDatetime + "\","
                                + "\"dropoff_datetime\":\"" + dropoffDatetime + "\","
                                + "\"passenger_count\":" + passengerCount + ","
                                + "\"pickup_longitude\":" + pickupLongitude + ","
                                + "\"pickup_latitude\":" + pickupLatitude + ","
                                + "\"dropoff_longitude\":" + dropoffLongitude + ","
                                + "\"dropoff_latitude\":" + dropoffLatitude + ","
                                + "\"store_and_fwd_flag\":\"" + storeAndFwdFlag + "\","
                                + "\"trip_duration\":\"" + tripDuration + "\""
                                + "}";

                        return jsonString;
                };
        }

}
