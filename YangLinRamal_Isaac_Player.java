//Ignore error; extends Player is requirement for assignment
class YangLinRamal_Isaac_Player extends Player{ 
    boolean opp1Def = false,opp2Def = false;  
    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
        if(n==0)    
            return 0;
        opp1Def = (oppHistory1[n-1]>0 || opp1Def); 
        opp2Def = (oppHistory2[n-1]>0 || opp2Def);
        return (opp1Def&&opp2Def)||(n>=109) ? 1 : 0; 
    }
}