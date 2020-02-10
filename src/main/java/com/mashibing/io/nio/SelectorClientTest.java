package com.mashibing.io.nio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by hp on 2020/2/8.
 */
public class SelectorClientTest extends Frame{
    TextField tf = new TextField();
    TextArea tArea = new TextArea();
    SocketChannel sc = null;
    boolean connected = false;
    Thread recv = new Thread(new ReplayThread());

    public static void main(String[] args) {
        new SelectorClientTest().launchFrame();
    }

    //构建聊天页面
    public void launchFrame(){
        //设置画面初始化所在的位置,窗口大小
        setLocation(300, 400);
        this.setSize(400, 600);
        //填充聊天面板，输入框到布局容器中
        add(tArea,BorderLayout.NORTH);
        add(tf,BorderLayout.SOUTH);
        pack();
        //窗口监听器
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //关闭与服务器的连接
                disConnect();
                //程序退出
                System.exit(0);
            }
        });
        //输入框监听事件
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = tf.getText().trim();
                tf.setText("");
                try {
                    ByteBuffer buff = ByteBuffer.wrap(str.getBytes());
                    //将数据填充到buffer中，通过sc写入
                    sc.write(buff);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        //窗口可见
        setVisible(true);
        //与服务器建立连接
        connect();
        //启动线程
        recv.start();
    }


    public void disConnect(){
        try {
            if(sc != null){
                sc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            sc = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
            if(!sc.connect(address)){
                while (!sc.finishConnect()){
                    System.out.print("非阻塞式连接服务器中。。。");
                }
            }
            connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ReplayThread implements Runnable{
        @Override
        public void run() {
           String str;
           while (connected){
               try {
                   ByteBuffer buffer = ByteBuffer.allocate(512);
                   int len = sc.read(buffer);
                   if(len != -1){
                       str = new String(buffer.array());
                       System.out.println(str);
                       tArea.setText(tArea.getText() + str + '\n');
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }

}
