package com.wpx.renggie.dto;


import com.wpx.renggie.entity.OrderDetail;
import com.wpx.renggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
