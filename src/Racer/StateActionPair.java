/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

/**
 *
 * @author Vyse
 */
public class StateActionPair {
    State2 state = null;
    Actions action = null;
    State2 statePrime = null;
    
    public StateActionPair(State2 s, Actions a)
    {
        state = s;
        action = a;
    }
    
    public StateActionPair(State2 s, Actions a, State2 sPrime)
    {
        state = s;
        action = a;
        statePrime = sPrime;
    }
}
