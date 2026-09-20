package com.sugarspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Loan {

    private Agent borrower;
    private Agent lender;
    private int debtAmountOfSugar;
    private int debtAmountOfSpice;
    private int interest;
    private int currentTick;

    private boolean dealForSugar;
    private boolean dealForSpice;

    World world;

    public Loan(World world, int currentTick) {
        this.world = world;
        this.interest = 4;
        this.currentTick = currentTick;

        dealForSugar = false;
        dealForSpice = false;
    }

    //---getter methods------------------------------
    public Agent getBorrower() {
        return borrower;
    }

    public Agent getLender() {
        return lender;
    }

    public int getDebtAmountOfSugar() {
        return debtAmountOfSugar;
    }

    public int getDebtAmountOfSpice() {
        return debtAmountOfSpice;
    }

    private int getInterest() {
        return interest;
    }

    public int getCurrentTick() {
        return currentTick;
    }
    //----------------------------------------------

    //---setter methods-----------------------------
    public void setDebtAmountOfSugar(int amount) {
        this.debtAmountOfSugar = amount;
    }

    public void setDebtAmountOfSpice(int amount) {
        this.debtAmountOfSpice = amount;
    }

    public void setCurrentTick(int tick){
        this.currentTick = tick;
    }
    //----------------------------------------------

    //---helper methods-----------------------------
    public boolean isDealForSugar(){
        return dealForSugar;
    }

    public boolean isDealForSpice(){
        return dealForSpice;
    }

    public boolean isMatched(){
        return borrower != null && lender != null;
    }
    //----------------------------------------------

    public void loanProcessing(Agent agent) {

        boolean isDone = false;

        //بررسی اینکه آیا عامل ورودی متد وام دهنده است یا وام گیرنده


        //حالتی که عامل ورودی برای این متد یک عامل وام گیرنده است
        if (agent.getMaxFertileLimit() > agent.getAge() && agent.getMinFertileLimits() <= agent.getAge() && (agent.getSugar() == agent.getInitSugar() || agent.getSpice() == agent.getInitSugar())) {

            List<Agent> neighborsOfTheBorrower = world.getNeighbors(agent);
            Collections.shuffle(neighborsOfTheBorrower);

            List<Agent> lenderNeighbors = new ArrayList<>();

            //پیدا کردن همسایه های وام دهنده ی عامل ورودی وام گیرنده
            for (Agent value : neighborsOfTheBorrower) {

                if ((value.getMaxFertileLimit() <= value.getAge() && value.getMinFertileLimits() > value.getAge()) || (value.getMaxFertileLimit() > value.getAge() && value.getMinFertileLimits() <= value.getAge() && (value.getSugar() > value.getInitSugar() || value.getSpice() > value.getInitSpice()))) {
                    lenderNeighbors.add(value);
                }
            }
            //--------------------------------------------------------

            setDebtAmountOfSugar((int) (Math.random() * 5 + 1));
            setDebtAmountOfSpice((int) (Math.random() * 5 + 1));

            for (Agent lenderNeighbor : lenderNeighbors) {

                //همسایه ی وام دهنده ی بارور با شکر یا ادویه اضافی
                if (lenderNeighbor.getMaxFertileLimit() > lenderNeighbor.getAge() && lenderNeighbor.getMinFertileLimits() <= lenderNeighbor.getAge() && (lenderNeighbor.getSugar() > lenderNeighbor.getInitSugar() || lenderNeighbor.getSpice() > lenderNeighbor.getInitSpice())) {

                    //بررسی اینکه اگر عامل وام گیرنده هم شکر بخواهد و هم ادویه و وام دهنده نیز قادر یه دادن هر دو باشد
                    if (agent.getSpice() == agent.getInitSpice() && agent.getSugar() == agent.getInitSugar() && lenderNeighbor.getSpice() - debtAmountOfSpice > lenderNeighbor.getInitSpice() && lenderNeighbor.getSugar() - debtAmountOfSugar > lenderNeighbor.getInitSugar()) {
                        lenderNeighbor.reduceSugar(debtAmountOfSugar);
                        lenderNeighbor.reduceSpice(debtAmountOfSpice);
                        agent.increaseSugar(debtAmountOfSugar);
                        agent.increaseSpice(debtAmountOfSpice);

                        dealForSugar = true;
                        dealForSpice = true;

                        isDone = true;
                    }
                    //------------------------------------------------------

                    //همسایه ی وام دهنده با شکر اضافی / بررسی اینکه آیا همسایه ی وام دهنده قادر به دادن شکر کافی به وام گیرنده هست یا خیر
                    else if (agent.getSugar() == agent.getInitSugar() && lenderNeighbor.getSugar() > lenderNeighbor.getInitSugar() && lenderNeighbor.getSugar() - debtAmountOfSugar > lenderNeighbor.getInitSugar()) {
                        lenderNeighbor.reduceSugar(debtAmountOfSugar);
                        agent.increaseSugar(debtAmountOfSugar);

                        dealForSugar = true;

                        isDone = true;
                    }
                    //-----------------------------------------------------

                    //همسایه ی وام دهنده با ادویه اضافی / بررسی اینکه آیا همسایه ی وام دهنده قادر به دادن ادویه اضافی به وام گیرنده هست یا خیر
                    else if (agent.getSpice() == agent.getInitSpice() && lenderNeighbor.getSpice() > lenderNeighbor.getInitSpice() && lenderNeighbor.getSpice() - debtAmountOfSpice > lenderNeighbor.getInitSpice()) {
                        lenderNeighbor.reduceSpice(debtAmountOfSpice);
                        agent.increaseSpice(debtAmountOfSpice);

                        dealForSpice = true;

                        isDone = true;
                    }
                    //------------------------------------------------------

                    lenderNeighbor.setWelfare(lenderNeighbor.getSugar(), lenderNeighbor.getSpice());
                    lenderNeighbor.setMRS(lenderNeighbor.getSugar(), lenderNeighbor.getSpice());
                    agent.setWelfare(agent.getSugar(), agent.getSpice());
                    agent.setMRS(agent.getSugar(), agent.getSpice());
                }
                //---------------------------------------------------------

                //همسایه ی وام دهنده ی نابارور
                else if (lenderNeighbor.getMaxFertileLimit() <= lenderNeighbor.getAge() && lenderNeighbor.getMinFertileLimits() > lenderNeighbor.getAge()) {

                    //بررسی زنده بودن همسایه ی وام دهنده نابارور در صورت دادن وام مورد نیاز و همچنین بررسی اینکه وام مورد نیاز عامل وام گیرنده از نصف شکر یا ادویه همسایه ی وام دهنده کمتر یا مساوی باشد تا وام دادن بتواند صورت بگیرد
                    if (lenderNeighbor.calculateAgentISAlive(lenderNeighbor.getSugar() / 2, lenderNeighbor.getSpice() / 2) && (debtAmountOfSugar <= lenderNeighbor.getSugar() / 2 || debtAmountOfSpice <= lenderNeighbor.getSpice() / 2)) {

                        if (agent.getSugar() == agent.getInitSugar() && agent.getSpice() == agent.getInitSpice()) {
                            lenderNeighbor.reduceSugar(debtAmountOfSugar);
                            lenderNeighbor.reduceSpice(debtAmountOfSpice);
                            agent.increaseSugar(debtAmountOfSugar);
                            agent.increaseSpice(debtAmountOfSpice);

                            dealForSugar = true;
                            dealForSpice = true;

                            isDone = true;

                        } else if (agent.getSugar() == agent.getInitSugar()) {
                            lenderNeighbor.reduceSugar(debtAmountOfSugar);
                            agent.increaseSugar(debtAmountOfSugar);

                            dealForSugar = true;

                            isDone = true;

                        } else if (agent.getSpice() == agent.getInitSpice()) {
                            lenderNeighbor.reduceSpice(debtAmountOfSpice);
                            agent.increaseSpice(debtAmountOfSpice);

                            dealForSpice = true;

                            isDone = true;
                        }
                    }
                }
                //درصورتی که وام مورد نظر گرفته شد و مشکل حل شد از همسایه های دیگر وام گرفته نمیشود
                if (isDone) {
                    lenderNeighbor.setWelfare(lenderNeighbor.getSugar(), lenderNeighbor.getSpice());
                    lenderNeighbor.setMRS(lenderNeighbor.getSugar(), lenderNeighbor.getSpice());
                    agent.setWelfare(agent.getSugar(), agent.getSpice());
                    agent.setMRS(agent.getSugar(), agent.getSpice());

                    this.borrower = agent;
                    this.lender = lenderNeighbor;

                    break;
                }
                //--------------------------------------------------------------------
            }
        }

        //حالتی که عامل ورودی برای این متد یک عامل وام دهنده است
        else if ((agent.getMaxFertileLimit() <= agent.getAge() && agent.getMinFertileLimits() > agent.getAge()) || (agent.getMaxFertileLimit() > agent.getAge() && agent.getMinFertileLimits() <= agent.getAge() && (agent.getSugar() > agent.getInitSugar() || agent.getSpice() > agent.getInitSpice()))) {

            List<Agent> neighborsOfTheLender = world.getNeighbors(agent);
            Collections.shuffle(neighborsOfTheLender);

            List<Agent> borrowerNeighbors = new ArrayList<>();

            //پیدا کردن همسایه های وام گیرنده ی عامل ورودی وام گیرنده
            for (Agent value : neighborsOfTheLender) {

                if ((value.getMaxFertileLimit() <= value.getAge() && value.getMinFertileLimits() > value.getAge()) || (value.getMaxFertileLimit() > value.getAge() && value.getMinFertileLimits() <= value.getAge() && (value.getSugar() > value.getInitSugar() || value.getSpice() > value.getInitSpice()))) {
                    borrowerNeighbors.add(value);
                }
            }
            //--------------------------------------------------------

            setDebtAmountOfSugar((int) (Math.random() * 5 + 1));
            setDebtAmountOfSpice((int) (Math.random() * 5 + 1));

            for (Agent borrowerNeighbor : borrowerNeighbors) {

                //همسایه ی وام گیرنده
                if (borrowerNeighbor.getMaxFertileLimit() > borrowerNeighbor.getAge() && borrowerNeighbor.getMinFertileLimits() <= borrowerNeighbor.getAge() && (borrowerNeighbor.getSugar() == borrowerNeighbor.getInitSugar() || borrowerNeighbor.getSpice() == borrowerNeighbor.getInitSpice())) {

                    // حالتی که عامل وام دهنده ورودی متد یک عامل نابارور باشد و قادر باشد نیمی از ثروت خود را به عنوان وام بدهد بدون اینکه بمیرد
                    if(agent.getMaxFertileLimit() <= agent.getAge() && agent.getMinFertileLimits() > agent.getAge() && (agent.getSugar() / 2 >= debtAmountOfSugar || agent.getSpice() / 2 >= debtAmountOfSpice)){

                        //حالتی که همسایه ی وام گیرنده به شکر و ادویه نیاز دارد
                        if(borrowerNeighbor.getSugar() == borrowerNeighbor.getInitSugar() && borrowerNeighbor.getSpice() == borrowerNeighbor.getInitSpice()){
                            if(agent.calculateAgentISAlive(agent.getSugar() - debtAmountOfSugar , agent.getSpice() - debtAmountOfSpice)){

                                agent.reduceSugar(debtAmountOfSugar);
                                agent.reduceSpice(debtAmountOfSpice);
                                borrowerNeighbor.increaseSugar(debtAmountOfSugar);
                                borrowerNeighbor.increaseSpice(debtAmountOfSpice);

                                dealForSugar = true;
                                dealForSpice = true;

                                isDone = true;
                            }
                        }

                        //حالتی که همسایه ی وام گیرنده فقط به شکر نیاز دارد
                        else if(borrowerNeighbor.getSugar() == borrowerNeighbor.getInitSugar()){
                            if(agent.calculateAgentISAlive(agent.getSugar() - debtAmountOfSugar , agent.getSpice())){

                                agent.reduceSugar(debtAmountOfSugar);
                                borrowerNeighbor.increaseSugar(debtAmountOfSugar);

                                dealForSugar = true;

                                isDone = true;
                            }
                        }

                        //حالتی که همسایه ی وام گیرنده فقط به ادویه نیاز دارد
                        else if(borrowerNeighbor.getSpice() == borrowerNeighbor.getInitSpice()){
                            if(agent.calculateAgentISAlive(agent.getSugar() , agent.getSpice() - debtAmountOfSpice)){

                                agent.reduceSpice(debtAmountOfSpice);
                                borrowerNeighbor.increaseSpice(debtAmountOfSpice);

                                dealForSpice = true;

                                isDone = true;
                            }
                        }

                    }
                    //حالتی که عامل  وام دهنده ی ورودی متد یک عامل بارور با ثروت اضافی باشد
                    else if(agent.getMaxFertileLimit() > agent.getAge() && agent.getMinFertileLimits() <= agent.getAge() && (agent.getSugar() - debtAmountOfSugar > agent.getInitSugar() || agent.getSpice() - debtAmountOfSpice > agent.getInitSpice())){

                        //حالتی که همسایه ی وام گیرنده هم شکر و هم ادویه نیاز داشته باشد و وام دهنده نیز قادر به دادن هر دو باشد
                        if(borrowerNeighbor.getSugar() == borrowerNeighbor.getInitSugar() && borrowerNeighbor.getSpice() == borrowerNeighbor.getInitSpice() && agent.getSugar() - debtAmountOfSugar> agent.getInitSugar() && agent.getSpice() - debtAmountOfSpice > agent.getInitSpice()){

                            agent.reduceSugar(debtAmountOfSugar);
                            agent.reduceSpice(debtAmountOfSpice);
                            borrowerNeighbor.increaseSugar(debtAmountOfSugar);
                            borrowerNeighbor.increaseSpice(debtAmountOfSpice);

                            dealForSugar = true;
                            dealForSpice = true;

                            isDone = true;
                        }

                        //حالتی که همسایه ی وام گیرنده فقط به شکر نیاز داشته باشد و وام دهنده نیز قادر به دادن شکر باشد
                        else if (borrowerNeighbor.getSugar() == borrowerNeighbor.getInitSugar() && agent.getSugar() - debtAmountOfSugar > agent.getInitSugar()){

                            agent.reduceSugar(debtAmountOfSugar);
                            borrowerNeighbor.increaseSugar(debtAmountOfSugar);

                            dealForSugar = true;

                            isDone = true;
                        }

                        //حالتی که همسایه ی وام گیرنده فقط به ادویه نیاز داشته باشد و وام دهنده نیز قادر به دادن ادویه باشد
                        else if (borrowerNeighbor.getSpice() == borrowerNeighbor.getInitSpice() && agent.getSpice() - debtAmountOfSpice > agent.getInitSpice()){

                            agent.reduceSpice(debtAmountOfSpice);
                            borrowerNeighbor.increaseSpice(debtAmountOfSpice);

                            dealForSpice = true;

                            isDone = true;
                        }

                    }

                    if(isDone){
                        agent.setWelfare(agent.getSugar() , agent.getSpice());
                        agent.setMRS(agent.getSugar() , agent.getSpice());
                        borrowerNeighbor.setWelfare(borrowerNeighbor.getSugar() , borrowerNeighbor.getSpice());
                        borrowerNeighbor.setMRS(borrowerNeighbor.getSugar() , borrowerNeighbor.getSpice());

                        this.borrower = borrowerNeighbor;
                        this.lender = agent;

                        break;
                    }
                }
            }
        }
    }

    public boolean sugarLoanPayment(){

        if(!isDealForSugar()){
            return true;
        }

        if (!borrower.isAlive() || !lender.isAlive()){
            return true;
        }
        else if (isDealForSugar() && borrower.getSugar() - (debtAmountOfSugar * (1 + interest)) > 0){
            borrower.reduceSugar(debtAmountOfSugar * (1 + interest));
            lender.increaseSugar(debtAmountOfSugar * (1 + interest));

            borrower.setWelfare(borrower.getSugar() , borrower.getSpice());
            borrower.setMRS(borrower.getSugar() , borrower.getSpice());
            lender.setWelfare(lender.getSugar() , lender.getSpice());
            lender.setMRS(lender.getSugar() , lender.getSpice());

            return true;
        }
        else if (isDealForSugar() && borrower.getSugar() - (debtAmountOfSugar * (1 + interest)) <= 0){

            int remainingSugar = (debtAmountOfSugar * (interest + 1)) - borrower.getSugar() / 2;

            lender.increaseSugar(borrower.getSugar() / 2);
            borrower.reduceSugar(borrower.getSugar() / 2);

            debtAmountOfSugar = remainingSugar;

            borrower.setWelfare(borrower.getSugar() , borrower.getSpice());
            borrower.setMRS(borrower.getSugar() , borrower.getSpice());
            lender.setWelfare(lender.getSugar() , lender.getSpice());
            lender.setMRS(lender.getSugar() , lender.getSpice());
        }

        return false;
    }

    public boolean spiceLoanPayment(){

        if(!isDealForSpice()){
            return true;
        }

        if (!borrower.isAlive() || !lender.isAlive()){
            return true;
        }
        else if (isDealForSpice() && borrower.getSpice() - (debtAmountOfSpice * (1 + interest)) > 0){
            borrower.reduceSpice(debtAmountOfSpice * (1 + interest));
            lender.increaseSpice(debtAmountOfSpice * (1 + interest));

            borrower.setWelfare(borrower.getSugar() , borrower.getSpice());
            borrower.setMRS(borrower.getSugar() , borrower.getSpice());
            lender.setWelfare(lender.getSugar() , lender.getSpice());
            lender.setMRS(lender.getSugar() , lender.getSpice());

            return true;
        }
        else if (isDealForSpice() && borrower.getSpice() - (debtAmountOfSpice * (1 + interest)) <= 0){

            int remainingSpice = (debtAmountOfSpice * (interest + 1)) - borrower.getSpice() / 2;

            lender.increaseSpice(borrower.getSpice() / 2);
            borrower.reduceSpice(borrower.getSpice() / 2);

            debtAmountOfSpice = remainingSpice;

            borrower.setWelfare(borrower.getSugar() , borrower.getSpice());
            borrower.setMRS(borrower.getSugar() , borrower.getSpice());
            lender.setWelfare(lender.getSugar() , lender.getSpice());
            lender.setMRS(lender.getSugar() , lender.getSpice());
        }

        return false;
    }
    
}


