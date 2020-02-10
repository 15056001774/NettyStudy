package com.mashibing.io.bio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("192.168.43.100", 8888));
        while(true) {
            Socket s = ss.accept(); //阻塞方法
            //以多线程的方式处理客户端交互
            new Thread(() -> {
                handle(s);
            }).start();
        }

    }

    static void handle(Socket s) {
        try {
            byte[] bytes = new byte[1024];
            int len = s.getInputStream().read(bytes);
            System.out.println(new String(bytes, 0, len));

            s.getOutputStream().write(bytes, 0, len);
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
