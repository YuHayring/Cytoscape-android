package cn.hayring.cytoscape.jsapi;

import android.webkit.JavascriptInterface;

import wendu.dsbridge.CompletionHandler;

/**
 * @author Hayring
 * @date 2021/8/22
 * @description
 */
public class BaseCaller {

    //同步API
    @JavascriptInterface
    public String testSyn(Object msg)  {
        return msg + "［syn call］";
    }

    /**
     * 异步API
     * @param msg
     * @param handler
     */
    @JavascriptInterface
    public void testAsyn(Object msg, CompletionHandler<String> handler) {
        handler.complete(msg+" [ asyn call]");
    }
}
