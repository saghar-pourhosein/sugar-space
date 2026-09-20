package com.sugarspace;

public class Disease {

    private boolean[][] allDiseasesList; //true = 1 , false = 0,

    public Disease(){
        allDiseasesList = new boolean[10][10];

        for(int i = 0 ; i < 10 ; i++){
            for(int j = 0 ; j < 10 ; j++){
                allDiseasesList[i][j] = Math.random() < 0.5;
            }
        }

    }

    public boolean[][] getAllDiseasesList(){
        return allDiseasesList;
    }

}
