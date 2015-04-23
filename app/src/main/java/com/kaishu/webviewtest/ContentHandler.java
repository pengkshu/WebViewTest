package com.kaishu.webviewtest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by 楷书 on 2015/4/20.
 */
public class ContentHandler extends DefaultHandler{

    private String nodeName;
    private StringBuffer id;
    private StringBuffer name;
    private StringBuffer version;

    @Override
    public void startDocument() throws SAXException {
        id = new StringBuffer();
        name = new StringBuffer();
        version = new StringBuffer();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //记录当前结点名
        nodeName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //根据当前的结点名判断将内容添加到哪一个StringBuilder对象中
        if("id".equals(nodeName)){
            id.append(ch, start, length);
        }else if("name".equals(nodeName)){
            name.append(ch, start, length);
        }else if("version".equals(nodeName)){
            version.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("app".equals(nodeName)){
            Log.d("ContentHandler", "id is " + id.toString().trim());
            Log.d("ContentHandler", "name is " + name.toString().trim());
            Log.d("ContentHandler", "version is " + version.toString().trim());
            //最后要将StringBuilder清空掉
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {

    }
}
