package com.mashibing.channel;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * byteBuffer可读可写
 */
public class BufferReadWriteTest {
    public static void main(String[] args) {
        //连接到需要拷贝的文件
        try {
            File file = new File("D:/2345Downloads/test/liang.txt");
            FileChannel fic = new FileInputStream(file).getChannel();
            FileChannel foc = new FileOutputStream("D:/2345Downloads/test/fu.txt").getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = -1;
            while((len = fic.read(buffer))!= -1 ){
                buffer.flip();
                foc.write(buffer);
                /*
                 * 从缓存区写完之后，记得清空缓存区，不然当读写完之后，
                 * pos与limit值相等，导致len = 0,程序一直在执行
                 */
                buffer.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
