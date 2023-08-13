package model;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.*;

public class Observation extends Trip {

        public final double lon;
        public final double lat;
        public final String timestamp;

        public Observation(Session session) {
                super(session);
                this.lon = session.getDouble("lon");
                this.lat = session.getDouble("lat");
                this.timestamp = getFormattedTimestampString(session.getString("timestamp"));
        }

        public Observation(String jsonString) throws IOException {
                super(jsonString);

                ObjectMapper objectMapper = new ObjectMapper();
                Observation parsedObservationData = objectMapper.readValue(jsonString, Observation.class);
                this.lon = parsedObservationData.lon;
                this.lat = parsedObservationData.lat;
                this.timestamp = parsedObservationData.timestamp;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                if (!super.equals(o)) return false;
                Observation observation = (Observation) o;
                return Objects.equals(lon, observation.lon) &&
                        Objects.equals(lat, observation.lat) &&
                        Objects.equals(timestamp, observation.timestamp);
        }

        @Override
        public int hashCode() {
                return Objects.hash(super.hashCode(), lon, lat, timestamp);
        }

}
