package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import model.Trip;

public class ReadObservationsSimulation extends Simulation {

        int applicationPort = 8081;
        FeederBuilder<String> feeder = csv("observations_trips.csv").circular();

        ScenarioBuilder retrieve = scenario("Match latest observations with trips")
                .feed(feeder)
                .exec(http("GET latest observations")
                        .get("/latest/element/#{id}")
                        .check(status().is(200))
                        .check(bodyString().saveAs("responseBody")))
                .exec(session -> {
                        var expectedTripJSONString = new Trip(session).toString();
                        var latestObservationJSONString = session.getString("responseBody");
                        var actualTripJSONStringForLatestObservation = getTripJSONString(latestObservationJSONString);

                        if(expectedTripJSONString.equals(actualTripJSONStringForLatestObservation)) return session;
                        session.markAsFailed();
                        throw new RuntimeException("Latest record did not match with actual trip data !");
                });

        HttpProtocolBuilder httpProtocol =
                http.baseUrl("http://localhost:" + applicationPort)
                        .acceptHeader("text/html,application/json")
                        .acceptEncodingHeader("gzip, deflate");

        {
                setUp(
                        retrieve.injectOpen(rampUsers(3).during(1))
                ).protocols(httpProtocol);
        }

        public static String getTripJSONString(String observationJSONString) {
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(observationJSONString);

                        if (jsonNode.has("lat") && jsonNode.has("lon") && jsonNode.has("timestamp")) {
                                ObjectNode updatedJson = (ObjectNode) jsonNode;
                                updatedJson.remove("lat");
                                updatedJson.remove("lon");
                                updatedJson.remove("timestamp");

                                return objectMapper.writeValueAsString(updatedJson);
                        } else {
                                return observationJSONString;
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                }
        }
}
