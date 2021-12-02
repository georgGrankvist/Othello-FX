package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;

import java.util.List;


public class AgentABPruning extends Agent {

    private static final int MAX_DEPTH = 4;


    public AgentABPruning(String agentName, PlayerTurn playerTurn) {
        super(agentName, playerTurn);
    }

    public AgentABPruning(String agentName) {
        super(agentName);
    }


    public AgentABPruning(PlayerTurn playerTurn) {
        super(playerTurn);
    }


    @Override
    public AgentMove getMove(GameBoardState gameState) {

        if (AgentController.isTerminal(gameState, PlayerTurn.PLAYER_ONE)) {
            return new MoveWrapper(null);
        }

        statistics();


        long startTime = System.currentTimeMillis();
        int val = miniMax(gameState, MAX_DEPTH, PlayerTurn.PLAYER_ONE, Integer.MIN_VALUE, Integer.MAX_VALUE); //Recursively build tree
        long endTime = System.currentTimeMillis();
        long runTime = (startTime - endTime);
        System.out.println("Time: " + runTime + "ms");


        //Match a move with the return value of the minimaxAB
        ObjectiveWrapper theMove = null;
        List<GameBoardState> states = gameState.getChildStates();
        for (GameBoardState state : states) {
            if (state.getStateValue() == val) {
                System.out.println("VALUE " + val + "MATCHED WITH STATEHEURISTIC "  + state.getStateValue());
                theMove = state.getLeadingMove();
                break;
            }
        }

        return new MoveWrapper(theMove);
    }



        public int miniMax ( GameBoardState state, int depth, PlayerTurn playerTurn, int alpha, int beta ) {

        setSearchDepth(depth);
        setNodesExamined(getNodesExamined()+1);

        int optV = 0;

        if (depth == 0) { return (int)  AgentController.heuristicEvaluation(state, AgentController.HeuristicType.DYNAMIC, playerTurn ); }

        if (playerTurn.equals(PlayerTurn.PLAYER_ONE)) {
            List<ObjectiveWrapper> available = AgentController.getAvailableMoves(state, playerTurn);
            optV = Integer.MIN_VALUE;

            for (ObjectiveWrapper move:available) {
                GameBoardState child = AgentController.getNewState(state, move);
                optV = Math.max (optV, miniMax(child, depth-1, PlayerTurn.PLAYER_TWO, alpha, beta ));
                if (optV >= beta) {
                    setPrunedCounter(getPrunedCounter()+1);
                    state.setStateValue(optV);
                    return optV;
                }
                state.addChildState(child);
                alpha = Math.max(alpha, optV);
            }
            state.setStateValue(optV);
        }

        else if (playerTurn.equals(PlayerTurn.PLAYER_TWO)) {
            List<ObjectiveWrapper> available = AgentController.getAvailableMoves(state, playerTurn);
            optV = Integer.MAX_VALUE;

            for (ObjectiveWrapper move:available) {
                GameBoardState child = AgentController.getNewState(state, move);
                optV = Math.min (optV, miniMax(child, depth-1, PlayerTurn.PLAYER_ONE, alpha, beta ));
                if (optV <= alpha) {
                    setPrunedCounter(getPrunedCounter()+1);
                    state.setStateValue(optV);
                    return optV;
                }
                state.addChildState(child);
                beta = Math.min(beta, optV);
            }
            state.setStateValue(optV);
        }

            return optV;
    }


    private void statistics () {
        setNodesExamined(0);
        setPrunedCounter(0);
        setReachedLeafNodes(0);
        setSearchDepth(0);
    }


    public int getUtility(GameBoardState node) {
        setReachedLeafNodes(getReachedLeafNodes()+1);
        return (node.getWhiteCount() - node.getBlackCount());
    }
}


