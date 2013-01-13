/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author James Sonntag
 * 
 * This creates and returns a HashMap of the States and utilities.
 */
public class ValueIteration2 {
    Main ValueRacer;
    MDP mdp;
    List<State2> states = new ArrayList<State2>();
    public double discount = 0;
    HashMap<State2, Double> Reward = new HashMap<State2, Double>();
    HashMap<String, State2> getKey = new HashMap<String, State2>();
    boolean hasStart = false;
    
    public ValueIteration2(Main ValueRacer, double discount)
    {
        this.ValueRacer = ValueRacer;
        if(discount > 1.0 || discount <= 0.0){
            throw new IllegalArgumentException("Discount must be > 0 and <= 1.0");
        }
        this.discount = discount;
    }
    
    public List<Actions> getActions()
    {
        List<Integer> xActions = new ArrayList<Integer>();
        List<Integer> yActions = new ArrayList<Integer>();
        List<Actions> actions = new ArrayList<Actions>();
        for(int i = -1; i < 2; i++)
        {
            xActions.add(i);
            yActions.add(i);
        }
        for(int i: xActions)
        {
            for(int j: yActions)
            {
                actions.add(new Actions(i,j));
            }
        }
        return actions;
    }
    
    public HashMap<State2, Double> go()
    {
        initialize();
        mdp = new MDP(states, getActions());
        return iterate(mdp, 0.4);
    }
    
    //Scans through the recently created matrix and assigns values/rewards for each state.
    public void initialize()
    {
        for(int i = 0; i < ValueRacer.Max_X; i++)
            for(int j = 0; j < ValueRacer.Max_Y; j++)
            {
                if(ValueRacer.Raceway[i][j] == '.')
                {
                    for(int k = -5; k < 6; k++)
                    {
                        for(int l = -5; l < 6; l++)
                        {
                            State2 use = new State2(i, j, k, l, ValueRacer);
                            setRelation(i,j,k,l,use);
                            Reward.put(use, ValueRacer.Move);
                            states.add(use);
                        }
                    }
                }
                else if(ValueRacer.Raceway[i][j] == 'F')
                {
                    for(int k = -5; k < 6; k++)
                    {
                        for(int l = -5; l < 6; l++)
                        {
                            State2 use = new State2(i, j, k, l, ValueRacer);
                            Reward.put(use, ValueRacer.Finish);
                            setRelation(i,j,k,l,use);
                            states.add(use);
                        }
                    }
                }
                else if(ValueRacer.Raceway[i][j] == 'S')
                {
                    for(int k = -5; k < 6; k++)
                    {
                        for(int l = -5; l < 6; l++)
                        {                          
                            State2 use = new State2(i, j, k, l, ValueRacer);
                            if(!hasStart && k == 0 && l == 0)
                            {
                                ValueRacer.car = new Driver(ValueRacer, use, 0, 0);
                                hasStart = true;
                            }
                            Reward.put(use, ValueRacer.Start);
                            setRelation(i,j,k,l,use);
                            states.add(use);
                        }
                    }
                }
            }
        ValueRacer.Reward.putAll(Reward);
        ValueRacer.keyLookup = getKey;
        
    }
    
    public State2 noAction(State2 current)
    {
        return new State2(current.xLocation+current.xVel, current.yLocation+current.yVel, current.xVel, current.yVel, ValueRacer);
    }
    
    //Runs through the newly created MDP and creates/returns a hashmap table
    public HashMap<State2, Double> iterate(MDP mdp, double error)
    {
        HashMap<State2, Double> Utility = createUtility(mdp.returnStates(), 0.0);
        HashMap<State2, Double> UtilityPrime = createUtility(mdp.returnStates(), 0.0);
        double maxChange = 0;
        double stopper = (error * (1 - discount) / discount);
        int checker = 0;
        
        
        do{
            Utility.putAll(UtilityPrime);
            maxChange = 0;
            for(State2 current : mdp.returnStates())
            {
                List<Actions> actions = ValueRacer.getActions();
                double bestAction = Double.NEGATIVE_INFINITY;

                for(Actions a: actions)
                {
                    double totalAction = 0.0;
                    for(State2 sPrime: current.possibleStates())
                    {
                        totalAction += (mdp.Probability(sPrime, current, a, ValueRacer) * Utility.get(sPrime));
                    }
                    if(totalAction > bestAction)
                    {
                        bestAction = totalAction;
                    }
                }
                UtilityPrime.put(current, Reward.get(current) + discount * bestAction);
                
                double difference = Math.abs(UtilityPrime.get(current) - Utility.get(current));
                
                if(difference > maxChange)
                    maxChange = difference;
            }
            checker++;
        } while(maxChange> stopper);
        
        return Utility;
    }
    
    public void setRelation(int xLoc, int yLoc, int xVel, int yVel, State2 state)
    {
        String key = Integer.toString(xLoc)+Integer.toString(yLoc)+Integer.toString(xVel)+Integer.toString(yVel);
        getKey.put(key, state);
    }
    
    public HashMap<State2, Double> createUtility(List<State2> states, Double util)
    {
        HashMap<State2, Double> Util = new HashMap();
        for(State2 i: states)
        {
            Util.put(i, util);
        }
        return Util;
    }
    
    
}
