public class ThreePrisonersDilemma {

	/*
	 This Java program models the two-player Prisoner's Dilemma game.
	 We use the integer "0" to represent cooperation, and "1" to represent
	 defection.

	 Recall that in the 2-players dilemma, U(DC) > U(CC) > U(DD) > U(CD), where
	 we give the payoff for the first player in the list. We want the three-player game
	 to resemble the 2-player game whenever one player's response is fixed, and we
	 also want symmetry, so U(CCD) = U(CDC) etc. This gives the unique ordering

	 U(DCC) > U(CCC) > U(DDC) > U(CDC) > U(DDD) > U(CDD)

	 The payoffs for player 1 are given by the following matrix: */

	static int[][][] payoff = {
		{{6,3},  //payoffs when first and second players cooperate
		 {3,0}}, //payoffs when first player coops, second defects
		{{8,5},  //payoffs when first player defects, second coops
	     {5,2}}};//payoffs when first and second players defect
	//{{{6,3},{3,0}},{{8,5},{5,2}}}
	/*
	 So payoff[i][j][k] represents the payoff to player 1 when the first
	 player's action is i, the second player's action is j, and the
	 third player's action is k.

		1	2	3	P
		1	0	0	8  1 2 0  8 3 3 14
		0	0	0	6  0 2 0  6 6 6 24
		1	0	1	5  1 1 1  5 0 5 10
		1	1	0	5  1 1 1  5 5 0 10
		0	0	1	3  0 1 1  3 3 8 14
		0	1	0	3  0 1 1  3 8 3 14
		1	1	1	2  1 0 2  2 2 2 6
		0	1	1	0  0 0 2  0 5 5 10

	 In this simulation, triples of players will play each other repeatedly in a
	 'match'. A match consists of about 100 rounds, and your score from that match
	 is the average of the payoffs from each round of that match. For each round, your
	 strategy is given a list of the previous plays (so you can remember what your
	 opponent did) and must compute the next action.  */


