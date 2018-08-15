package dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus- on 2018/8/14.
 */
public class DBScan {

    private double radius;
    private int minPts;

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setMinPts(int minPts) {
        this.minPts = minPts;
    }

    public void process(List<Point> points) {
        int size = points.size();
        int idx = 0;
        int cluster = 1;
        while (idx < size) {
            Point p = points.get(idx++);  //依次遍历所有数据点
            //选择点p,未被访问的点
            if (!p.getVisit()) {
                p.setVisit(true);
                ArrayList<Point> adjacentPoints = getAdjacentPoints(p, points);

                if (adjacentPoints != null && adjacentPoints.size() < minPts) {//当到p点的距离在阙值范围内，但是这样的点个数不足minPts时，设置为噪声点
                    p.setNoised(true);
                } else {//当在阙值范围内，并且点个数大于minPts时，点p加入一个簇类
                    p.setCluster(cluster);
                    for (int i = 0; i < adjacentPoints.size(); i++) {
                        Point adjacentPoint = adjacentPoints.get(i);
                        if (!adjacentPoint.getVisit()) {  //选择未被访问的点（即未曾被选为中心点的点）
                            adjacentPoint.setVisit(true);
                            ArrayList<Point> adjacentAdjacentPoints = getAdjacentPoints(adjacentPoint, points);
                            if (adjacentAdjacentPoints != null && adjacentAdjacentPoints.size() >= minPts) {
                                adjacentPoints.addAll(adjacentAdjacentPoints); //当在阙值范围内，并且点个数大于minPts时，加入一个簇类
                            }
                        }
                        if (adjacentPoint.getCluster() == 0) {
                            adjacentPoint.setCluster(cluster); //若点不属于任何一个簇，加入当前簇类
                            if (adjacentPoint.getNoised()) {
                                adjacentPoint.setNoised(false);  //把之前设置成噪声点的点，设置成非噪声点
                            }
                        }
                    }
                    cluster++;
                }
            }
        }
    }

    /**
     * 计算中心点到其余点的距离
     *
     * @param centerPoint
     * @param points
     * @return
     */
    private ArrayList<Point> getAdjacentPoints(Point centerPoint, List<Point> points) {
        ArrayList<Point> adjacentPoints = new ArrayList<>();
        for (Point p : points) {
            //在这里也包括中心点到自己的距离
            double distance = centerPoint.getDistance(p); //计算距离
            if (distance <= radius) {
                adjacentPoints.add(p);   //记录 到中心点的距离小于半径（阙值）的点
            }
        }
        return adjacentPoints;
    }
}
