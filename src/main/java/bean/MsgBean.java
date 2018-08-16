package bean;

import java.util.List;

/**
 * Created by Asus- on 2018/8/15.
 */
public class MsgBean {
    private int carid;

    private String time;
    private List<MsgDataBean> msg;

    public int getCarid() {
        return carid;
    }

    public void setCarid(int carid) {
        this.carid = carid;
    }

    public List<MsgDataBean> getMsgDataBean() {
        return msg;
    }

    public void setMsgDataBean(List<MsgDataBean> msgDataBean) {
        this.msg = msgDataBean;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
