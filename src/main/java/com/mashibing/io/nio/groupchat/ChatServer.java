package com.mashibing.io.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hp on 2020/2/8.
 */
public class ChatServer {
    private Selector selector;
    private ServerSocketChannel ssc;

    public ChatServer(){
        try {
            this.selector = Selector.open();
            this.ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress("127.0.0.1",8888));
            ssc.configureBlocking(false);
            //注册事件
            ssc.register(selector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //创建服务器端,绑定端口
        ChatServer server = new ChatServer();
        //等待客户端连接
        while(true){

            try {
                int i = server.selector.select();
                if(i>0){
                    //有事件发生
                    //获取监听的key
                    Set<SelectionKey> keys = server.selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        //移除key,防止多线程重复操作
                        iterator.remove();
                        //根据key,执行相应的处理
                        handel(key);
                    }
                }else{
                    System.out.println("等待事件中。。。");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理器
     * @param key
     */
    private static void handel(SelectionKey key) {
        if(key.isAcceptable()){
            //连接请求
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                //必须设置通道为 非阻塞，才能向 Selector 注册
                sc.configureBlocking(false);
                //为通信管道注册read事件
                sc.register(key.selector(),SelectionKey.OP_READ);
                System.out.println(sc.getRemoteAddress()+":上线了");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(key.isReadable()){
            SocketChannel sc = null;
            //读取数据请求
            try {
                sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(512);
                sc.read(buffer);
                String msg = new String(buffer.array());
                //循环写给每个sc
                for (SelectionKey k:key.selector().keys()) {
                    Channel c =  k.channel();
                    if(c instanceof SocketChannel){
                        SocketChannel cc = (SocketChannel) c;
                        cc.write(ByteBuffer.wrap(msg.getBytes()));
                    }
                }
            }catch (Exception e){
                //取消key注册,关闭通道
                try {
                    System.out.println(sc.getRemoteAddress()+"：离线了");
                    key.cancel();
                    sc.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }


}
