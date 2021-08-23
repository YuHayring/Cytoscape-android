package cn.hayring.cytoscape.bean;

import java.util.Map;

/**
 * @author Hayring
 * @date 2021/8/22
 * @description Element基类
 */
public interface BaseElement {

    /**
     * 节点
     * nodes
     */
    public static final String NODES = "nodes";

    /**
     * 边
     * edges
     */
    public static final String EDGES = "edges";

    /**
     * 类型
     * {@link #NODES 节点} 或 {@link #EDGES 边}
     */
    String getGroup();

    /**
     * 数据
     */
    Map<String, Object> getData();
}
