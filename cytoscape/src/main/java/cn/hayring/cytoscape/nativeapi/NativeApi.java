package cn.hayring.cytoscape.nativeapi;

import android.webkit.JavascriptInterface;

/**
 * @author Hayring
 * @date 2021/8/22
 * @description js调用原生api
 */
public interface NativeApi {

    /**
     * 页面加载完成
     */
    @JavascriptInterface
    void onCytoscapeLoaded();




}
