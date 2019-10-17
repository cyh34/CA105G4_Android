package com.android.ca105g4.order;

import java.util.List;

public class OrderTransactionVO {

    private String action;
    private OrdersVO ordersVO;
    private List<OrderDetailData> orderDetailDataList;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public OrdersVO getOrdersVO() {
        return ordersVO;
    }

    public void setOrdersVO(OrdersVO ordersVO) {
        this.ordersVO = ordersVO;
    }

    public List<OrderDetailData> getOrderDetailList() {
        return orderDetailDataList;
    }

    public void setOrderDetailList(List<OrderDetailData> orderDetailDataList) {
        this.orderDetailDataList = orderDetailDataList;
    }
}
