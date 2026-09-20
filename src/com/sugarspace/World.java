package com.sugarspace;
import java.util.*;

public class World {

    private int sugarGrowBackInterval = 2;
    private int sugarGrowBackRate = 1;
    private int spiceGrowBackInterval = 2;
    private int spiceGrowBackRate = 1;
    private Patch[][] sugarspace;
    private List<Agent> agents = new ArrayList<>(400);
    private List<Loan> activeLoans = new ArrayList<>();
    private List<Loan> finishedLoans = new ArrayList<>();
    private int epidemicTick = 50;// لحظه ای که اپیدمی شروع میشه
    private double infectionRato = 0.00001;//5درصد از عامل های دنیا به بیماری (اپیدمی) مبتلا میشوند
    private boolean epidemicHasStarted = false;
    Loan loan;
    Disease disease;




    //---constructor--------------------------------
    public World() {
        sugarspace = new Patch[51][51];

        //------------------------------پر کردن خانه های آرایه-----
        int peakSugar1X = 12;
        int peakSugar1Y = 12;
        int peakSugar2X = 38;
        int peakSugar2Y = 38;

        int peakSpice1X = 12;
        int peakSpice1Y = 38;
        int peakSpice2X = 38;
        int peakSpice2Y = 12;

        int initSugar = 0;
        int initSpice = 0;

        for (int i = 0; i < 51; i++) {
            for (int j = 0; j < 51; j++) {

                //for sugar
                double dist1FromSugar = Math.sqrt(Math.pow(i - peakSugar1X, 2) + Math.pow(j - peakSugar1Y, 2));
                double dist2FromSugar = Math.sqrt(Math.pow(i - peakSugar2X, 2) + Math.pow(j - peakSugar2Y, 2));
                double distToNearestPeakOfSugar = Math.min(dist1FromSugar, dist2FromSugar);

                //for spice
                double dist1FromSpice = Math.sqrt(Math.pow(i - peakSpice1X, 2) + Math.pow(j - peakSpice1Y, 2));
                double dist2FromSpice = Math.sqrt(Math.pow(i - peakSpice2X, 2) + Math.pow(j - peakSpice2Y, 2));
                double distToNearestPeakOfSpice = Math.min(dist1FromSpice, dist2FromSpice);


                initSugar = (int) Math.max(1, 10 - Math.round(distToNearestPeakOfSugar) / 3.0);
                initSpice = (int) Math.max(1, 10 - Math.round(distToNearestPeakOfSpice) / 3.0);

                sugarspace[i][j] = new Patch(i, j, initSugar, initSugar, initSpice, initSpice);
            }
        }
        //---------------------------------------------------------

        //اضافه کردن یا نکردن عوامل هایی برای هر خانه به صورت تصادفی
        int posX;
        int posY;
        int initS;
        int v;
        int sugarMetabolism;
        int gender;
        int maxAge;
        int atLeastFertileAge;
        int atMostFertileAge;
        int initSp;
        int spiceMetabolism;

        for (int i = 0; i < 400; i++) {

            posX = (int) (Math.random() * 40);
            posY = (int) (Math.random() * 40);
            initS = (int) (Math.random() * 21) + 5;
            v = (int) (Math.random() * 10) + 1;
            sugarMetabolism = (int) (Math.random() * 4) + 1;
            gender = (int) (Math.random() * 2);
            maxAge = (int) (Math.random() * 41) + 60;
            atLeastFertileAge = (int) (Math.random() * 3) + 15;
            atMostFertileAge = (int) (Math.random() * 16) + 45;
            initSp = (int) (Math.random() * 21) + 5;
            spiceMetabolism = (int) (Math.random() * 4) + 1;

            agents.add(new Agent(posX, posY, initS, v, sugarMetabolism, gender, maxAge, atLeastFertileAge, atMostFertileAge, initSp, spiceMetabolism));
        }

        disease = new Disease();

    }
    //---------------------------------------------------

    //---getter methods------------------------------------
    public List<Agent> getAgents() {
        return agents;
    }

    public Agent getAgentAt(int x, int y) {

        for (Agent agent : agents) {
            if (agent.getX_agent() == x && agent.getY_agent() == y && agent.isAlive()) {
                return agent;
            }
        }
        return null;
    }

    public Patch PatchAt(int i, int j) {
        return sugarspace[i][j];
    }

