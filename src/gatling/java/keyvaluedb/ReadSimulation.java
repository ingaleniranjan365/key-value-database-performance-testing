package keyvaluedb;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ReadSimulation extends Simulation {

        int applicationPort = 8081;
        FeederBuilder<String> feeder = csv("trips.csv").circular();

        ScenarioBuilder retrieve = scenario("Retrieve")
                .feed(feeder)
                .exec(
                        http("retrieve")
                                .get("/latest/element/#{id}")
                                .check(status().is(200))
                );


        HttpProtocolBuilder httpProtocol =
                http.baseUrl("http://localhost:" + applicationPort)
                        .acceptHeader("text/html,application/json")
                        .acceptEncodingHeader("gzip, deflate");

        {
                setUp(
                        retrieve.injectOpen(rampUsers(150000).during(300))
                ).protocols(httpProtocol);
        }

}
