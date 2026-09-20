package com.sugarspace;

import java.util.ArrayList;
import java.util.List;

public class Agent {

    private int x_agent;
    private int y_agent;
    private int initSugar;
    private int sugar;
    private int vision;
    private int sugarMetabolism;
    private int gender; // 0 = male , 1 = female
    private int age;
    private int maxAge;
    private List<Integer> fertileLimits;
    private int initSpice;
    private int spice;
    private int spiceMetabolism;
    private double welfare;
    private double MRS;
    private boolean[] immuneSystem; //true = 1 , false = 0,
    private List<Integer> diseaseSeverityList; // [1 , 3] , a parallel list with diseasesOfTheAgent
    private ArrayList<boolean[]> diseasesOfTheAgent; //true = 1 , false = 1 , a parallel list with diseaseSeverityList

    //---constructor----------------------------------
    public Agent(int x_agent , int y_agent , int initSugar , int vision , int sugarMetabolism , int gender , int maxAge , int atLeastFertileAge , int atMostFertileAge , int initSpice , int spiceMetabolism){
        this.x_agent = x_agent;
        this.y_agent = y_agent;
        this.initSugar = initSugar;
        this.sugar = initSugar;
        this.vision = vision;
        this.sugarMetabolism =  sugarMetabolism;
        this.gender = gender;
        this.age = 0;
        this.maxAge = maxAge;
        fertileLimits = new ArrayList<>(2);
        fertileLimits.addFirst(atMostFertileAge);
        fertileLimits.addLast(atLeastFertileAge);
        this.initSpice = initSpice;
        this.spice = initSpice;
        this.spiceMetabolism = spiceMetabolism;
        this.welfare = calculateWelfare(this.sugar , this.spice);
        this.MRS = calculateMRS(this.sugar , this.spice);
        immuneSystem = new boolean[50];

        for(int i = 0 ; i < 50 ; i++){
            immuneSystem[i] = Math.random() < 0.5;
        }

        diseaseSeverityList = new ArrayList<>();

        diseasesOfTheAgent = new ArrayList<>();
    }
    //-----------------------------------------------

    //new constructor to adding newborns to the world
    public Agent (int x_agent , int y_agent , int initSugar , int vision , int sugarMetabolism , int initSpice ,int spiceMetabolism){
        this.x_agent = x_agent;
        this.y_agent = y_agent;
        this.initSugar = initSugar;
        this.sugar = initSugar;
        this.vision = vision;
        this.sugarMetabolism = sugarMetabolism;
        this.initSpice = initSpice;
        this.spice = initSpice;
        this.spiceMetabolism = spiceMetabolism;

        this.gender = (int) (Math.random() * 2);
        this.maxAge = (int) (Math.random() * 41) + 60;
        fertileLimits = new ArrayList<>(2);
        this.fertileLimits.addFirst((int) (Math.random() * 16) + 45);
        this.fertileLimits.addLast((int) (Math.random() * 3) + 15);
        this.welfare = calculateWelfare(this.sugar , this.spice);
        this.MRS = calculateMRS(this.sugar , this.spice);
        immuneSystem = new boolean[50];

        for(int i = 0 ; i < 50 ; i++){
            immuneSystem[i] = Math.random() < 0.5;
        }

        diseaseSeverityList = new ArrayList<>();

        diseasesOfTheAgent = new ArrayList<>();
    }
    //------------------------------------------------

    //---getter methods------------------------------
    public int getInitSugar(){
        return initSugar;
    }

    public int getSugar() {
        return sugar;
    }

    public int getVision(){
        return vision;
    }

    public int getSugarMetabolism(){
        return sugarMetabolism;
    }

    public int getX_agent() {
        return x_agent;
    }

    public int getY_agent() {
        return y_agent;
    }

    public boolean isAlive(){
      return sugar >= 0 && age < maxAge && spice >= 0;
    }

    public int getGender(){
        return gender;
    }

    public int getAge(){
        return age;
    }

