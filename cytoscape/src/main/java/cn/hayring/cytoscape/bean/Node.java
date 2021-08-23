package cn.hayring.cytoscape.bean;

import java.util.Map;

/**
 * @author Hayring
 * @date 2021/8/22
 * @description cy.add(Element);
 */
public class Node implements BaseElement{



    /**
     * 类型
     * {@link #NODES 节点}
     */
    private String group = NODES;

    /**
     * 数据
     */
    private Map<String, Object> data;

    /**
     * 坐标
     * 默认坐标: (100,100)
     * @return position
     */
    private Position position = new Position(100, 100);


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
