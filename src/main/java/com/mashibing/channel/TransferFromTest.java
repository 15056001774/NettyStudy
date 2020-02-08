package com.mashibing.channel;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by hp on 2020/2/7.
 */
public class TransferFromTest {
    public static void main(String[] args) {
        try {
            FileChannel fic = new FileInputStream("D:/2345Downloads/test/liang.txt").getChannel();
            FileChannel foc = new FileOutputStream("D:/2345Downloads/test/jin.txt").getChannel();
            /**
             * 直接拷贝目标文件，底层也是通过循环读写的
             */
            foc.transferFrom(fic,0,fic.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
