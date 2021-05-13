package driver.transporterimenval.com.Getset;

public class paymentGetSet {
    private String isPaid;
    private String paymentNo;
    private String paymentAmount;
    private String orderQuantity;
    private String paymentMaxDate;
    private String paymentDate;

    public String getComplete() {
        return isPaid;
    }

    public void setComplete(String paid) {
        isPaid = paid;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getPaymentMaxDate() {
        return paymentMaxDate;
    }

    public void setPaymentMaxDate(String paymentMaxDate) {
        this.paymentMaxDate = paymentMaxDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}