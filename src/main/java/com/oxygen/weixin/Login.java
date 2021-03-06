package com.oxygen.weixin;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    /**
     * Default constructor.
     */
    public Login() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        PrintStream out = new PrintStream(response.getOutputStream());
        String token = "oxygen";
        String signature = request.getParameter("signature") + "";
        String timestamp = request.getParameter("timestamp") + "";
        String nonce = request.getParameter("nonce") + "";
        String echostr = request.getParameter("echostr") + "";
        String[] temp = new String[3];
        temp[0] = token;
        temp[1] = timestamp;
        temp[2] = nonce;
        logger.info(temp[0] + "," + temp[1] + "," + temp[2]);
        Arrays.sort(temp);
        logger.info(temp[0] + "," + temp[1] + "," + temp[2]);
        String testStr = sha1(temp[0] + temp[1] + temp[2]);
        logger.info("testStr:" + testStr);
        logger.info("signature:" + signature);
        logger.info("echoStr:" + echostr);

        if (testStr.equals(signature)) {
            out.println(echostr);
        } else {
            out.println(echostr);
        }
    }

    public static String bytetoString(byte[] digest) {
        String str = "";
        String tempStr = "";

        for (int i = 1; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                str = str + "0" + tempStr;
            } else {
                str = str + tempStr;
            }
        }
        return str.toLowerCase();
    }

    public static String sha1(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1"); // 选择SHA-1，也可以选择MD5
            byte[] digest = md.digest(inStr.getBytes()); // 返回的是byet[]，要转化为String存储比较方便
            outStr = bytetoString(digest);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }

    public static Map<String, String> parseXml(HttpServletRequest request)
            throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());

        // 释放资源
        inputStream.close();
        inputStream = null;

        return map;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        logger.info(request.getParameterMap().toString());
        PrintStream out = new PrintStream(response.getOutputStream());
        String[] nonce = request.getParameterValues("nonce");
        logger.info(Arrays.toString(nonce));
        Map<String,String> map=new HashMap<String,String>();;
        try {
             map =Login.parseXml(request);
             logger.info(map.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String fromUserName = map.get("FromUserName");
        String toUser=map.get("ToUserName");
        out.println("<xml>"
                + "<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>"
                + "<FromUserName><![CDATA["+toUser+"]]></FromUserName>"
                + "<CreateTime>"+new Date().getTime()+"</CreateTime>"
                + "<MsgType><![CDATA[text]]></MsgType>"
                + "<Content><![CDATA[hello]]></Content>" + "</xml>");
    }

}