	abstract class Player {
		// This procedure takes in the number of rounds elapsed so far (n), and
		// the previous plays in the match, and returns the appropriate action.
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			throw new RuntimeException("You need to override the selectAction method.");
		}
		// Used to extract the name of this player class.
		final String name() {
			String result = getClass().getName();
			return result.substring(result.indexOf('$')+1);
		}
	}
	class Chen_Zhiwei_Player extends Player {

		int[][][] payoff = {
			{{6,3},  //payoffs when first and second players cooperate
			 {3,0}}, //payoffs when first player coops, second defects
			{{8,5},  //payoffs when first player defects, second coops
			 {5,2}}};//payoffs when first and second players defect

		int myScore = 0;
		int opp1Score = 0;
		int opp2Score = 0;

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			// First Law: Always cooperate in first 2 rounds
			if (n < 2) return 0;

			// Second Law: Tolerate 2 consecutive defects from both opp
			// If 2 consecutive defects from both opp, then defect
			if (oppHistory1[n-1] == 1 && oppHistory1[n-2] == 1 &&
				oppHistory2[n-1] == 1 && oppHistory2[n-2] == 1)
				return 1;

			// Third Law: if one of the opponents is Nasty, then always defect
			boolean isOpp1Nasty, isOpp2Nasty;
			isOpp1Nasty = isNasty(n, oppHistory1);
			isOpp2Nasty = isNasty(n, oppHistory2);
			if (isOpp1Nasty || isOpp2Nasty) return 1;

			// Fourth Law: if one of the opponents is Random, then always defect
			boolean isOpp1Random, isOpp2Random;
			isOpp1Random = isRandom(n, oppHistory1);
			isOpp2Random = isRandom(n, oppHistory2);
			if (isOpp1Random || isOpp2Random) return 1;

			// Fifth Law: if my current score is lower than one of the opp, then always defect
			myScore += payoff[myHistory[n-1]][oppHistory1[n-1]][oppHistory2[n-1]];
			opp1Score += payoff[oppHistory1[n-1]][oppHistory2[n-1]][myHistory[n-1]];
			opp2Score += payoff[oppHistory2[n-1]][oppHistory1[n-1]][myHistory[n-1]];
			if (myScore < opp1Score || myScore < opp2Score) return 1;

			// Sixth Law: If above laws don't apply, then be a T4TPlayer
			if (Math.random() < 0.5) return oppHistory1[n-1];
			else return oppHistory2[n-1];
		}

		boolean isNasty(int n, int[] oppHistory) {
			int cnt = 0;
			for (int i=0; i<n; i++){
				if (oppHistory[i] == 1)
					cnt++;
			}

			if (cnt == n) return true;
			else return false;
		}

		boolean isRandom(int n, int[] oppHistory) {
			int sum = 0;
			double eps = 0.025;
			for (int i=0; i<n; i++) {
				sum += oppHistory[i];
			}

			// if ratio is roughly 0.5, then the opponent is highly likely to be random
			double ratio = (double) sum / n;
			if (Math.abs(ratio - 0.5) < eps) return true;
			else return false;
		}
	}
	class Ngo_Jason_Player extends Player{ // extends Player

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n == 0)
				return 0; // cooperate by default

			if (n >= 109)
				return 1; // opponents cannot retaliate

			// https://www.sciencedirect.com/science/article/abs/pii/S0096300316301011
			if (oppHistory1[n-1] == oppHistory2[n-1])
				return oppHistory1[n-1];

			// n starts at 0, so compare history first

			if (n % 2 != 0) { // odd round - be tolerant
				// TolerantPlayer
				int opponentCoop = 0;
				int opponentDefect = 0;

				for (int i = 0; i < n; i++) {
					if (oppHistory1[i] == 0)
						opponentCoop += 1;
					else
						opponentDefect += 1;

					if (oppHistory2[i] == 0)
						opponentCoop += 1;
					else
						opponentDefect += 1;
				}

				return (opponentDefect > opponentCoop) ? 1 : 0;
			}
			// else: even round - compare history

			// HistoryPlayer
			int myNumDefections = 0;
			int oppNumDefections1 = 0;
			int oppNumDefections2 = 0;

			for (int index = 0; index < n; ++index) {
				myNumDefections += myHistory[index];
				oppNumDefections1 += oppHistory1[index];
				oppNumDefections2 += oppHistory2[index];
			}

			if (myNumDefections >= oppNumDefections1 && myNumDefections >= oppNumDefections2)
				return 0;
			else
				return 1;
		}
	}
	class Huang_KyleJunyuan_Player extends Player {
		// Helper function to calculate percentage of cooperation
		float calCoopPercentage(int[] history) {
			int cooperates = 0;
			int length = history.length;
	
			for (int i = 0; i < length; i++)
				if (history[i] == 0)
					cooperates++;
	
			return (float) cooperates / length * 100;
		}
	
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n == 0)
				return 0; // First round: Cooperate
	
			/* 1. Calculate percentage of cooperation */
			float perOpp1Coop = calCoopPercentage(oppHistory1);
			float perOpp2Coop = calCoopPercentage(oppHistory2);
	
			/* 2. If both players are mostly cooperating */
			if (perOpp1Coop > 90 && perOpp2Coop > 90) {
				int range = (10 - 5) + 1; // Max: 10, Min: 5
				int random = (int) (Math.random() * range) + 5;
				
				if (n > (90 + random))  // Selfish: Last min defect
					return 1;
				else
					return 0;	// First ~90 rounds: Cooperate
			}
	
			/* 3. Defect by default */
			return 1;
		}
	}
	class Naing_Htet_Player extends Player {

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

            // Rule 1: our agent will cooperate in the first round
            if (n == 0)  {
                return 0;
            }

            // Rule 2: our agent will defect in the last few rounds, NastyPlayer mode is turned on
            if (n > 95) {
                return 1;
            }

            // Rule 3: if all players including our agent cooperated in the previous round,
            // then our agent will continue to cooperate
            if (myHistory[n-1] == 0 && oppHistory1[n-1] == 0 && oppHistory2[n-1] == 0) {
                return 0;
            }

            // Rule 4: check opponents history to see if they have defected before
            for (int i = 0; i < n; i++) {
                if (oppHistory1[i] == 1 || oppHistory2[i] == 1) {
                    // if either one of them defected before, our agent will always defect
                    return 1;
                }
            }
            // Rule 5: Otherwise, by default nature, our agent will always cooperate
            return 0;
        }
    }
	class tsKennethTeo_Player extends Player {

	// A Tolerant Tit for Tat player that considers action of both
		// opponents instead of just looking at one.
	// If opponents are not acting in unison, fall back to being an
	// alternator
	int selectAction(int n, int[] myHistory, int[] oppHistory1,
			int[] oppHistory2) {

			// Cooperate on the first two rounds
			if (n==0 || n==1) {
				return 0;
			}
			// Defect on the last two rounds
			else if (n==98||n==99) {
				 return 1;
			}

			// If both opponents are nasty, turn nasty as well. Only give
			// two chances before defecting
			if ((oppHistory1[n-1]==1&&oppHistory1[n-2]==1)
				&&(oppHistory2[n-1]==1&&oppHistory2[n-2]==1)) {
				return 1;
			}
			// If both opponents in synchronisation, possibly Tit for tat,
			//   return Tit for tat
			else if (oppHistory1[n-1] == oppHistory2[n-1]
				&& oppHistory1[n-2] == oppHistory2[n-2]) {
				return oppHistory1[n-1];
			}
			// Opponents not acting in unision, alternate between 0 and 1
			else {
				if(myHistory[n-1] == 1) {
					return 0;
				} else {
					return 1;
				}
			}
		}
	}
	class randomTilt extends Player{
		boolean defected = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//return 1 if defected
			if(defected)
				return 1;

			//if first turn, co operate
			if(n < 1)
				return 0;


			if(Math.random()>0.8)
			{
				defected = true;
				return 1;
			}

			//if any opponent defects, we permanently defect
			if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
			{
				defected = true;
				return 1;
			}
			//we defect on the last possible turn
			if(n >= 119)
				return 1;

			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class testPlayer extends Player{
		boolean defected = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//return 1 if defected
			if(defected)
				return 1;

			//if first turn, co operate
			if(n < 1)
				return 0;

			//if any opponent defects, we permanently defect
			if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
			{
				defected = true;
				return 1;
			}
			//we defect on the last possible turn
			if(n >= 119)
				return 1;

			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class testPlayer3 extends Player{
		boolean defected = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//return 1 if defected
			if(defected)
				return 1;

			//if first turn, co operate
			if(n > 0){
				//if any opponent defects, we permanently defect
				if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
				{
					defected = true;
					return 1;
				}
				//we defect on the last possible turn
				if(n >= 109)
					return 1;
			}
			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class YangLinRamal_Isaac_Player108 extends Player{
		boolean defected = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//return 1 if defected
			if(defected)
				return 1;

			//if first turn, co operate
			if(n > 0){
				//if any opponent defects, we permanently defect
				if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
				{
					defected = true;
					return 1;
				}
				//we defect on the last possible turn
				if(n >= 108)
					return 1;
			}
			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class YangLinRamal_Isaac_Player105 extends Player{
		boolean defected = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//return 1 if defected
			if(defected)
				return 1;

			//if first turn, co operate
			if(n > 0){
				//if any opponent defects, we permanently defect
				if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
				{
					defected = true;
					return 1;
				}
				//we defect on the last possible turn
				if(n >= 105)
					return 1;
			}
			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class TiltNRecover extends Player{
		boolean defected = false;
		int recovery = 0;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			//return 1 if defected
			if(defected)
			{
				recovery++;
				if(recovery < 2)
					return 1;
				defected = false;
			}

			//if first turn, co operate
			if(n > 0){
				//if any opponent defects, we permanently defect
				if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
				{
					defected = true;
					recovery = 0;
					return 1;
				}
			}
			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class TiltNRecover2 extends Player{
		boolean defected = false;
		int recovery = 0;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(defected)
			{
				//recover after a set amount of turns
				recovery++;

				//return 1 if defected and recovery not reached
				if(recovery < 8)
					return 1;
				
				//recovery reached, stop defecting for now
				defected = false;
			}

			//check if any opponents defected in previous round
			if(n > 0){
				//if any opponent defects, we defect until recovery point reached
				if(oppHistory1[n-1] > 0 || oppHistory2[n-1] > 0)
				{
					defected = true;
					recovery = 0;
					return 1;
				}
			}
			//coop by default
			return 0;
		}
	}
	class Thanos extends Player{
		int balancer = 0;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {


			if(n > 0){
				if(oppHistory1[n-1]>0)
					balancer--;
				else
					balancer++;
				
				if(oppHistory2[n-1]>0)
					balancer--;
				else
					balancer++;

				if(balancer < 0)
					return 1;
			}
			//if opps consistently co op, we also co op
			return 0;
		}
	}
	class PatternPlayer1 extends Player{
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(n==0)
				return 0;

			if(n%4==0)
				return 1;
			
			return 0;
		}
	}
	class PatternPlayer2 extends Player{
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(n==0)
				return 0;

			if(n%6==0)
				return 1;
			
			return 0;
		}
	}
	class PatternPlayer3 extends Player{
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(n==0)
				return 0;

			if(n%9==0)
				return 1;
			
			return 0;
		}
	}
	class PatternPlayer4 extends Player{
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(n==0)
				return 0;

			if(n%12==0)
				return 1;
			
			return 0;
		}
	}
	class YangLinRamal_Isaac_Player10 extends Player{
		/*
		Order of best scenarios
		Self  OppCoop	OppDefect	Points
		D		2		0			8
		C		2		0			6
		D		1		1			5
		C		1		1			3
		D		0		2			2
		C		0		2			0
		*/
		int[][][] opp1Style = {{{0,0},{0,0}},{{0,0},{0,0}}};
		int[][][] opp2Style = {{{0,0},{0,0}},{{0,0},{0,0}}};

		int opp1Points = 0;
		int opp2Points = 0;
		int mypoints = 0;

		int opp1DefCount = 0;
		int opp2DefCount = 0;
		boolean defected = false;

		double defMod1 = 0;
		double defMod2 = 0;

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			int returnValue = 0;
			if(n > 95)
				return 1;


			//track how opponents react to previous rounds
			if(n > 1)
			{
				defMod1 = opp1DefCount/(n-1);
				defMod2 = opp2DefCount/(n-1);
				opp1Style[myHistory[n-2]][oppHistory2[n-2]][oppHistory1[n-1]]++;
				opp2Style[myHistory[n-2]][oppHistory1[n-2]][oppHistory2[n-1]]++;
			}

			//collect data for n rounds before starting
			if(n > 0)
			{

				int opp1Prev = oppHistory1[n-1];
				int opp2Prev = oppHistory2[n-1];
				int myPrev = myHistory[n-1];
				opp1DefCount+=opp1Prev;
				opp2DefCount+=opp2Prev;

				if(opp1Prev > 0 || opp2Prev > 0)
					defected = true;

				if(defected)
					return 1;
				if(n < 1)
				{
					defMod1 = opp1DefCount;
					defMod2 = opp2DefCount;
				}

				opp1Points += calculateScore(opp1Prev, opp2Prev, myPrev);
				opp2Points += calculateScore(opp2Prev, opp1Prev, myPrev);
				mypoints += calculateScore(myPrev, opp1Prev, opp2Prev);

				// //if opps consistently defect, we also defect
				// if(opp1Prev == 1 && opp2Prev == 1 &&(defMod1 >= 0.5 || defMod2 >= 0.5))
				// 	return 1;

				//if opps consistently defect, we also defect
				if(opp1Prev == 1 && opp2Prev == 1)
					return 1;

				//if opps consistently co op, we also co op
				if(opp1Prev == 0 && opp2Prev == 0)
					return 0;

				//if im losing
				if(opp1Points > mypoints && opp2Points > mypoints)
					return 1;

				//calculate probability using previous round

				// //find probability of moves for both opp
				// double opp1CoopProb = 0,opp2CoopProb = 0;
				// try{
				// 	opp1CoopProb= opp1Style[myPrev][opp2Prev][0]/(opp1Style[myPrev][opp2Prev][0] + opp1Style[myPrev][opp2Prev][1]);
				// }
				// catch(Exception e)
				// {
				// 	opp1CoopProb = 1;
				// }
				// try{
				// 	opp2CoopProb = opp2Style[myPrev][opp1Prev][0]/(opp2Style[myPrev][opp1Prev][0] + opp2Style[myPrev][opp1Prev][1]);
				// }
				// catch(Exception e)
				// {
				// 	opp2CoopProb = 1;
				// }

				// double opp1DefectProb = 1 - opp1CoopProb;
				// double opp2DefectProb = 1 - opp2CoopProb;


				// //get expected value of my move
				// double evBetray = (2*(opp1DefectProb*opp2DefectProb) + 5*(opp1DefectProb*opp2CoopProb)+ 5*(opp2DefectProb*opp1CoopProb) + 8*(opp1CoopProb*opp2CoopProb));
				// double evCoop = 3*(opp1DefectProb*opp2CoopProb)+ 3*(opp2DefectProb*opp1CoopProb) + 6*(opp1CoopProb*opp2CoopProb);

				// // System.out.println("defMod 1 = " + defMod1);
				// // System.out.println("defMod 2 = " + defMod1);
				// // System.out.println("evBetray = " + evBetray);
				// // System.out.println("evCoop = " + evCoop);
				// if(defMod1 >= 0.5 || defMod2 >= 0.5)
				// {
				// 	returnValue = 1;
				// 	//System.out.println("Defect as over threshold");
				// }
				// else if(evBetray>evCoop)
				// {
				// 	returnValue = 1;
				// 	//System.out.println("Defect as best option");
				// }
				// else
				// {
				// 	returnValue = 0;
				// 	//System.out.println("Coop as best option");
				// }


			}

			return returnValue;

		}

		private int calculateScore(int choice1, int choice2, int choice3)
		{
			switch(choice1){
				case 0:
					if(choice2 == 0 && choice2 == choice3)
					{
						return 6;
					}
					else if(choice2 == 1 && choice2 == choice3){
						return 0;
					}
					else
					{
						return 3;
					}
				case 1:
					if(choice2 == 0 && choice2 == choice3)
						{
							return 8;
						}
						else if(choice2 == 1 && choice2 == choice3){
							return 2;
						}
						else
						{
							return 5;
						}
				default:
					return -1;
			}
		}
	}
	class testPlayer4 extends Player {
		int k = 1;
		//NicePlayer always cooperates
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(n == 0)
				return 0;
			if(oppHistory1[n-1] >0 || oppHistory2[n-1]>0)
			{
				k = 1;
				return 1;
			}

			if(n%k==0)
			{
				k++;
				return 1;
			}
			return 0;
		}
	}
	class RetardPlayer extends Player {
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

			if(n == 0)
				return 1;

			if(oppHistory1[n-1] >0 && oppHistory2[n-1]>0)
				return 0;
			
			return 1;
		}
	}
	class YangLinRamal_Isaac_Player extends Player {
		boolean opp1Def = false,opp2Def = false;  //Flags
    	int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if(n==0)    //Co-operate on first turn
				return 0;
			opp1Def = (oppHistory1[n-1]>0 || opp1Def); //Check if opponents defect on previous turn
			opp2Def = (oppHistory2[n-1]>0 || opp2Def);
			return (opp1Def&&opp2Def)||(n>=109) ? 1 : 0; 
			//If both have defected, we permanently defect;
			//We defect on the last turn to protect ourselves from last minute defectors
			//Otherwise co-operate by default
		}
	}
	class testPlayer3_2 extends Player {
		//NicePlayer always cooperates
		boolean opp1Def = false;
		boolean opp2Def = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if(n==0)
				return 0;
			if(oppHistory1[n-1]>0)	
				opp1Def = true;
			if(oppHistory2[n-1]>0)
				opp2Def = true;
			if(opp1Def && opp2Def)
				return 1;
			if(n >= 108)
				return 1;
			return 0;
		}
	}
	class testPlayer5 extends Player {
		//NicePlayer always cooperates
		boolean opp1Def = false;
		boolean opp2Def = false;
		boolean defected = false;
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if(n==0)
				return 0;
			if((opp1Def && opp2Def)||defected)
				return 1;
			if(oppHistory1[n-1]>0)	
				opp1Def = true;
			if(oppHistory2[n-1]>0)
				opp2Def = true;
			if(n>1)
			{
				if((oppHistory1[n-1]+oppHistory1[n-2] > 1) ||(oppHistory2[n-1]+oppHistory2[n-2] > 1))
					defected = true;
			}
			if((opp1Def && opp2Def)||defected)
				return 1;
			return 0;
		}
	}
	/* Here are four simple strategies: */

	class NicePlayer extends Player {

		//NicePlayer always cooperates
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return 0;
		}
	}
	class NastyPlayer extends Player {
		//NastyPlayer always defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return 1;
		}
	}
	class RandomPlayer extends Player {

		//RandomPlayer randomly picks his action each time
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (Math.random() < 0.5)
				return 0;  //cooperates half the time
			else
				return 1;  //defects half the time
		}
	}
	class TolerantPlayer extends Player {

		//TolerantPlayer looks at his opponents' histories, and only defects
		//if at least half of the other players' actions have been defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			int opponentDefect = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			if (opponentDefect > opponentCoop)
				return 1;
			else
				return 0;
		}
	}
	class FreakyPlayer extends Player {
		//FreakyPlayer determines, at the start of the match,
		//either to always be nice or always be nasty.
		//Note that this class has a non-trivial constructor.
		int action;
		FreakyPlayer() {
			if (Math.random() < 0.5)
				action = 0;  //cooperates half the time
			else
				action = 1;  //defects half the time
		}

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return action;
		}
	}
	class T4TPlayer extends Player {
		//Picks a random opponent at each play,
		//and uses the 'tit-for-tat' strategy against them
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			if (Math.random() < 0.5)
				return oppHistory1[n-1];
			else
				return oppHistory2[n-1];
		}
	}
	
	class ViswenPlayer extends Player{

		//NicePlayer always cooperates
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if(n==0)
				return 0;
			if(n>=90)
				return 1;
			if(oppHistory1[n-1] + oppHistory2[n-1] > 1)
				return 1;
			return 0;
		}
	}


	class SusT4TPlayer extends Player {

		//Picks a random opponent at each play,
		//and uses the 'tit-for-tat' strategy against them
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 1; //cooperate by default
			if (Math.random() < 0.5)
				return oppHistory1[n-1];
			else
				return oppHistory2[n-1];
		}
	}
	class SoftMajority extends Player {

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int selfDefect = 0;
			int opponentCoop = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				if (myHistory[i] > 0)
					selfDefect = selfDefect + 1;
			}
			if (opponentCoop >= selfDefect)
				return 1;
			else
				return 0;
		}
	}
	class SoftMajorityAvg extends Player {
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int selfDefect = 0;
			int opponentCoop = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				if (myHistory[i] > 0)
					selfDefect = selfDefect + 1;
			}
			if (opponentCoop/2 >= selfDefect)
				return 1;
			else
				return 0;
		}
	}
	class CopyKittenPlayer extends Player{

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			int totalDefect = 0;
			if(n > 1)
				totalDefect = oppHistory1[n-1] + oppHistory2 [n-1] + oppHistory1[n-2] + oppHistory2[n-2];
			else
				totalDefect = oppHistory1[n-1] + oppHistory2 [n-1];
			if (totalDefect >= 2)
				return 1;
			else
				return 0;
		}
	}
	class CopyCatPlayer extends Player{

		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			return Math.max(oppHistory1[n-1],oppHistory2[n-1]);
		}
	}
	

	/* In our tournament, each pair of strategies will play one match against each other.
	 This procedure simulates a single match and returns the scores. */
	float[] scoresOfMatch(Player A, Player B, Player C, int rounds) {
		int[] HistoryA = new int[0], HistoryB = new int[0], HistoryC = new int[0];
		float ScoreA = 0, ScoreB = 0, ScoreC = 0;

		for (int i=0; i<rounds; i++) {
			int PlayA = A.selectAction(i, HistoryA, HistoryB, HistoryC);
			int PlayB = B.selectAction(i, HistoryB, HistoryC, HistoryA);
			int PlayC = C.selectAction(i, HistoryC, HistoryA, HistoryB);
			ScoreA = ScoreA + payoff[PlayA][PlayB][PlayC];
			ScoreB = ScoreB + payoff[PlayB][PlayC][PlayA];
			ScoreC = ScoreC + payoff[PlayC][PlayA][PlayB];
			HistoryA = extendIntArray(HistoryA, PlayA);
			HistoryB = extendIntArray(HistoryB, PlayB);
			HistoryC = extendIntArray(HistoryC, PlayC);
		}
		float[] result = {ScoreA/rounds, ScoreB/rounds, ScoreC/rounds};
		return result;
	}

