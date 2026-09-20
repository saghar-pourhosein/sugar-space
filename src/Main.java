import com.sugarspace.Agent;
import com.sugarspace.Loan;
import com.sugarspace.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Main{
    public static void main(String[] args) {
        World world = new World();

        showAnimation(world);
        drawChart(world);
    }


    //رسم نمودار جمعیت _ ثروت -------------------------------
    public static void drawChart(World world){

        StdDraw.setCanvasSize(512 , 512);
        StdDraw.setXscale(0 , 25);
        StdDraw.setYscale(0 , 400);
        StdDraw.setTitle("population _ wealth on last tick");

        List<Agent> agentsListIn600thTick = new ArrayList<>();

        agentsListIn600thTick = world.getAgents();

        System.out.println("The number of alive agents in 600th tick : " + agentsListIn600thTick.size());

        //پیدا کردن بیشترین مقدار شکر و ادویه ای (ثروت) که عاملی میتونه داشته باشه در تیک 600 ام برای نمودار x
       double maxWelfare = 0;
        for(Agent agent : agentsListIn600thTick){
            if(agent.getWelfare() > maxWelfare){

                maxWelfare = agent.getWelfare();
            }
        }
        //----------------------------------------------------------------------------------

        int bucketSize = (int) (maxWelfare/ 26) + 1;


        List<Integer> theWealthOfAgents =  new ArrayList<>(26);

        int counter = 0;
        for(int i = 0 ;  i < 26 ; i++) {

            for(Agent agent : agentsListIn600thTick){

                if((int) (agent.getWelfare() / bucketSize) == i){
                    counter++;
                }
            }
            theWealthOfAgents.addLast(counter);
            System.out.println("The height of each chart :" + theWealthOfAgents.get(i));
            counter = 0;
        }

        StdDraw.setPenColor(StdDraw.BLUE);
        for(int i  = 0 ; i < theWealthOfAgents.size() ; i++){
            StdDraw.filledRectangle(i , (double) theWealthOfAgents.get(i) / 2 , 0.2 , (double) theWealthOfAgents.get(i) / 2 );

            StdDraw.show(500);
        }
    }
    //-----------------------------------------------

    //نمایش انیمیشنی دنیای شکر-----------------------
    public static void showAnimation(World world){

        StdDraw.setCanvasSize(800 , 800);
        StdDraw.setXscale(0 , 51);
        StdDraw.setYscale(0 , 51);
        StdDraw.enableDoubleBuffering();

        //وضعیت اولیه قبل از اجرا-------------------------
        int initialCount = world.getAgents().size();
        int initialSugar = 0;
        int initialSpice = 0;
        for(Agent a : world.getAgents()){
            initialSugar += a.getSugar();
            initialSpice += a.getSpice();
        }

        System.out.println( " : تعداد عامل ها قبل از اجرا" + initialCount + "\n" + "مجموع شکر عامل ها قبل از اجرا" + initialSugar + "\n" + "مجموع ادوبه عامل ها قبل از اجرا" + initialSpice + "\n");
        //------------------------------------------------

        //رسم اولیه دنیای شکر قبل از اجرای هر گونه قانونی --------------------
        StdDraw.clear();
        int sugarPatch;
        int r1 = 255;
        int g1;
        int b1;


        int spicePatch;
        int r2;
        int g2;
        int b2 = 255;

        for(int i = 0 ; i < 51 ; i++){
            for(int j = 0 ; j < 51 ; j++){

                sugarPatch = world.PatchAt(i , j).getSugar();
                g1 = 254 - sugarPatch * 9; //160 _ 254
                b1 = 217 - sugarPatch * 20; // 12 _ 217
                StdDraw.setPenColor(r1 , g1 , b1);
                StdDraw.filledRectangle(i + 0.225 , j , 0.225 , 0.45);

                spicePatch = world.PatchAt(i , j).getSpice();
                r2 = (int) (255 - spicePatch * 4.4); //209 _ 255
                g2 = (int) (255 - spicePatch * 21.6); //39 _ 255
                StdDraw.setPenColor(r2 , g2 , b2);
                StdDraw.filledRectangle(i - 0.225 , j , 0.225 , 0.45);
            }

        }
        for(int i = 0  ; i < 51 ; i++){
            for(int j = 0 ; j < 51 ; j++){
                Agent agent = world.getAgentAt(i , j);

                if(agent != null){

                    if(agent.getAge() >= agent.getMaxFertileLimit()){
                        StdDraw.setPenColor(Color.GRAY);
                    }
                    else if(agent.getGender() == 1){
                        StdDraw.setPenColor(Color.RED);
                    }
                    else if(agent.getGender() == 0){
                        StdDraw.setPenColor(Color.BLUE);
                    }

                    StdDraw.filledCircle(i , j , 0.2);
                }
            }
        }

        StdDraw.show(1500);
        //--------------------------------------------------------

        List<Loan> activeLoans = new ArrayList<>(world.getActiveLoans());
        List<Loan> finishedLoans = new ArrayList<>(world.getFinishedLoans());
        List<Loan> loansToRemove = new ArrayList<>();

        int epidemicTick = 50;
        boolean epidemicHasStarted = false;

        int tick = 0;
        while (tick < 600){

            world.G(tick);

            if(tick == epidemicTick && !epidemicHasStarted){
                world.spreadEpidemic();
                epidemicHasStarted = true;
            }

            for(Loan activeLoan : activeLoans){


                if(!activeLoan.getBorrower().isAlive() || !activeLoan.getLender().isAlive()){
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

            List<Agent> copyList = new ArrayList<>(world.getAgents());
            for(int i = 0 ; i < copyList.size() ; i++) {

                if (copyList.get(i).isAlive()) {
                    copyList.get(i).setAge(copyList.get(i).getAge() + 1);
                    world.E(copyList.get(i));
                    world.F(copyList.get(i));
                    world.T(copyList.get(i));
                    world.M(copyList.get(i));
                    world.S(copyList.get(i));

                    if(!world.hasAgentALoanInActiveLoans(copyList.get(i))){
                        Loan loan = new Loan(world , tick);
                        loan.loanProcessing(copyList.get(i));

                        if(loan.isMatched()){
                            activeLoans.add(loan);
                        }
                    }
                }
            }

            StdDraw.clear();

            //تنظیم رنگ خانه های دنیای شکر با توجه به مقدار شکر در هر خانه
            sugarPatch = 0;
            r1 = 255;
            g1 = 0;
            b1 = 0;


            spicePatch = 0;
            r2 = 0;
            g2 = 0;
            b2 = 255;

            for(int i = 0 ; i < 51 ; i++){
                for(int j = 0 ; j < 51 ; j++){

                    //رسم نصف خانه ها برای شکر
                    sugarPatch = world.PatchAt(i , j).getSugar();
                    g1 = 254 - sugarPatch * 9; //160 _ 254
                    b1 = 217 - sugarPatch * 20; // 12 _ 217
                    StdDraw.setPenColor(r1 , g1 , b1);
                    StdDraw.filledRectangle(i + 0.225 , j , 0.225 , 0.45);
                    //-------------------------

                    //رسم نصف دیگر خانه ها برای ادویه
                    spicePatch = world.PatchAt(i , j).getSpice();
                    r2 = (int) (255 - spicePatch * 4.4); //209 _ 255
                    g2 = (int) (255 - spicePatch * 21.6); //39 _ 255
                    StdDraw.setPenColor(r2 , g2 , b2);
                    StdDraw.filledRectangle(i - 0.225 , j , 0.225 , 0.45);
                    //-------------------------------
                }

            }
            //------------------------------------------------------------

            //گذاشتن عوامل به صورت نقطه در صفحه با توجه به پراکندگی آنها و تنظیم رنگ آنها با توجه به قابلیت تولید مثل و جنسیت
            for(int i = 0  ; i < 51 ; i++){
                for(int j = 0 ; j < 51 ; j++){
                    Agent agent = world.getAgentAt(i , j);

                    if(agent != null){

                        if(agent.getAge() >= agent.getMaxFertileLimit()){
                            StdDraw.setPenColor(Color.GRAY);
                        }
                        else if(agent.getGender() == 1){
                            StdDraw.setPenColor(Color.RED);
                        }
                        else if(agent.getGender() == 0){
                            StdDraw.setPenColor(Color.BLUE);
                        }

                        StdDraw.filledCircle(i , j , 0.2);
                    }
                }
            }
            //---------------------------------------------------------

            StdDraw.show(100);
            tick++;
        }

        //وضعیت ثانویه بعد از اجرا-------------------------
        int finalCount = world.getAgents().size();
        int finalSugar = 0;
        int finalSpice = 0;
        for(Agent a : world.getAgents()){
            finalSugar += a.getSugar();
            finalSpice += a.getSpice();
        }
        System.out.println("\n" + " : تعداد عامل ها بعد از اجرا" + finalCount  + "\n" + "مجموع شکر عامل ها بعد از اجرا" + finalSugar + "\n" + "مجموع ادویه عامل ها بعد از اجر" + finalSpice);
        //--------------------------------------------------
    }
}

