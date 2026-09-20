package com.sugarspace;

public class Patch {

    private final int positionX;
    private final int positionY;
    private int sugar;
    private final int maximumSugarCapacity;
    private int spice;
    private int maximumSpiceCapacity;


    //---constructor--------------------------------------
    public Patch(int x , int y , int maximumSugarCapacity , int initialSugar , int initSpice , int maximumSpiceCapacity){
        positionX = x;
        positionY = y;
        this.maximumSugarCapacity = maximumSugarCapacity;
        this.sugar = initialSugar;
        this.spice = initSpice;
        this.maximumSpiceCapacity = maximumSpiceCapacity;
    }
    //---------------------------------------------------

    //---getter methods--------------------------------
    public int getSugar(){
        return sugar;
    }

    public int getMaximumSugarCapacity(){
        return maximumSugarCapacity;
    }

    public int getPositionX(){
        return positionX;
    }

    public int getPositionY(){
        return positionY;
    }

    public int getSpice(){
        return spice;
    }

    public int getMaximumSpiceCapacity(){
        return maximumSpiceCapacity;
    }
    //----------------------------------------------------

    //---setter methods-----------------------------------
    public void setSugar(int initSugar){
        this.sugar = initSugar;
    }

    public void setSpice(int initSpice){
        this.spice = initSpice;
    }
    //----------------------------------------------------

    //---------------------------------------------قانون G
    public void addSugar(int sugarGrowBackRate){
       this.sugar = Math.min(this.sugar + sugarGrowBackRate , maximumSugarCapacity);
    }

    public void addSpice(int spiceGrowBackRate){
      this.spice = Math.min(this.spice + spiceGrowBackRate , maximumSpiceCapacity);
    }
    //-----------------------------------------------------
}

