package adasim.algorithm.routing;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jdom.Element;

import adasim.model.AdasimMap;
import adasim.model.ConfigurationException;
import adasim.model.RoadSegment;
import adasim.model.TrafficSimulator;
import adasim.model.internal.FilterMap;
import adasim.model.internal.SimulationXMLReader;
import adasim.model.internal.VehicleManager;

public class QLearningRoutingAlgorithm  extends AbstractRoutingAlgorithm {

	private boolean finished = false;
	
    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

 
    private   int statesCount  ;

    private final int reward = 100;
    private final int penalty = -10;
 
    private int[][] R;       // Reward lookup
    private double[][] Q;    // Q learning

    
	private final int lookahead;
	private final int recompute;
	private int steps;
	private List<RoadSegment> path;
	
    RoadSegment currentSource =null;
    
	private final static Logger logger = Logger.getLogger(LookaheadShortestPathRoutingAlgorithm.class);
	
	public QLearningRoutingAlgorithm() {
		this(2,2); 
	}
    public QLearningRoutingAlgorithm( int lookahead, int recomp ){
		this.lookahead = lookahead;
		this.recompute = recomp;
		this.steps = 0;
		logger.info( "QLearningRoutingAlgorithm(" + lookahead + "," + recompute +")" );
 
 
         
    }
   
  
    /*
     * Map Road segment to 2d array for q learninig to work on
     * 
     * 
     */
    public void mapSegmentToArray(List<RoadSegment> listOfRoadSegment ) {
    	 Collections.sort(listOfRoadSegment); // Make sure we alwasy loading sorted Map
    	 
    	 
    	 //int dim = listOfRoadSegment.size() +1; // dimention of 2d array
    	 R = new int[statesCount][statesCount];
         Q = new double[statesCount][statesCount];
    	 
    	 // set all cells with -1 for the impossible transitions
    	 for(int i=0;i<statesCount ;i++) {
    		 for(int j=0;j<statesCount ;j++) {
    			R[i][j]= -1; 	 
    		 }
    		 
    	 }
    	 
    	 
    	 // set  cells with 0 where agent could move 
    	 for (RoadSegment roadSegment : listOfRoadSegment) {
    		 for (RoadSegment roadSegmentNeighbors : roadSegment.getNeighbors()) {
    			 R[roadSegment.getID()][roadSegmentNeighbors.getID()]= 0;  // forword drive
    			// R[roadSegmentNeighbors.getID()][roadSegment.getID()]= 0;  //backword drive
    			 
    			 // set reword for reaching the target
    			 if(roadSegmentNeighbors.getID() == target.getID() ) {
    				 R[roadSegment.getID()][roadSegmentNeighbors.getID()]= reward;  
    			 }
//    			 if(roadSegment.getID() == target ) {
//    				 R[roadSegmentNeighbors.getID()][roadSegment.getID()]= reward;  
//    			 }
    		 }

   		 } 
    	 
    	 // set reword for reaching goal 
    	 
    	 //R[target][target]= reward; 
    	  initializeQ();
    
    }
 
    
    //Set Q values to R values
    void initializeQ()
    {
        for (int i = 0; i < statesCount; i++){
            for(int j = 0; j < statesCount; j++){
                Q[i][j] = (double)R[i][j];
            }
        }
    }
    
    // Used for debug
    void printR() {
        System.out.printf("%25s", "States: ");
        for (int i = 0; i < statesCount; i++) {
            System.out.printf("%4s", i);
        }
        System.out.println();

        for (int i = 0; i < statesCount; i++) {
            System.out.print("Possible states from " + i + " :[");
            for (int j = 0; j < statesCount; j++) {
                System.out.printf("%4s", R[i][j]);
            }
            System.out.println("]");
        }
    }

