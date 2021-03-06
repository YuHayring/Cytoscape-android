package cn.hayring.cytoscape.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hayring
 * @date 2021/8/23
 * @description
 */
public class Edge implements BaseElement{


    /**
     * 类型
     * {@link #NODES 节点}
     */
    private String group = EDGES;

    /**
     * 数据
     */
    private Map<String, Object> data;


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


    public Edge() {}

    public Edge(String id, String start, String end) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("source", start);
        data.put("target", end);
        this.data = data;
    }

    public Edge(Long id, Long startId, Long endId) {
        this("e"+id.toString(), startId.toString(), endId.toString());
    }
}
