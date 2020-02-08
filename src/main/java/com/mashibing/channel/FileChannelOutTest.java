package com.mashibing.channel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 了解fileChannel使用
 */
public class FileChannelOutTest {

    public static void main(String[] args) {
        //创建文件
        File file = new File("D:/2345Downloads/test/liang.txt");
        try {
            //构建输出流
            FileOutputStream fos = new FileOutputStream(file);
            //获取channel
            FileChannel fic = fos.getChannel();
            //构建缓冲区，将内容放入缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("jklih".getBytes());
            //转换
            buffer.flip();
            //ByteBuffer buffer = ByteBuffer.wrap("ddd".getBytes());
            //写入文件
            fic.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
