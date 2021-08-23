package cn.hayring.cytoscape.bean;

/**
 * @author Hayring
 * @date 2021/8/23
 * @description 坐标
 */
public class Position {

    /**
     * 横坐标
     */
    private Integer x;

    /**
     * 纵坐标
     */
    private Integer y;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Position(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
}
