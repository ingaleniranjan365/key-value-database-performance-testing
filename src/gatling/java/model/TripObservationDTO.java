package model;

public class TripObservationDTO {

        public String vendorId;
        public String passengerCount;
        public String pickupLongitude;
        public String pickupLatitude;
        public String id;
        public String pickupDatetime;
        public String storeAndFwdFlag;
        public String tripDuration;
        public String dropoffLongitude;
        public String dropoffLatitude;
        public String dropoffDatetime;
        public String timestamp;
        public String lon;
        public String lat;

        TripObservationDTO(TripObservation trip) {
                this.vendorId = String.valueOf(trip.vendorId);
                this.passengerCount = String.valueOf(trip.passengerCount);
                this.pickupDatetime = trip.pickupDatetime;
                this.pickupLatitude = String.valueOf(trip.pickupLatitude);
                this.pickupLongitude = String.valueOf(trip.pickupLongitude);
                this.id = trip.id;
                this.storeAndFwdFlag = trip.storeAndFwdFlag;
                this.tripDuration = trip.tripDuration;
                this.timestamp = trip.timestamp;
                this.lon = String.valueOf(trip.lon);
                this.lat = String.valueOf(trip.lat);
                this.dropoffDatetime = trip.dropoffDatetime.map(String::valueOf).orElse("");
                this.dropoffLatitude = trip.dropoffLatitude.map(String::valueOf).orElse("");
                this.dropoffLongitude = trip.dropoffLongitude.map(String::valueOf).orElse("");
        }

}
