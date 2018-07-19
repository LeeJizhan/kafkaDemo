package data;

import bean.CarBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus- on 2018/7/19.
 */
public class CarData {
    //珠海车
    private static final String ZHU_HAI_CAR_BRAND = "保时捷";
    private static final String ZHU_HAI_CAR_MODEL = "911";
    private static final String ZHU_HAI_CAR_NUMBER = "粤C00000";
    private static final String ZHU_HAI_CAR_OWNER = "珠海富豪SQ";
    private static final String ZHU_HAI_CAR_PHONE = "13988888888";

    //北京车
    private static final String BEI_JING_CAR_BRAND = "法拉利";
    private static final String BEI_JING_CAR_MODEL = "458";
    private static final String BEI_JING_CAR_NUMBER = "京A00000";
    private static final String BEI_JING_CAR_OWNER = "北京富豪Deeven";
    private static final String BEI_JING_CAR_PHONE = "13888888888";

    //上海车
    private static final String SHANG_HAI_CAR_BRAND = "阿斯顿·马丁";
    private static final String SHANG_HAI_CAR_MODEL = "Lagonda";
    private static final String SHANG_HAI_CAR_NUMBER = "沪A00000";
    private static final String SHANG_HAI_CAR_OWNER = "上海富豪TONY";
    private static final String SHANG_HAI_CAR_PHONE = "18888888888";

    //广州车
    private static final String GUANG_ZHOU_CAR_BRAND = "兰博基尼";
    private static final String GUANG_ZHOU_CAR_MODEL = "Centenario";
    private static final String GUANG_ZHOU_CAR_NUMBER = "粤A00000";
    private static final String GUANG_ZHOU_CAR_OWNER = "广州富豪TONY";
    private static final String GUANG_ZHOU_CAR_PHONE = "15988888888";

    //深圳车
    private static final String SHEN_ZHEN_CAR_BRAND = "迈凯伦";
    private static final String SHEN_ZHEN_CAR_MODEL = "720S";
    private static final String SHEN_ZHEN_CAR_NUMBER = "粤B00000";
    private static final String SHEN_ZHEN_CAR_OWNER = "深圳富豪William";
    private static final String SHEN_ZHEN_CAR_PHONE = "18988888888";

    private List<CarBean> carBeans = new ArrayList<CarBean>();

    public List<CarBean> getCarData() {
        for (int i = 1; i <= 1000; i++) {
            CarBean carBean = new CarBean();
            if (i <= 200) {
                carBean.setCarID(Integer.toString(i));
                carBean.setBrand(ZHU_HAI_CAR_BRAND);
                carBean.setModel(ZHU_HAI_CAR_MODEL);
                carBean.setNumber(ZHU_HAI_CAR_NUMBER);
                carBean.setOwner(ZHU_HAI_CAR_OWNER);
                carBean.setPhone(ZHU_HAI_CAR_PHONE);
            } else if (i > 200 && i <= 400) {
                carBean.setCarID(Integer.toString(i));
                carBean.setBrand(SHANG_HAI_CAR_BRAND);
                carBean.setModel(SHANG_HAI_CAR_MODEL);
                carBean.setNumber(SHANG_HAI_CAR_NUMBER);
                carBean.setOwner(SHANG_HAI_CAR_OWNER);
                carBean.setPhone(SHANG_HAI_CAR_PHONE);
            } else if (i > 400 && i <= 600) {
                carBean.setCarID(Integer.toString(i));
                carBean.setBrand(BEI_JING_CAR_BRAND);
                carBean.setModel(BEI_JING_CAR_MODEL);
                carBean.setNumber(BEI_JING_CAR_NUMBER);
                carBean.setOwner(BEI_JING_CAR_OWNER);
                carBean.setPhone(BEI_JING_CAR_PHONE);
            } else if (i > 600 && i <= 800) {
                carBean.setCarID(Integer.toString(i));
                carBean.setBrand(GUANG_ZHOU_CAR_BRAND);
                carBean.setModel(GUANG_ZHOU_CAR_MODEL);
                carBean.setNumber(GUANG_ZHOU_CAR_NUMBER);
                carBean.setOwner(GUANG_ZHOU_CAR_OWNER);
                carBean.setPhone(GUANG_ZHOU_CAR_PHONE);
            } else if (i > 800 && i <= 1000) {
                carBean.setCarID(Integer.toString(i));
                carBean.setBrand(SHEN_ZHEN_CAR_BRAND);
                carBean.setModel(SHEN_ZHEN_CAR_MODEL);
                carBean.setNumber(SHEN_ZHEN_CAR_NUMBER);
                carBean.setOwner(SHANG_HAI_CAR_OWNER);
                carBean.setPhone(SHANG_HAI_CAR_PHONE);
            }
            carBeans.add(carBean);
        }
        return carBeans;
    }
}
