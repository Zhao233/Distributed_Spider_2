package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void setZeroes(int[][] matrix) {
        List<int[]> list = new LinkedList<>();

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] == 0){
                    list.add(new int[]{i,j});
                }
            }
        }

        for(int[] temp : list){
            for(int i = 0; i < matrix.length ; i++ ){
                matrix[i][temp[1]] = 0;
            }

            for(int j = 0 ; j < matrix[0].length; j++){
                matrix[temp[0]][j] = 0;
            }
        }

        System.out.println(1);
    }


    public static void main(String[] args) throws Exception {
        int[][] num = {{1,1,1},{1,0,1},{1,1,1}};
        // -4 -1 -1 0 1 2

        setZeroes(num);

        SpringApplication.run(DemoApplication.class, args);
    }
}