    void calculateQ() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) { // Train cycles
           
        	// Select random initial state
            int crtState = currentSource.getID();// set source base on input rand.nextInt(statesCount);

            while (!isFinalState(crtState)) {
                int[] actionsFromCurrentState = possibleActionsFromState(crtState);

                // Pick a random action from the ones possible
                int index = rand.nextInt(actionsFromCurrentState.length);
                int nextState = actionsFromCurrentState[index];

                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
                
                double q = Q[crtState][nextState];
                double maxQ = maxQ(nextState);
                int r = R[crtState][nextState];

                double value = q + alpha * (r + gamma * maxQ - q);
                Q[crtState][nextState] = value;

                crtState = nextState;
            }
        }
    }

    boolean isFinalState(int state) {
        
        return state ==  target.getID();
    }

    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < statesCount; i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);
        //the learning rate and eagerness will keep the W value above the lowest reward
        double maxValue = -10;
        for (int nextAction : actionsFromState) {
            double value = Q[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    void printPolicy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < statesCount; i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
    }

    int getPolicyFromState(int state) {
        int[] actionsFromState = possibleActionsFromState(state);

        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state;

        // Pick to move to the state that has the maximum Q value
        for (int nextState : actionsFromState) {
            double value = Q[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    void printQ() {
        System.out.println("\nQ matrix");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("From state " + i + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                System.out.printf("%6.2f ", (Q[i][j]));
            }
            System.out.println();
        }
    }
	@Override
	public List<RoadSegment> getPath(RoadSegment from, RoadSegment to) {
		
		currentSource = from;
		
		// get path
        List<RoadSegment> nodes = graph.getRoadSegments();
    	statesCount = nodes.size();
        mapSegmentToArray(nodes);  // convert segments to 2d array
        
        calculateQ();  // calculate q-learninig
        
        System.out.println("========================Source: " + currentSource.getID() +", Target: "+ target.getID() + "===============================");
        
        printR();
        printQ();
        printPolicy();
       
    	List<RoadSegment> newListOfNodes = new ArrayList<RoadSegment>( );
    	 
    	// get path using stack
    	
        Stack<Integer> st= new Stack<Integer>();
        st.add(getPolicyFromState(from.getID()));
        
        while( !st.isEmpty()){
        	int currentState= st.pop();
        	newListOfNodes.add(nodes.get(currentState));
        	
        	if(currentState == to.getID()) {
        		break;
        	}
        	int nextSate = getPolicyFromState(currentState);
        	st.add(nextSate);
        	
        }
        
        System.out.println("\nRoadSegments: ");
        for (RoadSegment roadSegment : newListOfNodes) {
			System.out.println("\tSegment ID:"+roadSegment.getID());
			System.out.println("\t \tNeighbors: "+ roadSegment.getNeighbors());
		}
        
        System.out.println("=======================================================");
        
        
        
    	if ( newListOfNodes == null ) {
			finished = true;
		}
    	
        return newListOfNodes;
	}
	
	
	@Override
	public RoadSegment getNextNode() {
		
		if ( finished ) return null;
		if ( path == null ) {
			path = getPath(source);
			logger.info( pathLogMessage() );
		}
		assert path != null || finished;
		if ( path == null || path.size() == 0 ) {
			finished = true;
			return null;
		}
		if ( ++steps == recompute ) {
			RoadSegment next = path.remove(0);
			path = getPath(next);
			logger.info( "UPDATE: " + pathLogMessage() );
			steps = 0;
			return next;
		} else {
			return path.remove(0);
		}
	}
	
	/**
	 * Computes a path to the configured target node starting from
	 * the passed <code>start</code> node.
	 * @param start
	 */
	private List<RoadSegment> getPath(RoadSegment start) {
		
		List<RoadSegment> p = getPath( start, target );
		if ( p == null ) {
			finished = true;
		}
		return p;
	}
	
	private String pathLogMessage() {
		StringBuffer buf = new StringBuffer( "PATH: Vehicle: " );
		buf.append( vehicle.getID() );
		buf.append( " From: " );
		buf.append( source.getID() );
		buf.append( " To: " );
		buf.append( target.getID() );
		buf.append( " Path: " );
		buf.append( path == null ? "[]" : path );
		return buf.toString();
	}
}