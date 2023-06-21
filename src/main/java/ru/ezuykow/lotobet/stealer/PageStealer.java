package ru.ezuykow.lotobet.stealer;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ezuykow
 */
@Component
public class PageStealer {

    private WebClient client;

    public PageStealer() {
        createWebClient();
    }

    //-----------------API START-----------------

    public HtmlPage stealPage(String URL) throws IOException {
        HtmlPage page = client.getPage(URL);
        client.waitForBackgroundJavaScript(20_000);
        return page;
    }

    public void closeWebClient() {
        client.close();
    }

    //-----------------API END-----------------

    private void createWebClient() {
        client = new WebClient(BrowserVersion.FIREFOX);
        client.setCssErrorHandler(new SilentCssErrorHandler());
        client.getOptions().setDownloadImages(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        client.getOptions().setJavaScriptEnabled(true);
    }
}
