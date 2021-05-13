package driver.transporterimenval.com.Getset;

public class DeliveryGetSet {
    private String status;
    private String orderNo;
    private String resName;
    private String orderAddress;
    private String orderAmount;
    private String orderDelivery;
    private String orderTime;
    private String orderDate;
    private String orderComision;
    private String orderTotalGeneral;
    private String order_encrypted;

    public String getOrder_encrypted() {
        return order_encrypted;
    }

    public void setOrder_encrypted(String order_encrypted) {
        this.order_encrypted = order_encrypted;
    }

    public String getOrderTotalGeneral() {
        return orderTotalGeneral;
    }

    public void setOrderTotalGeneral(String orderTotalGeneral) {
        this.orderTotalGeneral = orderTotalGeneral;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String ResName) {
        this.resName = ResName;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String OrderAddress) {
        this.orderAddress = OrderAddress;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderDelivery() {
        return orderDelivery;
    }

    public void setOrderDelivery(String orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public String getOrderComision() {
        return orderComision;
    }

    public void setOrderComision(String orderComision) {
        this.orderComision = orderComision;
    }
}