//	This is a helper function needed by scoresOfMatch.
	int[] extendIntArray(int[] arr, int next) {
		int[] result = new int[arr.length+1];
		for (int i=0; i<arr.length; i++) {
			result[i] = arr[i];
		}
		result[result.length-1] = next;
		return result;
	}

	/* The procedure makePlayer is used to reset each of the Players
	 (strategies) in between matches. When you add your own strategy,
	 you will need to add a new entry to makePlayer, and change numPlayers.*/

	 int counter = 0;
	int numPlayers = 19;

	//Make sure your player is under Case 0 for win/loss results to display accurately
	Player makePlayer(int which) {
		switch (which){

		//FOR TESTING AGAINST ALL CREATED PLAYERS
		//case 0: return new YangLinRamal_Isaac_Player(which);
		//case 1: return new Ngo_Jason_Player(which);
		//case 2: return new Naing_Htet_Player(which);
		//case 3: return new Chen_Zhiwei_Player(which);
		//case 4: return new tsKennethTeo_Player(which);
		////defaults
		//case 5: return new T4TPlayer(which);
		//case 6: return new NicePlayer(which);
		//case 7: return new NastyPlayer(which);
		//case 8: return new RandomPlayer(which);
		//case 9: return new TolerantPlayer(which);
		//case 10: return new FreakyPlayer(which);
		////defaults end
		//case 11: return new Huang_KyleJunyuan_Player(which);
		//case 12: return new randomTilt(which);
		//case 13: return new CopyKittenPlayer(which);
		//case 14: return new CopyCatPlayer(which);
		//case 15: return new testPlayer3(which);
		//case 16: return new PatternPlayer1(which);
		//case 17: return new PatternPlayer2(which);
		//case 18: return new PatternPlayer3(which);
		//case 19: return new PatternPlayer4(which);
		//case 20: return new testPlayer4(which);
		//case 21: return new RetardPlayer(which);
		//case 22: return new TiltNRecover(which);
		//case 23: return new TiltNRecover2(which);
		//case 24: return new Thanos(which);
		//case 25: return new SoftMajority(which);
		//case 26: return new SoftMajorityAvg(which);
		//case 27: return new SusT4TPlayer(which);


		//FOR TESTING AGAINST HIGH SKILL CAP PLAYERS
		case 0: return new YangLinRamal_Isaac_Player();
		case 1: return new Naing_Htet_Player();
		case 2: return new Huang_KyleJunyuan_Player();
		case 3: return new CopyKittenPlayer();
		case 4: return new testPlayer3();
		case 5: return new TiltNRecover();
		case 6: return new TiltNRecover2();
		case 7: return new testPlayer3_2();
		case 8: return new testPlayer5();
		case 9: return new tsKennethTeo_Player();
		case 10: return new Ngo_Jason_Player();
		case 11: return new Chen_Zhiwei_Player();
		case 12: return new ViswenPlayer();
		//defaults
		case 13: return new T4TPlayer();
		case 14: return new NicePlayer();
		case 15: return new NastyPlayer();
		case 16: return new RandomPlayer();
		case 17: return new TolerantPlayer();
		case 18: return new FreakyPlayer();
		//defaults end


		//FOR TESTING AGAINST DEFAULT PLAYERS
		// case 0: return new YangLinRamal_Isaac_Player();
		// //defaults
		// case 1: return new NicePlayer();
		// case 2: return new NastyPlayer();
		// case 3: return new FreakyPlayer();
		// case 4: return new RandomPlayer();
		// case 5: return new TolerantPlayer();
		// case 6: return new T4TPlayer();
		// //defaults end
		}
		throw new RuntimeException("Bad argument passed to makePlayer");
	}

	/* Finally, the remaining code actually runs the tournament. */

	public static void main (String[] args) {
		ThreePrisonersDilemma instance = new ThreePrisonersDilemma();
		instance.runTournament();
	}

	boolean verbose = true; // set verbose = false if you get too much text output

	void runTournament() {
		float[] totalScore = new float[numPlayers];
		int[][] lossRecored = new int[numPlayers][3];
		// This loop plays each triple of players against each other.
		// Note that we include duplicates: two copies of your strategy will play once
		// against each other strategy, and three copies of your strategy will play once.
		int repeat = 100;
		for(int x = 0; x < repeat; x++)
		{
			for (int i=0; i<numPlayers; i++) for (int j=i; j<numPlayers; j++) for (int k=j; k<numPlayers; k++) {
				counter++;
				Player A = makePlayer(i); // Create a fresh copy of each player
				Player B = makePlayer(j);
				Player C = makePlayer(k);
				int rounds = 90 + (int)Math.rint(20 * Math.random()); // Between 90 and 110 rounds
				float[] matchResults = scoresOfMatch(A, B, C, rounds); // Run match	
				totalScore[i] = totalScore[i] + matchResults[0];
				totalScore[j] = totalScore[j] + matchResults[1];
				totalScore[k] = totalScore[k] + matchResults[2];
				if(i == 0)
				{
					if(matchResults[0] < matchResults[1])
						lossRecored[j][0]++;
					if(matchResults[0] < matchResults[2])
						lossRecored[k][0]++;
					if(matchResults[0] == matchResults[1])
						lossRecored[j][2]++;
					if(matchResults[0] == matchResults[2])
						lossRecored[k][2]++;
					lossRecored[j][1]++;
					lossRecored[k][1]++;
				}
				else if(j == 0)
				{
					if(matchResults[1] < matchResults[0])
						lossRecored[i][0]++;
					if(matchResults[1] < matchResults[2])
						lossRecored[k][0]++;
					if(matchResults[1] == matchResults[0])
						lossRecored[i][2]++;
					if(matchResults[1] == matchResults[2])
						lossRecored[k][2]++;
					lossRecored[i][1]++;
					lossRecored[k][1]++;
				}
				else if(k == 0)
				{
					if(matchResults[2] < matchResults[1])
						lossRecored[j][0]++;
					if(matchResults[2] < matchResults[0])
						lossRecored[i][0]++;
					if(matchResults[2] == matchResults[1])
						lossRecored[j][2]++;
					if(matchResults[2] == matchResults[0])
						lossRecored[i][2]++;
					lossRecored[j][1]++;
					lossRecored[i][1]++;
				}
				// if (verbose)
				// 	System.out.println(A.name() + " scored " + matchResults[0] +
				// 			" points, " + B.name() + " scored " + matchResults[1] +
				// 			" points, and " + C.name() + " scored " + matchResults[2] + " points.");
				
				if(counter%10000==0)
					System.out.println(counter);
			}
		}

		int[] sortedOrder = new int[numPlayers];
		// This loop sorts the players by their score.
		for (int i=0; i<numPlayers; i++) {
			int j=i-1;
			for (; j>=0; j--) {
				if (totalScore[i] > totalScore[sortedOrder[j]])
					sortedOrder[j+1] = sortedOrder[j];
				else break;
			}
			sortedOrder[j+1] = i;
		}

		// Finally, print out the sorted results.
		if (verbose) System.out.println();
		System.out.println("Tournament Results");
		for (int i=0; i<numPlayers; i++)
			System.out.println((i+1)+". "+makePlayer(sortedOrder[i]).name() + ": "
				+ totalScore[sortedOrder[i]]/repeat + " points.");
		System.out.println("\n");
		for(int i = 0; i< numPlayers; i++)
			System.out.println("Player " + makePlayer(i).name()+" || Wins: "+ (lossRecored[i][1] - (lossRecored[i][2]+lossRecored[i][0])) + " | Losses: " + lossRecored[i][0] + " | Ties: " +  lossRecored[i][2]+ " | Total Matches: " +  lossRecored[i][1]+"||");
	} // end of runTournament()
} // end of class PrisonersDilemma

