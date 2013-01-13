/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author James Sonntag
 * 
 * A basic holding class for the MDP
 * 
 */
public class MDP {
    List<State2> states;
    List<Actions> actions;
    
    public MDP(List<State2> s, List<Actions> a)
    {
        states = s;
        actions = a;
    }
    
    public List<State2> returnStates()
    {
        return states;
    }
    
    public State2 initialState()
    {
        return states.get(0);
    }
    
    public List<Actions> getActions(State2 s)
    {
        return actions;
    }
    
    public double Probability(State2 sPrime, State2 sCurrent, Actions action, Main m)
    {
        for(State2 l: sCurrent.possibleStates(action))
        {
            if(sPrime.equals(l))
                return 0.8;
        }
        return 0.2;
    }
    
}