    public int getMaxAge(){
        return maxAge;
    }

    public int getMinFertileLimits(){
        return fertileLimits.get(1);
    }

    public int getMaxFertileLimit(){
        return fertileLimits.getFirst();
    }

    public int getInitSpice(){
        return initSpice;
    }

    public int getSpice(){
        return spice;
    }

    public int getSpiceMetabolism(){
        return spiceMetabolism;
    }

    public double getWelfare(){
        return welfare;
    }

    public double getMRS(){
        return MRS;
    }

    public boolean[] getImmuneSystem(){
        return immuneSystem;
    }

    public List<Integer> getDiseaseSeverityList(){
        return diseaseSeverityList;
    }

    public ArrayList<boolean[]> getDiseasesOfTheAgent(){
        return diseasesOfTheAgent;
    }
    //------------------------------------------------

    //--- setter methods------------------------------
    public void setPosition(int x , int y){
        this.x_agent = x;
        this.y_agent = y;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setWelfare(int sugar , int spice){
        this.welfare = calculateWelfare(sugar , spice);
    }

    public void setMRS(int sugar , int spice){
        this.MRS = calculateMRS(sugar , spice);
    }

    public void setImmuneSystem(boolean[] newImmune){
        this.immuneSystem = newImmune;
    }
    //------------------------------------------------

    public void reduceSugar(int amount){
        this.sugar -= amount;
    }

    public void reduceSpice(int amount){
        this.spice -= amount;
    }

    public void increaseSugar(int amount){
        this.sugar += amount;
    }

    public void increaseSpice(int amount){
        this.spice += amount;
    }

    public void increaseSugarMetabolism(int amount){
        this.sugarMetabolism += amount;
    }

    public void increaseSpiceMetabolism(int amount){
        this.spiceMetabolism += amount;
    }

    public void reduceSugarMetabolism(int amount){
        this.sugarMetabolism -= amount;
    }

    public void reduceSpiceMetabolism(int amount){
        this.spiceMetabolism -= amount;
    }

    //-----------------------------------------قانون M
    public void ruleM(Patch patch){
        this.sugar += patch.getSugar();
        patch.setSugar(0);
        this.sugar -= this.sugarMetabolism;

        this.spice += patch.getSpice();
        patch.setSpice(0);
        this.spice -= this.spiceMetabolism;

        this.setWelfare(this.sugar , this.spice);
        this.setMRS(this.sugar , this.spice);
    }
    //------------------------------------------------

    //---helper method for rule M---------------------
    public double calculateWelfare(int sugar, int spice){
        return  Math.pow(sugar , (double) this.sugarMetabolism / (this.sugarMetabolism + this.spiceMetabolism)) * Math.pow(spice , (double) this.spiceMetabolism / (this.sugarMetabolism + this.spiceMetabolism));
    }
    //------------------------------------------------

    //---helper method for rule T---------------------
    public double calculateMRS(int sugar , int spice) {
        if (sugar <= 0) {
            return Double.MAX_VALUE;
        }
        if (spice <= 0) {
            return Double.MIN_VALUE;
        } else {
            return (double) (this.sugarMetabolism * spice) / (this.spiceMetabolism * sugar);
        }
    }
    //------------------------------------------------

    //---helper method for rule L---------------------
    public boolean calculateAgentISAlive(int sugar , int spice){
        return sugar >= 0 && spice >= 0 && this.age <= this.maxAge;
    }
    //------------------------------------------------

    //---helper methods for rule E and F--------------
    public void addDisease(boolean[] diseasePattern){
        this.diseasesOfTheAgent.add(diseasePattern);
        int severity = (int) (Math.random() * 3 + 1);
        this.diseaseSeverityList.add(severity);
        this.increaseSugarMetabolism(severity);
        this.increaseSpiceMetabolism(severity);
    }

    public void removeDisease(int index){
        this.diseasesOfTheAgent.remove(index);
        this.diseaseSeverityList.remove(index);
    }
    //------------------------------------------------
}
