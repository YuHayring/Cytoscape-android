package cn.hayring.cytoscape;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import cn.hayring.cytoscape.bean.BaseElement;
import wendu.dsbridge.DWebView;

/**
 * @author Hayring
 * @date 2021/8/22
 * @description CytoscapeView
 */
public class CytoscapeView extends DWebView implements LifecycleObserver {

    /**
     * 日志tag
     */
    private static final String LOG_TAG = "CytoscapeView";

    /**
     * 内嵌html地址
     */
    private static final String INNER_HTML_URL = "file:///android_asset/index.html";

    /**
     * json数据处理
     */
    private Gson gson = new Gson();

    /**
     * 生命周期
     */
    private Lifecycle mLifecycle;

    /**
     * 结点文字用什么属性显示
     */
    private String nodeContentField = "name";

    public CytoscapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) context;
            mLifecycle = owner.getLifecycle();
            mLifecycle.addObserver(this);
        } else {
            throw new IllegalArgumentException("This view must run in a lifecycleOwner!");
        }
    }

    public CytoscapeView(Context context) {
        super(context);
    }


    /**
     * 禁止加载其他URL
     * @param url
     */
    @Override
    public void loadUrl(String url) {
        if (INNER_HTML_URL.equals(url)) {
            super.loadUrl(INNER_HTML_URL);
        } else {
            Log.e("DWebView.load(url)", "load other url is unsupported");
        }
    }

    /**
     * 禁止加载其他URL
     * @param url
     * @param additionalHttpHeaders
     */
    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (INNER_HTML_URL.equals(url)) {
            super.loadUrl(INNER_HTML_URL, additionalHttpHeaders);
        } else {
            Log.e("DWebView.load(url)", "load other url is unsupported");
        }
    }

    /**
     * 禁止加载其他URL
     * @param url url
     * @param params
     */
    @Override
    public void postUrl(String url, byte[] params) {
        if (INNER_HTML_URL.equals(url)) {
            super.postUrl(INNER_HTML_URL, params);
        } else {
            Log.e("DWebView.load(url)", "load other url is unsupported");
        }
    }


    /**
     * 创建时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateActivity() {
        getSettings().setJavaScriptEnabled(true);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        //放大1.58倍，前端缩小为0.7，否则会有性能问题
//        setInitialScale(158);
        setScrollBarSize(0);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        ViewNativeApi viewNativeApi = new ViewNativeApi();
        viewNativeApi.setCytoscapeView(this);
        addJavascriptObject(viewNativeApi, null);
    }

    /**
     * 启动时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartActivity() {
        super.loadUrl(INNER_HTML_URL + "?nodeContentField=" + nodeContentField );
    }


    /**
     * 在画布中添加结点或边
     * @param elements 元素集合
     */
    public void add(List<BaseElement> elements) {
        String json = gson.toJson(elements);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(getClass().getName(),e.getMessage());
            return;
        }
        callHandler("cy.add", new Object[]{jsonArray});
    }


    /**
     * 在画布中删除结点
     */
    public void removeNode(String id) {
        callHandler("cy.add", new Object[]{"node[id = \"" + id +"\"]"});
    }

    /**
     * 在画布中删除边
     */
    public void removeEdge(String id) {
        callHandler("cy.remove", new Object[]{"edge[id = \"" + id +"\"]"});
    }
    /**
     * 在画布中删除边
     */
    public void removeEdge(Long id) {
        callHandler("cy.remove", new Object[]{"edge[id = \"e" + id +"\"]"});
    }

    /**
     * 在画布中选中结点
     *
     * @param id
     */
    public void selectNode(String id) {
        callHandler("mycy.select", new Object[]{id, null});
    }

    public void selectNode(Long id) {
        selectNode(id.toString());
    }

    public void selectNode(Long id, Long currentId) {
        callHandler("mycy.select", new Object[]{id.toString(), currentId.toString()});
    }

    /**
     * 在画布中选中边
     *
     * @param id
     */
    public void selectEdge(String id) {
        callHandler("mycy.select", new Object[]{id, null});
    }

    public void selectEdge(Long id) {
        selectEdge('e' + id.toString());
    }

    public void selectEdge(Long id, Long currentId) {
        callHandler("mycy.select", new Object[]{'e' + id.toString(), 'e' + currentId.toString()});
    }

    /**
     * 选中与取消选中
     *
     * @param id        要选中的id
     * @param currentId 当前已选中的id
     */
    public void selectElement(String id, String currentId) {
        callHandler("mycy.select", new Object[]{id, currentId});
    }


    /**
     * 设置那个属性作为结点文字显示
     *
     * @param nodeContentField 属性名
     */
    public void setNodeContentField(String nodeContentField) {
        this.nodeContentField = nodeContentField;
    }


    public String getNodeContentField() {
        return nodeContentField;
    }


    /**
     * 元素事件监听器
     */
    public static interface OnElementClickListener {
        void onClick(String id);
    }

    /**
     * view持有的结点点击监听器
     */
    private OnElementClickListener mOnNodeClickListener = null;

    public void setmOnNodeClickListener(OnElementClickListener mOnNodeClickListener) {
        this.mOnNodeClickListener = mOnNodeClickListener;
    }

    /**
     * view持有的结点选中监听器
     */
    private OnElementClickListener mOnNodeSelectedListener = null;

    public void setmOnNodeSelectedListener(OnElementClickListener mOnNodeSelectedListener) {
        this.mOnNodeSelectedListener = mOnNodeSelectedListener;
    }

    /**
     * view持有的结点取消选中监听器
     */
    private OnElementClickListener mOnNodeUnSelectedListener = null;

    public void setmOnNodeUnSelectedListener(OnElementClickListener mOnNodeUnSelectedListener) {
        this.mOnNodeUnSelectedListener = mOnNodeUnSelectedListener;
    }

    /**
     * view持有的结点长按监听器
     */
    private OnElementClickListener mOnNodeLongClickListener;

    public void setmOnNodeLongClickListener(OnElementClickListener mOnNodeLongClickListener) {
        this.mOnNodeLongClickListener = mOnNodeLongClickListener;
    }

    /**
     * view持有的关系点击监听器
     */
    private OnElementClickListener mOnEdgeClickListener;

    public void setmOnEdgeClickListener(OnElementClickListener mOnEdgeClickListener) {
        this.mOnEdgeClickListener = mOnEdgeClickListener;
    }

    /**
     * view持有的关系选中监听器
     */
    private OnElementClickListener mOnEdgeSelectedListener;

    public void setmOnEdgeSelectedListener(OnElementClickListener mOnEdgeSelectedListener) {
        this.mOnEdgeSelectedListener = mOnEdgeSelectedListener;
    }

    /**
     * view持有的关系取消选中监听器
     */
    private OnElementClickListener mOnEdgeUnSelectedListener;

    public void setmOnEdgeUnSelectedListener(OnElementClickListener mOnEdgeUnSelectedListener) {
        this.mOnEdgeUnSelectedListener = mOnEdgeUnSelectedListener;
    }

    /**
     * view持有的关系长按监听器
     */
    private OnElementClickListener mOnEdgeLongClickListener;

    public void setmOnEdgeLongClickListener(OnElementClickListener mOnEdgeLongClickListener) {
        this.mOnEdgeLongClickListener = mOnEdgeLongClickListener;
    }

    /**
     * 加载完成监听器
     */
    private Runnable mOnCytoscapeViewLoadedListener;

    public void setOnCytoscapeViewLoadedListener(Runnable onCytoscapeViewLoadedListener) {
        this.mOnCytoscapeViewLoadedListener = onCytoscapeViewLoadedListener;
    }

    /**
     * js调用原生
     */
    public static class ViewNativeApi {

        private WeakReference<CytoscapeView> cytoscapeView;


        @JavascriptInterface
        public void onCytoscapeLoaded(Object msg) {
            cytoscapeLoadedOnView();
            cytoscapeLoaded();
        }

        /**
         * 加载完成时的回调
         */
        public void cytoscapeLoadedOnView() {
            cytoscapeView.get().
                    callHandler("setNodeContent", new Object[]{cytoscapeView.get().getNodeContentField()});
        }


        private static final String CYTOSCAPEVIEW_LOADED_LOG_MESSAGE = "onCytoscapeViewLoaded";

        public void cytoscapeLoaded() {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnCytoscapeViewLoadedListener != null) {
                Log.i(LOG_TAG, CYTOSCAPEVIEW_LOADED_LOG_MESSAGE);
                cytoscapeView.get().mOnCytoscapeViewLoadedListener.run();
            }
        }


        public void setCytoscapeView(CytoscapeView cytoscapeView) {
            this.cytoscapeView = new WeakReference<>(cytoscapeView);
        }


        private static final String NODE_CLICK_LOG_MESSAGE = "onNodeClick:";

        /**
         * 结点点击事件
         * @param msg 结点id
         */
        @JavascriptInterface
        public void onNodeClick(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnNodeClickListener != null) {
                try {
                    Log.i(LOG_TAG, NODE_CLICK_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnNodeClickListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }

        private static final String NODE_SELECTED_LOG_MESSAGE = "onNodeSelected:";

        /**
         * 结点选中事件
         * @param msg 结点id
         */
        @JavascriptInterface
        public void onNodeSelected(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnNodeSelectedListener != null) {
                try {
                    Log.i(LOG_TAG, NODE_SELECTED_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnNodeSelectedListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }

        private static final String NODE_UNSELECTED_LOG_MESSAGE = "onNodeUnSelected:";

        /**
         * 结点取消选中事件
         * @param msg 结点id
         */
        @JavascriptInterface
        public void onNodeUnSelected(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnNodeUnSelectedListener != null) {
                try {
                    Log.i(LOG_TAG, NODE_UNSELECTED_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnNodeUnSelectedListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }


        private static final String NODE_LONG_CLICK_LOG_MESSAGE = "onNodeLongClick:";


        /**
         * 结点长按事件
         * @param msg 结点id
         */
        @JavascriptInterface
        public void onNodeLongClick(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnNodeLongClickListener != null) {
                try {
                    Log.i(LOG_TAG, NODE_LONG_CLICK_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnNodeLongClickListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }


        private static final String EDGE_CLICK_LOG_MESSAGE = "onEdgeClick:";

        /**
         * 关系点击事件
         * @param msg 关系id
         */
        @JavascriptInterface
        public void onEdgeClick(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnEdgeClickListener != null) {
                try {
                    Log.i(LOG_TAG, EDGE_CLICK_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnEdgeClickListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }


        private static final String EDGE_SELECTED_LOG_MESSAGE = "onEdgeSelected:";

        /**
         * 关系选中事件
         * @param msg 关系id
         */
        @JavascriptInterface
        public void onEdgeSelected(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnEdgeSelectedListener != null) {
                try {
                    Log.i(LOG_TAG, EDGE_SELECTED_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnEdgeSelectedListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }


        private static final String EDGE_UNSELECTED_LOG_MESSAGE = "onEdgeUnSelected:";

        /**
         * 关系取消选中事件
         * @param msg 关系id
         */
        @JavascriptInterface
        public void onEdgeUnSelected(Object msg) {
            if (isLifecycleOwnerActive() && cytoscapeView.get().mOnEdgeUnSelectedListener != null) {
                try {
                    Log.i(LOG_TAG, EDGE_UNSELECTED_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnEdgeUnSelectedListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }

        private static final String EDGE_LONG_CLICK_LOG_MESSAGE = "onEdgeLongClick:";

        /**
         * 关系长按事件
         * @param msg 关系id
         */
        @JavascriptInterface
        public void onEdgeLongClick(Object msg) {
            if (isLifecycleOwnerActive() &&
                    cytoscapeView.get().mOnEdgeLongClickListener != null) {
                try {
                    Log.i(LOG_TAG, EDGE_LONG_CLICK_LOG_MESSAGE + msg);
                    cytoscapeView.get().mOnEdgeLongClickListener.onClick(msg.toString());
                } catch (NumberFormatException e) {
                    Log.e("Js Parameter error", e.getMessage());
                }
            }
        }


        /**
         * 判断生命周期是否激活
         * @return active
         */
        private boolean isLifecycleOwnerActive() {
            return cytoscapeView.get().mLifecycle.getCurrentState() == Lifecycle.State.STARTED
                    || cytoscapeView.get().mLifecycle.getCurrentState() == Lifecycle.State.RESUMED;
        }
    }
}
