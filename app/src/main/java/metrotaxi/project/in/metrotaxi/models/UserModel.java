package metrotaxi.project.in.metrotaxi.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("user_name")
    String username = "";
    String password = "";
    String name = "";
    String email = "";
    @SerializedName("phone_number")
    String phoneNumber = "";
    String address = "";
    String fcm = "";
    @SerializedName("user_type")
    String userType = "";
    @SerializedName("vehicle_number")
    String vehicleNumber = "";
    String response = "";
    @SerializedName("license_url")
    String licenseUrl;

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getRcUrl() {
        return rcUrl;
    }

    public void setRcUrl(String rcUrl) {
        this.rcUrl = rcUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @SerializedName("rc_url")
    String rcUrl;
    @SerializedName("profile_url")
    String profileUrl;

    double lat;
    double longi;

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }



    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
