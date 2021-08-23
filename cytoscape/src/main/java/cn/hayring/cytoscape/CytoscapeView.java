package cn.hayring.cytoscape;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

import cn.hayring.cytoscape.bean.BaseElement;
import cn.hayring.cytoscape.jsapi.BaseCaller;
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
        addJavascriptInterface(new BaseCaller(), null);
        //放大1.58倍，前端缩小为0.7，否则会有性能问题
        setInitialScale(158);
        setScrollBarSize(0);
    }

    /**
     * 启动时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartActivity() {
        super.loadUrl(INNER_HTML_URL);
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

}
