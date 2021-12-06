package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.threading.ThreadManager;
import com.eudycontreras.othello.threading.TimeSpan;

import java.util.List;

/**
 * <H2>Created by</h2> Eudy Contreras
 * <h4> Mozilla Public License 2.0 </h4>
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <a href="https://www.mozilla.org/en-US/MPL/2.0/">visit Mozilla Public Lincense Version 2.0</a>
 * <H2>Class description</H2>
 * 
 * @author Eudy Contreras
 */
public class MastermindAI extends Agent {

	public int prunedCounter = 0;


	MastermindAI(String agentName) {
		super(agentName);
		this.setAgentName("MASTERMIND");
		// TODO Auto-generated constructor stub
	}

	MastermindAI(PlayerTurn playerTurn, String agentName) {
		super(agentName, playerTurn);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Delete the content of this method and Implement your logic here!
	 *
	 * @return
	 */

	public AgentMove getMove(GameBoardState gameState) {
		int waitTime = UserSettings.MIN_SEARCH_TIME; // 1.5 seconds
		ThreadManager.pause(TimeSpan.millis(waitTime)); // Pauses execution for the wait time to cause delay

		return getMiniMaxMove(gameState);
	}



	public AgentMove getMiniMaxMove (GameBoardState gameState) {

		resetStatistics();


		if ((!AgentController.isTerminal(gameState,PlayerTurn.PLAYER_ONE))){

		   	long startTime = System.currentTimeMillis();
			int val = getMiniMax(gameState, 6,Integer.MIN_VALUE, Integer.MAX_VALUE,true );
			long endTime = System.currentTimeMillis();
			long timeElapsed = (endTime- startTime);

			System.out.println("Time elapsed = " + timeElapsed + "ms");

			ObjectiveWrapper optimalMove = null;

			List<GameBoardState> states = gameState.getChildStates();

			for (GameBoardState state : states) {

				if (state.getStateHeuristic() == val) {
					System.out.println("MASTERMIND  : VALUE " + val + " MATCHED WITH STATE HEURISTIC "  + state.getStateHeuristic());
					optimalMove = state.getLeadingMove();
					break;
				}
			}

			if(optimalMove == null) {
				System.out.println("UNABLE TO LOCATE MOVE");
				optimalMove= states.get(0).getLeadingMove();
			}

			System.out.println("Nodes pruned: " + prunedCounter);
			return new MoveWrapper(optimalMove);
		}
		else
			return new MoveWrapper(null);

	}





	public  int getMiniMax(GameBoardState gameState, int depth, int alpha, int beta, boolean maximizingPlayer ) {


		if (depth == AgentController.minDepth) {
			setReachedLeafNodes(getReachedLeafNodes() + 1 );
			return (int) AgentController.getDynamicHeuristic(gameState);
		}


		setSearchDepth(depth);
		setNodesExamined(getNodesExamined() + 1 );



		if (maximizingPlayer) {
			int maxEval = Integer.MIN_VALUE;

			List <ObjectiveWrapper> maxPlayerMoves = AgentController.getAvailableMoves(gameState,PlayerTurn.PLAYER_ONE);

			for (int i = 0 ; i < maxPlayerMoves.size(); i++) {

				GameBoardState childState = AgentController.getNewState(gameState,maxPlayerMoves.get(i));

				int eval = getMiniMax(childState,depth-1,alpha,beta,false);

				maxEval = Math.max(maxEval,eval);
				alpha = Math.max(alpha,eval);
				gameState.addChildStates(childState);
				gameState.setStateHeuristic(maxEval);


				if (beta <= alpha) {
					setPrunedCounter(getPrunedCounter() + 1 );
					prunedCounter += getPrunedCounter();
					break;
				}


			}

			return maxEval;
		}
		else  {
			int minEval = Integer.MAX_VALUE;

			List <ObjectiveWrapper> minPlayerMoves = AgentController.getAvailableMoves(gameState,PlayerTurn.PLAYER_TWO);

			for (int i = 0 ; i < minPlayerMoves.size(); i++) {
				GameBoardState childState = AgentController.getNewState(gameState,minPlayerMoves.get(i));

				int eval = getMiniMax(childState,depth-1,alpha,beta,true);
				minEval = Math.min(minEval,eval);
				beta = Math.min(beta,eval);
				gameState.addChildStates(childState);
				gameState.setStateHeuristic(minEval);

				if (beta <= alpha) {
					setPrunedCounter(getPrunedCounter() + 1 );
					prunedCounter += getPrunedCounter();
					break;
				}


			}

			return minEval;

		}


	}

	private void resetStatistics () {
		setNodesExamined(0);
		setPrunedCounter(0);
		setReachedLeafNodes(0);
		setSearchDepth(0);
	}


	/**
	 * Default template move which serves as an example of how to implement move
	 * making logic. Note that this method does not use Alpha beta pruning and
	 * the use of this method can disqualify you
	 * 
	 * @param gameState
	 * @return
	 */
	private AgentMove getExampleMove(GameBoardState gameState){
		
		int waitTime = UserSettings.MIN_SEARCH_TIME; // 1.5 seconds
		
		ThreadManager.pause(TimeSpan.millis(waitTime)); // Pauses execution for the wait time to cause delay
		
		return AgentController.getExampleMove(gameState, playerTurn); // returns an example AI move Note: this is not AB Pruning
	}

}
