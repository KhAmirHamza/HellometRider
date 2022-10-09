package com.hellomet.rider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rider {
    public static class MetaData{

        @Expose
        @SerializedName("name")
        String name;

        @Expose
        @SerializedName("date_of_birth")
        String date_of_birth;

        @Expose
        @SerializedName("image_url")
        String image_url;

        @Expose
        @SerializedName("phone_number")
        String phone_number;

        @Expose
        @SerializedName("lat")
        double latitude;

        @Expose
        @SerializedName("lng")
        double longitude;

        public MetaData(String name, String date_of_birth, String image_url, String phone_number, double latitude, double longitude) {
            this.name = name;
            this.date_of_birth = date_of_birth;
            this.image_url = image_url;
            this.phone_number = phone_number;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate_of_birth() {
            return date_of_birth;
        }

        public void setDate_of_birth(String age) {
            this.date_of_birth = age;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    @Expose
    @SerializedName("_id")
    String id;

    @Expose
    @SerializedName("meta_data")
    MetaData meta_data;

    @Expose
    @SerializedName("auth")
    Auth auth;

    public Rider(MetaData meta_data, Auth auth) {
        this.meta_data = meta_data;
        this.auth = auth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetaData getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(MetaData meta_data) {
        this.meta_data = meta_data;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }
}
