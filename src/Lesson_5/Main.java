package Lesson_5;

import java.util.Arrays;

//    1. Необходимо написать два метода, которые делают следующее:
//            1) Создают одномерный длинный массив, например:
//
//    static final int size = 10000000;
//    static final int h = size / 2;
//    float[] arr = new float[size];
//
//2) Заполняют этот массив единицами;
//3) Засекают время выполнения: long a = System.currentTimeMillis();
//4) Проходят по всему массиву и для каждой ячейки считают новое значение по формуле:
//    arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
//5) Проверяется время окончания метода System.currentTimeMillis();
//6) В консоль выводится время работы: System.out.println(System.currentTimeMillis() - a);
//
//    Отличие первого метода от второго:
//    Первый просто бежит по массиву и вычисляет значения.
//    Второй разбивает массив на два массива, в двух потоках высчитывает новые значения и потом склеивает эти массивы обратно в один.
//
//    Пример деления одного массива на два:
//
//            System.arraycopy(arr, 0, a1, 0, h);
//            System.arraycopy(arr, h, a2, 0, h);
//
//    Пример обратной склейки:
//
//            System.arraycopy(a1, 0, arr, 0, h);
//            System.arraycopy(a2, 0, arr, h, h);
//
//    Примечание:
//            System.arraycopy() – копирует данные из одного массива в другой:
//            System.arraycopy(массив-источник, откуда начинаем брать данные из массива-источника, массив-назначение,
//    откуда начинаем записывать данные в массив-назначение, сколько ячеек копируем)
//    По замерам времени:
//    Для первого метода надо считать время только на цикл расчета:
//
//            for (int i = 0; i < size; i++) {
//        arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
//    }
//
//    Для второго метода замеряете время разбивки массива на 2, просчета каждого из двух массивов и склейки.

public class Main {

    static final int size = 10000000;
    static final int h = size / 4;
    float[] arr = new float[size];

    public static void main(String[] args) {
        Main main = new Main();
        main.method1();
        main.method2();
    }

    private void method1(){
        System.out.println("Первый метод");
        System.out.println("__________");
        float[] arr = new float[size];
        Arrays.fill(arr, 1.0f);
        long start = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Время выполнения метода 1: %s", String.valueOf(end - start)));
        System.out.println("__________\n");
    }

    private void method2(){
        System.out.println("Второй метод");
        System.out.println("__________");
        float[] arr = new float[size];
        float[] a1 = new float[h];
        float[] a2 = new float[h];
        float[] a3 = new float[h];
        float[] a4 = new float[h];

        Thread t1 = new Thread(() ->this.method2Full(a1, 1));
        Thread t2 = new Thread(() ->this.method2Full(a2, 2));
        Thread t3 = new Thread(() ->this.method2Full(a3, 3));
        Thread t4 = new Thread(() ->this.method2Full(a4, 4));

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        Arrays.fill(arr, 1.0f);
        long start = System.currentTimeMillis();
        System.arraycopy(arr, 0, a1, 0, h);
        System.arraycopy(arr, h, a2, 0, h);
        System.arraycopy(arr, h, a3, 0, h);
        System.arraycopy(arr, h, a4, 0, h);
        long split = System.currentTimeMillis();
        System.out.println(String.format("Время разделения массива: %s", String.valueOf(split - start)));

        try{
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e){
            System.out.println(String.format(e.getMessage()));
        }

        long con = System.currentTimeMillis();
        System.arraycopy(a1, 0, arr, 0, h);
        System.arraycopy(a2, 0, arr, h, h);
        System.arraycopy(a3, 0, arr, h, h);
        System.arraycopy(a4, 0, arr, h, h);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Время склейки массива: %s", String.valueOf(end - con)));
        System.out.println(String.format("Время выполнения метода 2: %s", String.valueOf(end - start)));
    }

    private void method2Full(float[] arr, int num){
        long start = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Время реализации потока %d: %s", num, String.valueOf(end - start)));
    }
}
