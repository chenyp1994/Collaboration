package com.chenyp.collaboration.util;

import com.chenyp.collaboration.ui.activity.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Exception;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HttpPostUtil {

    private final static String randomBaseString = "-1234567890"
            + "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String FORM_CONTENT_TYPE = "multipart/form-data";// 默认Content-Type
    private final static int CONNECTION_TIME_OUT = 10 * 1000;// 连接超时为10秒
    private static String boundary = generateBoundary();// 设置分割线
    private final static String METHOD = "POST";// 默认设置为POST方法

    private Map<String, String> textParams = new HashMap<>();
    private Map<String, File> fileParams = new HashMap<>();
    private DataOutputStream dos = null;

    public HttpPostUtil() {

    }

    public interface HttpCallbackListener {
        /**
         * 服务器响应成功
         *
         * @param outputStream 二进制流数据
         */
        void onSuccess(byte[] outputStream);

        /**
         * 服务器响应失败
         *
         * @param error 错误的信息
         */
        void onError(String error);
    }

    /**
     * 增加一个字符串数据到form表单数据中
     *
     * @param name  字符串名
     * @param value 字符串值
     */
    public void addTextParameter(String name, String value) {
        textParams.put(name, value);
    }

    /**
     * 增加一个文件到form表单数据中
     *
     * @param name  文件名
     * @param value 文件路径
     */
    public void addFileParameter(String name, File value) {
        fileParams.put(name, value);
    }

    /**
     * 清空所有已添加的form表单数据
     */
    public void clearAllParameters() {
        textParams.clear();
        fileParams.clear();
    }

    /**
     * 发送请求到后台
     *
     * @param url      服务器地址
     * @param listener 监听返回接口
     */
    public void sendHttpRequest(final String url,
                                final HttpCallbackListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                ByteArrayOutputStream out = null;
                try {
                    connection = getHttpConnection(url);
                    connection.connect();
                    dos = new DataOutputStream(connection.getOutputStream());

                    // 写入数据包
                    writeFileParams();
                    writeStringParams();
                    paramsEnd();

                    String cookie = connection.getHeaderField("Set-Cookie");
                    //如果session存在，则保存sessionID;
                    if (cookie != null) {
                        BaseActivity.sessionID = cookie.substring(0, cookie.indexOf(";"));
                    }

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return;
                    }

                    InputStream in = connection.getInputStream();
                    out = new ByteArrayOutputStream();
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    in.close();
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e.getMessage());
                    }
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                        clearAllParameters();
                        try {
                            if (dos != null) {
                                dos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (listener != null) {
                            if (out != null) {
                                listener.onSuccess(out.toByteArray());
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 获得一个自定义默认的HttpURLConnection
     *
     * @param url 服务器地址
     * @return HttpURLConnection
     * @throws Exception
     */
    private HttpURLConnection getHttpConnection(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        //设置上传时候的Cookie
        conn.setRequestProperty("Cookie", BaseActivity.sessionID);
        conn.setConnectTimeout(CONNECTION_TIME_OUT);
        conn.setRequestMethod(METHOD);
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", FORM_CONTENT_TYPE + ";boundary=" + boundary);
        return conn;
    }

    /**
     * 把字符串数据写入流
     *
     * @throws Exception
     */
    private void writeStringParams() throws Exception {
        Set<String> keySet = textParams.keySet();
        for (String name : keySet) {
            String value = textParams.get(name);
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\"\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(encode(value) + "\r\n");
        }
    }

    /**
     * 把文件写入流
     *
     * @throws Exception
     */
    private void writeFileParams() throws Exception {
        Set<String> keySet = fileParams.keySet();
        for (String name : keySet) {
            File value = fileParams.get(name);
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\"; filename=\"" + encode(value.getName()) + "\"\r\n");
            dos.writeBytes("Content-Type: application/octet-stream" + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(getBytes(value));
            dos.writeBytes("\r\n");
        }
    }

    /**
     * 把文件转成字节流
     *
     * @param f 文件
     * @return byte[]
     * @throws Exception
     */
    private byte[] getBytes(File f) throws Exception {
        FileInputStream in = new FileInputStream(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = in.read(b)) != -1) {
            out.write(b, 0, n);
        }
        in.close();
        return out.toByteArray();
    }

    /**
     * 添加结尾数据
     *
     * @throws Exception
     */
    private void paramsEnd() throws Exception {
        dos.writeBytes("--" + boundary + "--" + "\r\n");
        dos.writeBytes("\r\n");
    }

    // 获取随机字符串分割线
    private static String generateBoundary() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            stringBuilder.append(randomBaseString.charAt(
                    rand.nextInt(randomBaseString.length())));
        }
        return stringBuilder.toString();
    }

    // 对字符串进行转码，转为UTF-8。服务器那边要进行一次解码
    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    /**
     * 提供一个测试的主函数
     */
    public static void main(String[] args) {
        //测试
        HttpPostUtil httpPostUtil = new HttpPostUtil();
        httpPostUtil.addTextParameter("type", "4");
        httpPostUtil.addTextParameter("id", "1");
        httpPostUtil.addTextParameter("password", "123456");
        httpPostUtil.sendHttpRequest("http://localhost:8080/Broker", new HttpCallbackListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                System.out.println(json);
                HttpPostUtil httpPostUtil = new HttpPostUtil();
                httpPostUtil.addTextParameter("type", "9");
                httpPostUtil.addTextParameter("discuss", "4");
                //httpPostUtil.addTextParameter("exhibitDetail", "1");
                //httpPostUtil.addTextParameter("detail", "百度 Android 定位 SDK 提供了 LBS 定位功能，开发者可以便捷地为应用程序添加定位功能。凭借其全球定位能力以及混合定位模式(wifi、GPS、基站等)，高德 Android 定位 SDK能够智能判断用户场景，以更快的响应速度以及更低的耗电量和网络流量消耗来实现精准的定位功能。它是全球最好用的定位SDK。");
                httpPostUtil.sendHttpRequest("http://localhost:8080/Broker", new HttpCallbackListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(byte[] outputStream) {
                        String json = new String(outputStream);
                        System.out.println(json);
                    }

                    @Override
                    public void onError(String error) {

                    }

                });
            }

            @Override
            public void onError(String error) {

            }

        });
    }
}
