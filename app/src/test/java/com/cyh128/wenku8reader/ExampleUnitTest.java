package com.cyh128.wenku8reader;

import com.cyh128.wenku8reader.util.LoginWenku8;
import com.cyh128.wenku8reader.util.Wenku8Spider;

import org.junit.Test;

import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void spider_isWorking() throws IOException {
         LoginWenku8.getPageHtml("https://www.wenku8.cc/modules/article/toplist.php?sort=allvisit&page=1");
    }
}