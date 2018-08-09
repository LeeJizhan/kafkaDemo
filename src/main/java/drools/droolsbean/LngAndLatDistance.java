package drools.droolsbean;

import java.text.NumberFormat;

public class LngAndLatDistance {
    public static double getLngAndLat(WeiLan start, Car end) {

        double lat1 = (Math.PI / 180) * start.getLat();
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.getLon();
        double lon2 = (Math.PI / 180) * end.longitude;

        //地球半径
        double R = 6371.004;

        //两点间距离
        double dis = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        //数字格式化对象
        NumberFormat nFormat = NumberFormat.getNumberInstance();
        if (dis < 1) {
            nFormat.setMaximumFractionDigits(1);
            dis *= 1000;

            //return  nFormat.format(dis)+"m";
            return dis;

        } else {
            nFormat.setMaximumFractionDigits(2);
            //return nFormat.format(dis)+"km";
            dis *= 1000;
            return dis;
        }

    }
}
