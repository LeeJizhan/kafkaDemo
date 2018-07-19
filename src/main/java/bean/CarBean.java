package bean;

/**
 * Created by Asus- on 2018/7/19.
 */
public class CarBean {
    //车的编号，唯一
    private String carID;
    //车品牌
    private String brand;
    //车型
    private String model;
    //车牌号
    private String number;
    //车主
    private String owner;

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    //车主电话
    private String phone;

}
