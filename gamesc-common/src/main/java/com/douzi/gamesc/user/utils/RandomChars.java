package com.douzi.gamesc.user.utils;

import java.util.Random;

public class RandomChars {

    //随机产生的字母和数字组合（去掉字母O、I、Q和数字0）
    private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H','I', 'J',
            'K', 'L', 'M', 'N','O', 'P','Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z','0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    //随机产生的26个字母组合
    private static char[] letterSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H','I', 'J',
            'K', 'L', 'M', 'N','O', 'P','Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z' };

    //随机产生的10个数字组合
    private static char[] numberSequence = { '1', '2', '3', '4', '5', '6', '7', '8','9', '0'};

    //静态随机类
    public static Random staticRandom = null;
    public static String getRandomChars(int codeCount)
    {
        Random random = getRandomObject();
        StringBuffer randomCode = new StringBuffer();
        for (int i = 0; i < codeCount; i++)
        {
            String strRand = String.valueOf(codeSequence[random.nextInt(36)]);
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }
    public static String getRandomLetters(int codeCount)
    {
        Random random = getRandomObject();
        StringBuffer randomCode = new StringBuffer();
        for (int i = 0; i < codeCount; i++)
        {
            String strRand = String.valueOf(letterSequence[random.nextInt(26)]);
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }
    public static String getRandomNumber(int codeCount)
    {
        Random random = getRandomObject();
        StringBuffer randomCode = new StringBuffer();
        for (int i = 0; i < codeCount; i++)
        {
            String strRand = String.valueOf(numberSequence[random.nextInt(10)]);
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }
    /**
     * 获取指定数量的互不相同的数字，参数codeCount必须小于10
     * @param codeCount
     * @return
     */
    public static int[] getDiffRandomNumber(int codeCount){


        Random random = getRandomObject();

        if(codeCount>10) codeCount=10;

        int[] result = new int[codeCount];
        int[] source = {1,2,3,4,5,6,7,8,9,0};

        int length = 10;

        for(int i=0;i<codeCount;i++){

            int m=random.nextInt(length);
            int temp=source[m];
            source[m]=source[length-1];
            source[length-1]=temp;

            result[i]=temp;
            length--;
        }
        return result;
    }
    /**
     * 获取指定数量的互不相同的字母，参数codeCount必须小于26
     * @param codeCount
     * @return
     */
    public static char[] getDiffRandomLetter(int codeCount){

        Random random = getRandomObject();

        if(codeCount>26) codeCount=26;

        char[] result = new char[codeCount];
        char[] source = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H','I', 'J',
                'K', 'L', 'M', 'N','O', 'P','Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z' };

        int length = 26;

        for(int i=0;i<codeCount;i++){

            int m=random.nextInt(length);
            char temp=source[m];
            source[m]=source[length-1];
            source[length-1]=temp;

            result[i]=temp;
            length--;
        }
        return result;
    }
    /**
     * 得到单例类的Random对象
     * @return
     */
    public static Random getRandomObject(){

        if(staticRandom==null){
            staticRandom =  new Random();
        }
        return staticRandom;
    }
}
