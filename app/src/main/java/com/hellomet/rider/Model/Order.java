package com.hellomet.rider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    @SerializedName("_id")
    @Expose

    String id;

    @SerializedName("prescriptionImageUrls")
    @Expose
    List<PrescriptionImageUrl> prescriptionImageUrls;

    public class PrescriptionImageUrl {

        @SerializedName("prescriptionImageUrl")
        @Expose
        String prescriptionImageUrl;

        public PrescriptionImageUrl(String prescriptionImageUrl) {
            this.prescriptionImageUrl = prescriptionImageUrl;
        }

        public String getPrescriptionImageUrl() {
            return prescriptionImageUrl;
        }

        public void setPrescriptionImageUrl(String prescriptionImageUrl) {
            this.prescriptionImageUrl = prescriptionImageUrl;
        }
    }

    @SerializedName("items")
    @Expose
    List<Item> items;
    @SerializedName("meta_data")
    @Expose
    MetaData meta_data;

    public static class MetaData {
        @SerializedName("created_at")
        @Expose
        String created_at;
        @SerializedName("rider_id")
        @Expose
        String rider_id;
        @SerializedName("rider_name")
        @Expose
        String rider_name;
        @SerializedName("rider_phone_number")
        @Expose
        String rider_phone_number;
        @SerializedName("payment_method")
        @Expose
        String payment_method;
        @SerializedName("payment_status")
        @Expose
        String payment_status;
        @SerializedName("pharmacy_address")
        @Expose
        String pharmacy_address;
        @SerializedName("pharmacy_id")
        @Expose
        String pharmacy_id;
        @SerializedName("pharmacy_lat")
        @Expose
        String pharmacy_lat;
        @SerializedName("pharmacy_lng")
        @Expose
        String pharmacy_lng;
        @SerializedName("pharmacy_name")
        @Expose
        String pharmacy_name;
        @SerializedName("pharmacy_phone_number")
        @Expose
        String pharmacy_phone_number;
        @SerializedName("requirement")
        @Expose
        String requirement;
        @SerializedName("status")
        @Expose
        String status;
        @SerializedName("total_price")
        @Expose
        String total_price;
        @SerializedName("user_address")
        @Expose
        String user_address;
        @SerializedName("user_id")
        @Expose
        String user_id;
        @SerializedName("user_lat")
        @Expose
        String user_lat;
        @SerializedName("user_lng")
        @Expose
        String user_lng;
        @SerializedName("user_name")
        @Expose
        String user_name;
        @SerializedName("user_phone_number")
        @Expose
        String user_phone_number;

        public MetaData(String pharmacy_id, String pharmacy_name, String pharmacy_address, String pharmacy_phone_number, String pharmacy_lat, String pharmacy_lng, String user_id, String user_name, String user_phone_number, String user_address, String user_lat, String user_lng, String rider_id, String rider_name, String rider_phone_number, String requirement, String total_price, String status, String payment_method, String payment_status, String created_at) {
            this.pharmacy_id = pharmacy_id;
            this.pharmacy_name = pharmacy_name;
            this.pharmacy_address = pharmacy_address;
            this.pharmacy_phone_number = pharmacy_phone_number;
            this.pharmacy_lat = pharmacy_lat;
            this.pharmacy_lng = pharmacy_lng;
            this.user_id = user_id;
            this.user_name = user_name;
            this.user_phone_number = user_phone_number;
            this.user_address = user_address;
            this.user_lat = user_lat;
            this.user_lng = user_lng;
            this.rider_id = rider_id;
            this.rider_name = rider_name;
            this.rider_phone_number = rider_phone_number;
            this.requirement = requirement;
            this.total_price = total_price;
            this.status = status;
            this.payment_method = payment_method;
            this.payment_status = payment_status;
            this.created_at = created_at;
        }

        public String getPharmacy_id() {
            return this.pharmacy_id;
        }

        public void setPharmacy_id(String pharmacy_id2) {
            this.pharmacy_id = pharmacy_id2;
        }

        public String getPharmacy_name() {
            return this.pharmacy_name;
        }

        public void setPharmacy_name(String pharmacy_name2) {
            this.pharmacy_name = pharmacy_name2;
        }

        public String getPharmacy_address() {
            return this.pharmacy_address;
        }

        public void setPharmacy_address(String pharmacy_address2) {
            this.pharmacy_address = pharmacy_address2;
        }

        public String getPharmacy_phone_number() {
            return this.pharmacy_phone_number;
        }

        public void setPharmacy_phone_number(String pharmacy_phone_number2) {
            this.pharmacy_phone_number = pharmacy_phone_number2;
        }

        public String getPharmacy_lat() {
            return this.pharmacy_lat;
        }

        public void setPharmacy_lat(String pharmacy_lat2) {
            this.pharmacy_lat = pharmacy_lat2;
        }

        public String getPharmacy_lng() {
            return this.pharmacy_lng;
        }

        public void setPharmacy_lng(String pharmacy_lng2) {
            this.pharmacy_lng = pharmacy_lng2;
        }

        public String getUser_id() {
            return this.user_id;
        }

        public void setUser_id(String user_id2) {
            this.user_id = user_id2;
        }

        public String getUser_name() {
            return this.user_name;
        }

        public void setUser_name(String user_name2) {
            this.user_name = user_name2;
        }

        public String getUser_phone_number() {
            return this.user_phone_number;
        }

        public void setUser_phone_number(String user_phone_number2) {
            this.user_phone_number = user_phone_number2;
        }

        public String getUser_address() {
            return this.user_address;
        }

        public void setUser_address(String user_address2) {
            this.user_address = user_address2;
        }

        public String getUser_lat() {
            return this.user_lat;
        }

        public void setUser_lat(String user_lat2) {
            this.user_lat = user_lat2;
        }

        public String getUser_lng() {
            return this.user_lng;
        }

        public void setUser_lng(String user_lng2) {
            this.user_lng = user_lng2;
        }

        public String getRider_id() {
            return this.rider_id;
        }

        public void setRider_id(String rider_id) {
            this.rider_id = rider_id;
        }

        public String getRider_name() {
            return this.rider_name;
        }

        public void setRider_name(String rider_name) {
            this.rider_name = rider_name;
        }

        public String getRider_phone_number() {
            return this.rider_phone_number;
        }

        public void setRider_phone_number(String rider_phone_number) {
            this.rider_phone_number = rider_phone_number;
        }

        public String getRequirement() {
            return this.requirement;
        }

        public void setRequirement(String requirement2) {
            this.requirement = requirement2;
        }

        public String getTotal_price() {
            return this.total_price;
        }

        public void setTotal_price(String total_price2) {
            this.total_price = total_price2;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status2) {
            this.status = status2;
        }

        public String getPayment_method() {
            return this.payment_method;
        }

        public void setPayment_method(String payment_method2) {
            this.payment_method = payment_method2;
        }

        public String getPayment_status() {
            return this.payment_status;
        }

        public void setPayment_status(String payment_status2) {
            this.payment_status = payment_status2;
        }

        public String getCreated_at() {
            return this.created_at;
        }

        public void setCreated_at(String created_at2) {
            this.created_at = created_at2;
        }
    }

    public static class Item {
        @SerializedName("brand")
        @Expose
        String brand;
        @SerializedName("features")
        @Expose
        String features;
        @SerializedName("medicine_id")
        @Expose
        String medicine_id;
        @SerializedName("name")
        @Expose
        String name;
        @SerializedName("price")
        @Expose
        String price;
        @SerializedName("quantity")
        @Expose
        String quantity;
        @SerializedName("sub_total")
        @Expose
        String sub_total;

        public Item(String medicine_id2, String name2, String price2, String quantity2, String brand2, String features2, String sub_total2) {
            this.medicine_id = medicine_id2;
            this.name = name2;
            this.price = price2;
            this.quantity = quantity2;
            this.brand = brand2;
            this.features = features2;
            this.sub_total = sub_total2;
        }

        public String getMedicine_id() {
            return this.medicine_id;
        }

        public void setMedicine_id(String medicine_id2) {
            this.medicine_id = medicine_id2;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }

        public String getPrice() {
            return this.price;
        }

        public void setPrice(String price2) {
            this.price = price2;
        }

        public String getQuantity() {
            return this.quantity;
        }

        public void setQuantity(String quantity2) {
            this.quantity = quantity2;
        }

        public String getBrand() {
            return this.brand;
        }

        public void setBrand(String brand2) {
            this.brand = brand2;
        }

        public String getFeatures() {
            return this.features;
        }

        public void setFeatures(String features2) {
            this.features = features2;
        }

        public String getSub_total() {
            return this.sub_total;
        }

        public void setSub_total(String sub_total2) {
            this.sub_total = sub_total2;
        }
    }


    public Order(MetaData meta_data) {
        this.meta_data = meta_data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PrescriptionImageUrl> getPrescriptionImageUrls() {
        return prescriptionImageUrls;
    }

    public void setPrescriptionImageUrls(List<PrescriptionImageUrl> prescriptionImageUrls) {
        this.prescriptionImageUrls = prescriptionImageUrls;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public MetaData getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(MetaData meta_data) {
        this.meta_data = meta_data;
    }
}
