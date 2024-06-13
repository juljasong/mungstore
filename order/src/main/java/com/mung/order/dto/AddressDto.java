package com.mung.order.dto;

import com.mung.order.domain.Delivery;
import com.mung.order.domain.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

public class AddressDto {

    @Data
    public static class DeliveryAddressDto {

        private String shipper;
        private String trackingNo;
        private String tel1;
        private String tel2;
        private String zipcode;
        private String city;
        private String street;
        private DeliveryStatus status;

        @Builder
        public DeliveryAddressDto(String shipper, String trackingNo, String tel1, String tel2,
            String zipcode, String city, String street, DeliveryStatus status) {
            this.shipper = shipper;
            this.trackingNo = trackingNo;
            this.tel1 = tel1;
            this.tel2 = tel2;
            this.zipcode = zipcode;
            this.city = city;
            this.street = street;
            this.status = status;
        }

        public DeliveryAddressDto(Delivery delivery) {
            this.shipper = delivery.getShipper();
            this.trackingNo = delivery.getTrackingNo();
            this.tel1 = delivery.getTel1();
            this.tel2 = delivery.getTel2();
            this.zipcode = delivery.getAddress().getZipcode();
            this.city = delivery.getAddress().getCity();
            this.street = delivery.getAddress().getStreet();
            this.status = delivery.getStatus();
        }
    }

}
