/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This creates and returns the optimal policy for a given utility.
 * 
 * @author James Sonntag
 */
public class Policy {
    HashMap<State2, Actions> optPolicy;
    MDP mdp;
    HashMap<State2, Double> Utility;
    double discount = 1;
    Main m;
    
    public Policy(MDP mdp, HashMap<State2, Double> Utility, double discount, Main m)
    {
        this.mdp = mdp;
        this.Utility = Utility;
        this.discount = discount;
        this.m = m;
    }
    
    public HashMap<State2, Actions> go()
    {
        findPolicy(Utility, mdp);
        return optPolicy;
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
    
    public void findPolicy(HashMap<State2, Double> Utility, MDP mdp)
    {
        HashMap<State2, Actions> policy = new HashMap<State2, Actions>();
        Actions temp = new Actions(0,0);
        
        for(State2 current: mdp.returnStates())
        {
            double bestChange = Utility.get(current);
            for(Actions action: current.possibleActions())
            {
                double totalChange = 0;
                for(State2 sPrime: current.possibleStates())
                {
                    if(action.x != 0 && action.y != 0)
                        totalChange = mdp.Probability(sPrime, current, action, m) * Utility.get(sPrime);
                    if(totalChange < bestChange )
                    {
                        bestChange = totalChange;
                        temp = action;
                    }
                }
            }
            policy.put(current, temp);
        }
        optPolicy = policy;
    }
    
    public HashMap<State2, Actions> getBestPolicy(MDP mdp, HashMap<State2, Double> Utility, double discount, HashMap<State2, Double> Reward)
    {
        HashMap<State2, Double> Utilities = new HashMap<State2, Double>();
        Utilities.putAll(Utility);
        HashMap<State2, Actions> policy = createPolicy(mdp.returnStates());
        boolean unchanged;
        do{
            Utilities = evaluate(discount, Utilities, Reward);
            unchanged = true;
                    
            for(State2 current : mdp.returnStates())
            {
                double maxAction = 0;
                double piVal = 0;
                Actions actionArgMax = policy.get(current);
                for(Actions action: current.possibleActions())
                {
                    double totalActions = 0;
                    for(State2 statePrime: current.possibleStates(action))
                    {
                        if(action.x != 0 && action.y != 0)
                            totalActions += mdp.Probability(statePrime, current, action, m) * Utilities.get(statePrime) ;
                    }
                    
                    if(totalActions < maxAction)
                    {
                        maxAction = totalActions;
                        actionArgMax = action;
                    }
                    
                    if(action.x == policy.get(current).x && action.y == policy.get(current).y)
                    {
                        piVal = totalActions;
                    }
                }
                
                if(maxAction > piVal)
                {
                    policy.put(current, actionArgMax);
                    unchanged = false;
                }
                
                
            }
        } while(!unchanged);
        
        return policy;
    }    
    
    public HashMap<State2, Double> evaluate(double discount, HashMap<State2, Double> Utilities, HashMap<State2, Double> Reward)
    {
        HashMap<State2, Double> UtilityI = createUtility(mdp.returnStates(), 0.0);
        
            for(State2 current: m.states)
            {
                double totalUtil = 0;
                for(State2 sPrime: current.possibleStates())
                    totalUtil += (mdp.Probability(sPrime, current, optPolicy.get(current), m) * Utilities.get(sPrime))  ;
                            
                totalUtil = totalUtil*discount;
                totalUtil += Reward.get(current);
                UtilityI.put(current, totalUtil);
            }
        return UtilityI;
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
    
    public HashMap<State2, Actions> createPolicy(List<State2> states)
    {
        
        HashMap<State2, Actions> Util = new HashMap<State2, Actions>();
        for(State2 i: states)
        {
            Util.put(i, randomActs());
        }
        return Util;
    }
    
    public Actions randomActs()
    {
        int i = (int)(Math.random()*1);
        int j = (int)(Math.random()*1);
        if(Math.random() < .3)
        {
            i = i*-1;
        }
        if(Math.random() > .7)
            j = j*-1;
        return new Actions(i, j);
    }
}
