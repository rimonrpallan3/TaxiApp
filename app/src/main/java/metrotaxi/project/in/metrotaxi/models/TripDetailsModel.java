package metrotaxi.project.in.metrotaxi.models;

import com.google.gson.annotations.SerializedName;

public class TripDetailsModel {

    String user;
    @SerializedName("driver_name")
    String driverName;
    @SerializedName("vehicle_number")
    String vehicleNumber;
    @SerializedName("driver_lat_long")
    String driverLatLong;

    int cash;
    @SerializedName("src_lat")
    double srcLat;
    @SerializedName("src_long")
    double srcLong;
    @SerializedName("dest_lat")
    double destLat;
    @SerializedName("dest_long")
    double destLong;
    String time;
    String from;
    double distance;
    String to;
    String date;
    String status;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDriverLatLong() {
        return driverLatLong;
    }

    public void setDriverLatLong(String driverLatLong) {
        this.driverLatLong = driverLatLong;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getSrcLat() {
        return srcLat;
    }

    public void setSrcLat(double srcLat) {
        this.srcLat = srcLat;
    }

    public double getSrcLong() {
        return srcLong;
    }

    public void setSrcLong(double srcLong) {
        this.srcLong = srcLong;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public double getDestLong() {
        return destLong;
    }

    public void setDestLong(double destLong) {
        this.destLong = destLong;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
