package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.gatling.javaapi.core.Session;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Trip {

        public final int vendorId;
        public final int passengerCount;
        public final double pickupLongitude;
        public final double pickupLatitude;
        public final String id;
        public final String pickupDatetime;
        public final String storeAndFwdFlag;
        public final String tripDuration;
        public final Optional<Double> dropoffLongitude;
        public final Optional<Double> dropoffLatitude;
        public final Optional<String> dropoffDatetime;

        public Trip(Session session) {
                this.vendorId = session.getInt("vendor_id");
                this.passengerCount = session.getInt("passenger_count");
                this.pickupLongitude = session.getDouble("pickup_longitude");
                this.pickupLatitude = session.getDouble("pickup_latitude");
                this.id = session.getString("id");
                this.pickupDatetime = getFormattedTimestampString(session.getString("pickup_datetime"));
                this.storeAndFwdFlag = session.getString("store_and_fwd_flag");
                this.tripDuration = session.getString("trip_duration");
                this.dropoffLongitude = getDouble(Objects.requireNonNull(session.getString("dropoff_longitude")));
                this.dropoffLatitude = getDouble(Objects.requireNonNull(session.getString("dropoff_latitude")));
                this.dropoffDatetime = getTimestamp(Objects.requireNonNull(session.getString("dropoff_datetime")));
        }

        public Trip(String jsonString) throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();
                Trip parsedTripData = objectMapper.readValue(jsonString, Trip.class);

                this.id = parsedTripData.id;
                this.vendorId = parsedTripData.vendorId;
                this.passengerCount = parsedTripData.passengerCount;
                this.pickupLongitude = parsedTripData.pickupLongitude;
                this.pickupLatitude = parsedTripData.pickupLatitude;
                this.pickupDatetime = parsedTripData.pickupDatetime;
                this.storeAndFwdFlag = parsedTripData.storeAndFwdFlag;
                this.tripDuration = parsedTripData.tripDuration;
                this.dropoffLongitude = parsedTripData.dropoffLongitude;
                this.dropoffLatitude = parsedTripData.dropoffLatitude;
                this.dropoffDatetime = parsedTripData.dropoffDatetime;
        }


        public String getFormattedTimestampString(String timestamp) {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestamp);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                return offsetDateTime.format(formatter);
        }

        public Optional<Double> getDouble(String floatStr) {
                if(floatStr.equals("")) {
                        return Optional.empty();
                } else {
                        return Optional.of(Double.parseDouble(floatStr));
                }
        }

        public Optional<String> getTimestamp(String timestamp) {
                if(timestamp.equals("")) {
                        return Optional.empty();
                } else {
                        return Optional.of(getFormattedTimestampString(timestamp));
                }
        }


        @Override
        public String toString() {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                try {
                        return objectMapper.writeValueAsString(this);
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return "";
                }
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Trip trip = (Trip) o;
                return vendorId == trip.vendorId &&
                        passengerCount == trip.passengerCount &&
                        Objects.equals(pickupLongitude, trip.pickupLongitude) &&
                        Objects.equals(pickupLatitude, trip.pickupLatitude) &&
                        Objects.equals(id, trip.id) &&
                        Objects.equals(pickupDatetime, trip.pickupDatetime) &&
                        Objects.equals(storeAndFwdFlag, trip.storeAndFwdFlag) &&
                        Objects.equals(tripDuration, trip.tripDuration) &&
                        Objects.equals(dropoffDatetime, trip.dropoffDatetime);
        }

        @Override
        public int hashCode() {
                return Objects.hash(vendorId, passengerCount, pickupLongitude, pickupLatitude, id,
                        pickupDatetime, storeAndFwdFlag, tripDuration, dropoffDatetime);
        }
}