    //---finding the neighbors of each agent for rule S----
    public List<Agent> getNeighbors(Agent agent) {

        List<Agent> neighborsList = new ArrayList<>();

        int y_1 = agent.getY_agent() + 1;
        int x_1 = agent.getX_agent();
        if (y_1 < 51 && isAgentAt(x_1, y_1)) {
            neighborsList.add(getAgentAt(x_1, y_1));
        }

        int y_2 = agent.getY_agent();
        int x_2 = agent.getX_agent() + 1;
        if (x_2 < 51 && isAgentAt(x_2, y_2)) {
            neighborsList.add(getAgentAt(x_2, y_2));
        }

        int y_3 = agent.getY_agent() - 1;
        int x_3 = agent.getX_agent();
        if (y_3 > -1 && isAgentAt(x_3, y_3)) {
            neighborsList.add(getAgentAt(x_3, y_3));
        }

        int y_4 = agent.getY_agent();
        int x_4 = agent.getX_agent() - 1;
        if (x_4 > -1 && isAgentAt(x_4, y_4)) {
            neighborsList.add(getAgentAt(x_4, y_4));
        }

        return neighborsList;
    }

    public boolean theDealIsValid(Agent agent, int sugar, int spice) {
        return agent.getWelfare() < agent.calculateWelfare(sugar, spice);
    }
    //-----------------------------------------------------

    public List<Loan> getActiveLoans(){
        return activeLoans;
    }

    public List<Loan> getFinishedLoans(){
        return finishedLoans;
    }
    //-----------------------------------------------------

