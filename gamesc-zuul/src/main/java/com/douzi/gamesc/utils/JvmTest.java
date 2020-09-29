package com.douzi.gamesc.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class JvmTest {

    byte[] buffer = new byte[new Random().nextInt(1024*200)];
    public static void main(String[] args) {
        List<JvmTest> list = new ArrayList<JvmTest>();
        while (true){
            list.add(new JvmTest());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
