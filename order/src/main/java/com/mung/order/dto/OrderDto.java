package com.mung.order.dto;

import com.mung.common.domain.Validate;
import com.mung.common.domain.Validate.Message;
import com.mung.common.domain.Validate.Regex;
import com.mung.common.request.BaseSearchRequest;
import com.mung.order.domain.OrderItem;
import com.mung.order.domain.OrderStatus;
import com.mung.order.dto.AddressDto.DeliveryAddressDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class OrderDto {

    @Data
    public static class OrderRequest {

        private List<OrderItemDto> orderItems = new ArrayList<>();
        private int totalPrice;

        @NotBlank(message = Validate.Message.EMPTY_TEL)
        @Size(min = 11, max = 11, message = Validate.Message.VALID_TEL)
        private String tel1;

        @Size(min = 11, max = 11, message = Validate.Message.VALID_TEL)
        private String tel2;

        @NotBlank(message = Message.NOT_EMPTY)
        @Pattern(regexp = Regex.VALID_ZIPCODE,
            message = Validate.Message.VALID_ZIPCODE)
        private String zipcode;

        @NotBlank(message = Message.NOT_EMPTY)
        private String city;

        @NotBlank(message = Message.NOT_EMPTY)
        private String street;

        @Builder
        public OrderRequest(List<OrderItemDto> orderItems, int totalPrice, String tel1, String tel2,
            String zipcode, String city, String street) {
            this.orderItems = orderItems;
            this.totalPrice = totalPrice;
            this.tel1 = tel1;
            this.tel2 = tel2;
            this.zipcode = zipcode;
            this.city = city;
            this.street = street;
        }
    }

    @Data
    public static class OrderItemDto {

        private Long productId;
        private String productName;
        private Long optionId;
        private int quantity;
        private String contents;
        private int orderPrice;
        private OrderStatus orderStatus;

        @Builder
        public OrderItemDto(Long productId, String productName, Long optionId, int quantity,
            String contents, int orderPrice, OrderStatus orderStatus) {
            this.productId = productId;
            this.productName = productName;
            this.optionId = optionId;
            this.quantity = quantity;
            this.contents = contents;
            this.orderPrice = orderPrice;
            this.orderStatus = orderStatus;
        }

        public OrderItemDto(OrderItem orderItem) {
            this.productId = orderItem.getId();
            this.productName = orderItem.getProduct().getName();
            this.optionId = orderItem.getOptions().getId();
            this.quantity = orderItem.getQuantity();
            this.contents = orderItem.getContents();
            this.orderPrice = orderItem.getOrderPrice();
            this.orderStatus = orderItem.getStatus();
        }
    }

    @Data
    public static class OrderResponse {

        private Long orderId;

        @Builder
        public OrderResponse(Long orderId) {
            this.orderId = orderId;
        }
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OrderCancelRequest {

        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private Long orderId;

        @Builder
        public OrderCancelRequest(Long orderId) {
            this.orderId = orderId;
        }
    }

    @Data
    public static class OrderCancelResponse {

        private Long orderId;

        @Builder
        public OrderCancelResponse(Long orderId) {
            this.orderId = orderId;
        }
    }

    @Data
    public static class GetOrderResponse {

        private Long orderId;
        private List<OrderItemDto> orderItems = new ArrayList<>();
        private DeliveryAddressDto deliveryAddress;
        private OrderStatus orderStatus;
        private int totalPrice;

        @Builder
        public GetOrderResponse(Long orderId, List<OrderItemDto> orderItems,
            DeliveryAddressDto deliveryAddress, OrderStatus orderStatus, int totalPrice) {
            this.orderId = orderId;
            this.orderItems = orderItems;
            this.deliveryAddress = deliveryAddress;
            this.orderStatus = orderStatus;
            this.totalPrice = totalPrice;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OrderSearchRequest extends BaseSearchRequest {

        private Long memberId;

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime orderedAtFrom;

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime orderedAtTo;

        @Builder
        public OrderSearchRequest(Long memberId, LocalDateTime orderedAtFrom,
            LocalDateTime orderedAtTo) {
            this.memberId = memberId;
            this.orderedAtFrom = orderedAtFrom;
            this.orderedAtTo = orderedAtTo;
        }
    }

    @Data
    public static class GetOrdersResponse {

        private Long orderId;
        private OrderStatus orderStatus;
        private int totalPrice;
        private LocalDateTime orderedAt;

        @Builder
        public GetOrdersResponse(Long orderId, OrderStatus orderStatus, int totalPrice,
            LocalDateTime orderedAt) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.totalPrice = totalPrice;
            this.orderedAt = orderedAt;
        }
    }
}