    //قانون G و موازی سازی با سرید-------------------------
    public void G(int currentTick) {
        if (currentTick % sugarGrowBackInterval == 0 && currentTick % spiceGrowBackInterval == 0) {

            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 13; i++) {
                    for (int j = 0; j < 51; j++) {
                        sugarspace[i][j].addSugar(sugarGrowBackRate);
                        sugarspace[i][j].addSpice(spiceGrowBackRate);
                    }
                }
            });

            Thread t2 = new Thread(() -> {
                for (int i = 13; i < 26; i++) {
                    for (int j = 0; j < 51; j++) {
                        sugarspace[i][j].addSugar(sugarGrowBackRate);
                        sugarspace[i][j].addSpice(spiceGrowBackRate);
                    }
                }
            });

            Thread t3 = new Thread(() -> {
                for (int i = 26; i < 39; i++) {
                    for (int j = 0; j < 51; j++) {
                        sugarspace[i][j].addSugar(sugarGrowBackRate);
                        sugarspace[i][j].addSpice(spiceGrowBackRate);
                    }
                }
            });

            Thread t4 = new Thread(() -> {
                for (int i = 39; i < 51; i++) {
                    for (int j = 0; j < 51; j++) {
                        sugarspace[i][j].addSugar(sugarGrowBackRate);
                        sugarspace[i][j].addSpice(spiceGrowBackRate);
                    }
                }
            });

            t1.start();
            t2.start();
            t3.start();
            t4.start();

            try {
                t1.join();
                t2.join();
                t3.join();
                t4.join();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //-----------------------------------------------------

    //----------------------------------------------قانون M
    public void M(Agent agent) {
        Patch patch = null;
        int pX = agent.getX_agent();
        int pY = agent.getY_agent();

        double welfare = 0;
        double temp;
        int index = -1;
        int minDist = 0;
        //بررسی  شرایط خانه های چپ عامل برای مهاجرت------
        if (pX != 0) {
            for (int i = pX - 1; i >= Math.max(0, pX - agent.getVision()); i--) {
                if (!isAgentAt(i, pY)) {

                    temp = agent.calculateWelfare((sugarspace[i][pY].getSugar() + agent.getSugar()) - agent.getSugarMetabolism(), (sugarspace[i][pY].getSpice() + agent.getSpice()) - agent.getSpiceMetabolism());

                    if (temp > welfare) {
                        welfare = temp;
                        index = i;
                    }
                }
            }
            if (index != -1) {
                patch = sugarspace[index][pY];
                minDist = Math.abs(patch.getPositionX() - pX);
            }
        }
        //-------------------------------------------------


        //بررسی شرایط خانه های راست عامل برای مهاجرت------
        index = -1;
        if (pX < 50) {
            for (int i = pX + 1; i <= Math.min(50, pX + agent.getVision()); i++) {
                if (!isAgentAt(i, pY)) {
                    temp = agent.calculateWelfare((sugarspace[i][pY].getSugar() + agent.getSugar()) - agent.getSugarMetabolism(), (sugarspace[i][pY].getSpice() + agent.getSpice()) - agent.getSpiceMetabolism());

                    if (temp > welfare) {
                        welfare = temp;
                        index = i;
                        minDist = Math.abs(i - pX);
                    } else if (temp == welfare && minDist > Math.abs(i - pX)) {
                        index = i;
                        minDist = Math.abs(i - pX);
                    }
                }
            }

            if (index != -1) {
                patch = sugarspace[index][pY];
            }

        }
        //-------------------------------------------------

        //بررسی شرایط خانه های پایین عامل برای مهاجرت------
        if (pY != 0) {
            index = -1;
            for (int j = pY - 1; j >= Math.max(0, pY - agent.getVision()); j--) {
                if (!isAgentAt(pX, j)) {
                    temp = agent.calculateWelfare((sugarspace[pX][j].getSugar() + agent.getSugar()) - agent.getSugarMetabolism(), (sugarspace[pX][j].getSpice() + agent.getSpice()) - agent.getSpiceMetabolism());

                    if (temp > welfare) {
                        welfare = temp;
                        index = j;
                        minDist = Math.abs(j - pY);
                    } else if (temp == welfare && minDist > Math.abs(j - pY)) {
                        index = j;
                        minDist = Math.abs(j - pY);
                    }
                }
            }

            if (index != -1) {
                patch = sugarspace[pX][index];
            }
        }
        //-------------------------------------------------

        //بررسی شرایط خانه های بالا عامل برای مهاجرت------
        if (pY < 50) {
            index = -1;
            for (int j = pY + 1; j <= Math.min(50, pY + agent.getVision()); j++) {
                if (!isAgentAt(pX, j)) {
                    temp = agent.calculateWelfare((sugarspace[pX][j].getSugar() + agent.getSugar()) - agent.getSugarMetabolism(), (sugarspace[pX][j].getSpice() + agent.getSpice()) - agent.getSpiceMetabolism());

                    if (temp > welfare) {
                        welfare = temp;
                        index = j;
                        minDist = Math.abs(j - pY);
                    } else if (temp == welfare && minDist > Math.abs(j - pY)) {
                        index = j;
                        minDist = Math.abs(j - pY);
                    }
                }
            }

            if (index != -1) {
                patch = sugarspace[pX][index];
            }
        }
        //-------------------------------------------------

        if (patch != null) {
            agent.setPosition(patch.getPositionX(), patch.getPositionY());
            agent.ruleM(patch);
        } else {
            Patch currentPatch = sugarspace[pX][pY];
            agent.ruleM(currentPatch);
        }

        if (!agent.isAlive()) {
            agents.remove(agent);
        }
    }
    //-----------------------------------------------------

    //---قانون تولید مثل S---------------------------------
    public void S(Agent agent) {

        //بررسی خود عامل مورد نظر برای جفت شدن و تولید مثل
        if (agent.getSugar() >= agent.getInitSugar() && agent.getSpice() >= agent.getInitSpice() && agent.getMaxFertileLimit() > agent.getAge() && agent.getMinFertileLimits() <= agent.getAge()) {


            List<Agent> neighbors = getNeighbors(agent);

            if (neighbors == null) {
                return;
            } else {

                //انتخاب تصادفی همسایه ----------
                Collections.shuffle(neighbors);
                //------------------------------

                Agent parent = null;
                Agent child = null;

                for (int i = 0; i < neighbors.size(); i++) {

                    parent = neighbors.get(i);
                    //بررسی شرایط جفت مورد نظر برای تولید مثل
                    if (parent.getGender() != agent.getGender() && parent.getMinFertileLimits() <= parent.getAge() && parent.getMaxFertileLimit() > parent.getAge() &&
                            getEmptyPatch(parent) != null && parent.getSugar() >= parent.getInitSugar() && parent.getSpice() >= parent.getInitSpice()) {

                        //تولید مثل دو عامل و وارد شدن عامل فرزند جدید به دنیای شکر
                        child = new Agent(getEmptyPatch(parent).getPositionX(), getEmptyPatch(parent).getPositionY(), (int) Math.round((agent.getInitSugar() + parent.getInitSugar()) / 2.0),
                                (int) Math.round((agent.getVision() + parent.getVision()) / 2.0), (int) Math.round((agent.getSugarMetabolism() + parent.getSugarMetabolism()) / 2.0),
                                (int) Math.round((agent.getInitSpice() + parent.getInitSpice()) / 2.0), (int) Math.round((agent.getSpiceMetabolism() + parent.getSpiceMetabolism()) / 2.0));

                        agent.reduceSugar((int) Math.round(agent.getInitSugar() / 2.0));
                        agent.reduceSpice((int) Math.round(agent.getInitSpice() / 2.0));
                        parent.reduceSugar((int) Math.round(parent.getInitSugar() / 2.0));
                        parent.reduceSpice((int) Math.round(parent.getInitSpice() / 2.0));

                        agent.setWelfare(agent.getSugar(), agent.getSpice());
                        agent.setMRS(agent.getSugar(), agent.getSpice());
                        parent.setWelfare(parent.getSugar(), parent.getSpice());
                        parent.setMRS(parent.getSugar(), parent.getSpice());

                        if (!agent.isAlive()) {
                            agents.remove(agent);
                        }
                        if (!parent.isAlive()) {
                            agents.remove(parent);
                        }

                        agents.addLast(child);
                        break;
                        //---------------------------------------------------------

                    } else {
                        continue;
                    }
                }
            }
        } else {
            return;
        }
    }
    //-----------------------------------------------------

    //---rule T تجارت و ایجاد بازار غیرمتمرکز--------------
    public void T(Agent agent) {
        double p;
        int agentSugar = 0;
        int agentSpice = 0;
        int neighborSugar = 0;
        int neighborSpice = 0;
        boolean agentHasHigherMRS;
        double MRSAgentPrime = 0;
        double MRSNeighborPrime = 0;
        boolean orderPreserved;

        int safetyCounter;
        int maxIterations;

        boolean treadHappened = true;

        treadHappened = false;
        safetyCounter = 0;
        maxIterations = 10;

        while (treadHappened && safetyCounter < maxIterations) {
            treadHappened = false;
            safetyCounter++;

            List<Agent> neighbors = getNeighbors(agent);
            Collections.shuffle(neighbors);

            for (int i = 0; i < neighbors.size(); i++) {

                if (agent.getMRS() == neighbors.get(i).getMRS()) {
                    continue;
                }

                p = Math.sqrt(neighbors.get(i).getMRS() * agent.getMRS());

                if (neighbors.get(i).getMRS() > agent.getMRS()) {

                    agentHasHigherMRS = false;
                    if (p >= 1) {
                        agentSugar = agent.getSugar() - 1;
                        agentSpice = (int) (agent.getSpice() + p);
                        neighborSugar = neighbors.get(i).getSugar() + 1;
                        neighborSpice = (int) (neighbors.get(i).getSpice() - p);

                    } else if (p > 0) {
                        agentSugar = (int) (agent.getSugar() - (1 / p));
                        agentSpice = agent.getSpice() + 1;
                        neighborSugar = (int) (neighbors.get(i).getSugar() + (1 / p));
                        neighborSpice = neighbors.get(i).getSpice() - 1;

                    }
                } else {

                    agentHasHigherMRS = true;

                    if (p >= 1) {
                        agentSugar = agent.getSugar() + 1;
                        agentSpice = (int) (agent.getSpice() - p);
                        neighborSugar = neighbors.get(i).getSugar() - 1;
                        neighborSpice = (int) (neighbors.get(i).getSpice() + p);

                    } else if (p > 0) {
                        agentSugar = (int) (agent.getSugar() + (1 / p));
                        agentSpice = agent.getSpice() - 1;
                        neighborSugar = (int) (neighbors.get(i).getSugar() - (1 / p));
                        neighborSpice = neighbors.get(i).getSpice() + 1;

                    }
                }

                MRSAgentPrime = agent.calculateMRS(agentSugar, agentSpice);
                MRSNeighborPrime = neighbors.get(i).calculateMRS(neighborSugar, neighborSpice);

                //چک کردن معتبر بودن معادله و اینکه MRS ها از روی هم عبور نکنند تا وارد حلقه های بی پایان نشویم
                if (!agentHasHigherMRS) {
                    orderPreserved = MRSAgentPrime < MRSNeighborPrime;
                } else {
                    orderPreserved = MRSAgentPrime > MRSNeighborPrime;
                }
                //----------------------------------------------------------------------------------------------


                if (theDealIsValid(agent, agentSugar, agentSpice) && theDealIsValid(neighbors.get(i), neighborSugar, neighborSpice) && MRSAgentPrime != MRSNeighborPrime && orderPreserved) {
                    agent.setWelfare(agentSugar, agentSpice);
                    agent.setMRS(agentSugar, agentSpice);
                    neighbors.get(i).setWelfare(neighborSugar, neighborSpice);
                    neighbors.get(i).setMRS(neighborSugar, neighborSpice);

                    treadHappened = true;
                }
            }
        }
    }
    /*
    قانون E فقط برای بررسی بیماری ها با سیستم ایمنی و در صورت ایمن بودن عامل در برابر بیماری حذف آن بیماری می باشد
    نه برای اضافه کردن بیماری ای به لیست بماری های عامل
     */
    public void E(Agent agent){
        int dist = 0;
        int minDist = 12;
        int counter = 0;
        int partNo = 0;

        //ساخت یک سیستم ایمنی جدید برای عامل

        //انتخاب یک بیماری تصادفی از بین لیست بیماری های موجود
        int randomDiseaseIndex = (int)(Math.random() * 10);
        boolean[] disease1 = disease.getAllDiseasesList()[randomDiseaseIndex];
        //----------------------------------------------------

        for (int i = 0; i < agent.getImmuneSystem().length; i++) {

            if (disease1[counter] != agent.getImmuneSystem()[i]) {
                dist++;
            }

            if ((i + 1) % 10 == 0) {

                if(minDist > dist) {
                    minDist = dist;
                    partNo = (i + 1) / 10;
                }

                dist = 0;
                counter = -1;
            }

            counter++;
        }

        if(minDist != 0) {
            boolean[] newImmuneSystem = agent.getImmuneSystem().clone();
            int starterIndex = (partNo - 1) * 10;

            for(int i = starterIndex ; i < starterIndex + 10 ; i++){
                int diseaseIndex = i - starterIndex;
                if(newImmuneSystem[i] != disease1[diseaseIndex]){
                    newImmuneSystem[i] = !newImmuneSystem[i];

                    break;
                }
            }
            agent.setImmuneSystem(newImmuneSystem);
        }
        //---------------------------------

        //بعد از اینکه سیستم ایمنی تغییر کرده حالا دوباره بیماری ها را با سیستم ایمنی جدیدعامل بررسی میکنم
        for(int i = 0 ; i < disease.getAllDiseasesList().length ; i++){
            boolean[] diseasePattern = disease.getAllDiseasesList()[i];
            int hammingDist = findMinHammingDistance(agent.getImmuneSystem() , diseasePattern);

            int existingIndex = -1;
            for(int j = 0 ; j < agent.getDiseasesOfTheAgent().size() ; j++){
                if(agent.getDiseasesOfTheAgent().get(j) == diseasePattern){
                    existingIndex = j;
                    break;
                }
            }

            if(hammingDist == 0){
                //مقاومه؛ اگه از قبل مبتلا بوده، درمانش کن
                if(existingIndex != -1){
                    agent.reduceSugarMetabolism(agent.getDiseaseSeverityList().get(existingIndex));
                    agent.reduceSpiceMetabolism(agent.getDiseaseSeverityList().get(existingIndex));
                    agent.removeDisease(existingIndex);
                }
            }
        }
    }

    public void F(Agent agent){
        if(agent.getDiseasesOfTheAgent().isEmpty()){
            return;
        }

        List<Agent> neighbors = getNeighbors(agent);

        for(Agent neighbor : neighbors) {
            int randomIndex = (int) (Math.random() * agent.getDiseasesOfTheAgent().size());
            boolean[] diseaseToTransfer = agent.getDiseasesOfTheAgent().get(randomIndex);

            boolean alreadyInfected = false;
            for (boolean[] existing : neighbor.getDiseasesOfTheAgent()) {
                if (existing == diseaseToTransfer) {
                    alreadyInfected = true;
                    break;
                }
            }

            if (!alreadyInfected) {
                neighbor.addDisease(diseaseToTransfer);
            }

        }
    }

    //---helper methods-------------------------------------

    // بررسی خالی بودن یا نبودن خانه ی فضا (جهان) از عامل---
    public boolean isAgentAt(int x, int y) {
        for (Agent agent : agents) {
            if (agent.getX_agent() == x && agent.getY_agent() == y && agent.isAlive()) {
                return true;
            }
        }
        return false;
    }
    //-----------------------------------------------------

    //پیدا کردن حداقل یک خانه خالی در اطراف عامل مورد نظر
    public Patch getEmptyPatch(Agent agent){

        Patch patch = null;
        if(agent.getY_agent() + 1 < 51 && !isAgentAt(agent.getX_agent() , agent.getY_agent() + 1)){
            patch =  PatchAt(agent.getX_agent() , agent.getY_agent() + 1);

            return patch;

        } else if(agent.getX_agent() + 1 < 51 && !isAgentAt(agent.getX_agent() + 1 , agent.getY_agent())){
            patch = PatchAt(agent.getX_agent() + 1 , agent.getY_agent());

            return patch;

        } else if (agent.getY_agent() - 1 > -1 && !isAgentAt(agent.getX_agent() , agent.getY_agent() - 1)){
            patch = PatchAt(agent.getX_agent() , agent.getY_agent() - 1);

            return patch;

        } else if (agent.getX_agent() - 1 > -1 && !isAgentAt(agent.getX_agent() - 1 , agent.getY_agent())){
            patch = PatchAt(agent.getX_agent() - 1 , agent.getY_agent());

            return patch;
        }

        return patch;
    }
    //---------------------------------------------------

    //بررسی اینکه آیا عامل از قبل در فرآیند وام گیری شرکت کرده یا خیر
    public boolean hasAgentALoanInActiveLoans(Agent agent){

        for(Loan activeLoan : activeLoans){
            if(activeLoan.getBorrower() == agent || activeLoan.getLender() == agent){
                return true;
            }
        }
        return false;
    }
    //---------------------------------------------------------------

    public int findMinHammingDistance(boolean[] immune , boolean[] diseasePattern){
        int counter = 0;
        int dist = 0;
        int minDist = 11;

        for(int i = 0 ; i < immune.length ; i++){

            if (diseasePattern[counter] != immune[i]) {
                dist++;
            }

            if ((i + 1) % 10 == 0) {

                if(minDist > dist) {
                    minDist = dist;
                }

                dist = 0;
                counter = -1;
            }

            counter++;
        }

        return minDist;
    }

    public void spreadEpidemic(){
        int numberOfTheInfectedAgents = (int) (agents.size() * infectionRato);

        List<Agent> aliveAgentsList = new ArrayList<>(agents);

        Collections.shuffle(aliveAgentsList);

        for(int i = 0 ; i < numberOfTheInfectedAgents && i < aliveAgentsList.size() ; i++){

            int randomDiseaseIndex = (int) (Math.random() * 10);
            aliveAgentsList.get(i).addDisease(disease.getAllDiseasesList()[randomDiseaseIndex]);

        }
    }
    //---------------------------------------------------------------

    //اجرای تیک --------------------------------------------
    public void run(){
        int tick = 0;

        while (tick < 600){

            G(tick);

            if(tick == epidemicTick && !epidemicHasStarted){
                spreadEpidemic();
                epidemicHasStarted = true;
            }

            //---اجرای فرآیند وام دهی در هر تیک فقط برای یک بار
            List<Loan> loansToRemove = new ArrayList<>();
            for(Loan activeLoan : activeLoans){

                if(!activeLoan.getLender().isAlive() || !activeLoan.getBorrower().isAlive()){
                    finishedLoans.add(activeLoan);
                    loansToRemove.add(activeLoan);

                    continue;
                }

                if(activeLoan.getCurrentTick() + 10 > tick){
                    continue;
                }

                boolean sugarDone = activeLoan.sugarLoanPayment();
                boolean spiceDone = activeLoan.spiceLoanPayment();

                if(sugarDone && spiceDone){
                    finishedLoans.add(activeLoan);
                    loansToRemove.add(activeLoan);

                }
                else{
                    activeLoan.setCurrentTick(tick);
                }

            }

            activeLoans.removeAll(loansToRemove);
            //------------------------------------------------

            List<Agent> copyAgents = new ArrayList<>(agents);

            for(Agent agent : copyAgents) {

                if (agent.isAlive()) {

                    if(!hasAgentALoanInActiveLoans(agent)) {

                        loan = new Loan(this , tick);
                        loan.loanProcessing(agent);

                        if (loan.isMatched()) {

                            activeLoans.add(loan);
                        }
                    }

                    agent.setAge(agent.getAge() + 1);
                    E(agent);
                    F(agent);
                    T(agent);
                    M(agent);
                    S(agent);
                }
            }
            tick++;

        }
    }
    //-----------------------------------------------------

    //-----------------------------------------------------

}
