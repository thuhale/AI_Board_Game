/***************************************************************************************
  PlayerImpl.java
  Implement five functions in this file.
  ---------
  Licensing Information:  You are free to use or extend these projects for
  educational purposes provided that (1) you do not distribute or publish
  solutions, (2) you retain this notice, and (3) you provide clear
  attribution to UW-Madison.
 
  Attribution Information: The Take Stone Games was developed at UW-Madison.
  The initial project was developed by Jerry(jerryzhu@cs.wisc.edu) and his TAs.
  Current version with depthLimit and SBE was developed by Fengan Li(fengan@cs.wisc.edu)
  and Chuck Dyer(dyer@cs.wisc.edu)
  
*****************************************************************************************/

import java.util.*;

public class PlayerImpl implements Player {
	// Identifies the player
	private int name = 0;
	int n = 0;
	

	// Constructor
	public PlayerImpl(int name, int n) {
		this.name = 0;
		this.n = n;
	}

	// Function to find possible successors
	@Override
	public ArrayList<Integer> generateSuccessors(int lastMove, int[] takenList) 
	{
		ArrayList<Integer> successor = new ArrayList<Integer>();
		if (lastMove == -1){
			for(int i=1; i<takenList.length/2;i++){
				if(takenList[i] == 0 && (i % 2 == 1)){
					successor.add(i);
				}
			}
		}
		
		else if (lastMove != -1){
			for(int i=1; i<takenList.length;i++){
				if(takenList[i] == 0 && ((i % lastMove==0) || (lastMove % i==0))){
					successor.add(i);
				}
			}
		}
		return successor;
	}

	// The max value function
	@Override
	public double max_value(GameState s, int depthLimit) 
	{	
		if (this.isTerminalState(s)==true){
			s.leaf = true;
			s.bestMove = -1;
			return -1;
		}

		if (depthLimit == 0){
			s.leaf = false;
			s.bestMove = -1;
			return this.stateEvaluator(s);
		}
		else {
			int nextDepthLimit;
			if (depthLimit == -1) {
				//Depth limit is -1. Try to call recursively until terminal case
				nextDepthLimit = -1;
			}
			else {
				//depth limit is a positive number. Try to calculate depth-1
				nextDepthLimit = depthLimit - 1;
			}

			double alpha = -1;
			ArrayList<Integer> suc = this.generateSuccessors(s.lastMove, s.takenList);
			int b;
			int[] newTaken;
			for (int i = 0; i < suc.size(); i++){
				newTaken = s.takenList.clone();
				b = suc.get(i);
				newTaken[b]=1;
				GameState h = new GameState(newTaken, b);
				double value = min_value(h, nextDepthLimit);
				int compare = Double.compare(alpha, value);
				
				if (compare <= 0) {
					alpha = value;
					s.leaf = false;
					s.bestMove = b;
				}
			}
			return alpha;
		}
	}

	
	// The min value function
	@Override
	public double min_value(GameState s, int depthLimit)
	{
		if (this.isTerminalState(s)==true){
			s.leaf = true;
			s.bestMove = -1;
			return 1;
		}
		
		if (depthLimit == 0){
			s.leaf = false;
			s.bestMove = -1;
			return 0 - this.stateEvaluator(s);
		}
		else {
			int nextDepthLimit;
			if (depthLimit == -1) {
				//Depth limit is -1. Try to call recursively until terminal case
				nextDepthLimit = -1;
			}
			else {
				//depth limit is a positive number. Try to calculate depth-1
				nextDepthLimit = depthLimit - 1;
			}

			double beta = 1;
			ArrayList<Integer> suc = this.generateSuccessors(s.lastMove, s.takenList);
			int b;
			int[] newTaken;
			for (int i = 0; i < suc.size(); i++){
				newTaken = s.takenList.clone();
				b = suc.get(i);
				newTaken[b]=1;
				GameState h = new GameState(newTaken, b);
				double value = max_value(h, nextDepthLimit);
				int compare = Double.compare(beta, value);
				
				if (compare >= 0) {
					beta = value;
					s.leaf = false;
					s.bestMove = b;
				}
			}
			return beta;
		}
	}
	
	// Function to find the next best move
	@Override
	public int move(int lastMove, int[] takenList, int depthLimit) {
		GameState start = new GameState(takenList, lastMove);
		double result = max_value(start, depthLimit);
		return start.bestMove;
	}
	
	// The static board evaluator function
	@Override
	public double stateEvaluator(GameState s)
	{
		int a = s.lastMove;
		int[] taken = s.takenList;
		ArrayList<Integer> successorList = this.generateSuccessors(a, taken);
		int successorSize = successorList.size();

		if (taken[1]==0){
			return 0;
		}
		
		if(a == 1){
			if(successorSize % 2 == 1){
				return 0.5;
			}
			else {
				return -0.5;
			}
		}
		
		if(this.isPrime(a)){
			int primeCount = 0;
			for (int i = 0; i < successorSize; i++){
				if (successorList.get(i) % a == 0){
					primeCount++;
				}
			}
			
			if (primeCount % 2 == 1){
				return 0.7;
			}
			else{
				return -0.7;
			}
		}
		else{
			int primeC = 0;
			int primeFactor = this.getLargestPrimeDivider(a);
			for (int i = 0; i< successorSize; i++){
				if(successorList.get(i) % primeFactor == 0){
					primeC++;
				}
			}
			if (primeC % 2 == 1){
				return 0.6;
			}
			else{
				return -0.6;
			}
			
		}
	}
	//check if a number is a prime
	public boolean isPrime(int k) {
		   if (k <= 1) {
		       return false;
		   }
		   for (int i = 2; i < Math.sqrt(k); i++) {
		       if (k % i == 0) {
		           return false;
		       }
		   }
		   return true;
		}

	//find the largest prime divider
	public int getLargestPrimeDivider(int d) {
		  if (d==1){
			  System.out.println("This method does not apply to 1");
			  return 0;
		  }
		  else if(this.isPrime(d)){
			  return d;
		  }
		  else{
			  for (int j = d-1; j>1; j--){
				  if (d % j == 0 && isPrime(j)){
					  return j;
				  }
			  }
			  return d;
		  }
		}
	
	//this method returns true if s is a terminal state
	public boolean isTerminalState(GameState s){
		if (this.generateSuccessors(s.lastMove,s.takenList).size()==0){
			return true;
		}
		return false;
	}
	
	
}
