/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.util.HashMap;

/**
 *
 * @author Vyse
 */
public class FrequencyPrime {
    public HashMap<String, Integer> Frequency = new HashMap<String, Integer>();
    
    public FrequencyPrime()
    {
        
    }
    
    public void increment(StateActionPair sa)
    {
        int value = 0;
        try{
            String key =Integer.toString(sa.statePrime.xLocation) +Integer.toString(sa.statePrime.yLocation) +Integer.toString(sa.statePrime.xVel) +Integer.toString(sa.statePrime.yVel) + Integer.toString(sa.action.x) + Integer.toString(sa.action.y) + Integer.toString(sa.state.xLocation) + Integer.toString(sa.state.yLocation) + Integer.toString(sa.state.xVel) + Integer.toString(sa.state.yVel);
            if(Frequency.containsKey(key))
            {
                value = Frequency.get(key);
                Frequency.remove(key);
            }
        Frequency.put(key, value+1);
        }catch(Exception e){
            String key =Integer.toString(sa.statePrime.xVel) +Integer.toString(sa.statePrime.yVel) + Integer.toString(sa.action.x) + Integer.toString(sa.action.y) + Integer.toString(sa.state.xLocation) + Integer.toString(sa.state.yLocation) + Integer.toString(sa.state.xVel) + Integer.toString(sa.state.yVel);
            if(Frequency.containsKey(key))
            {
                value = Frequency.get(key);
                Frequency.remove(key);
            }
            Frequency.put(key, value+1);
            }
        
        
    }
    
    public int getFreq(StateActionPair sa)
    {
        String key =Integer.toString(sa.statePrime.xLocation) +Integer.toString(sa.statePrime.yLocation) +Integer.toString(sa.statePrime.xVel) +Integer.toString(sa.statePrime.yVel) + Integer.toString(sa.action.x) + Integer.toString(sa.action.y) + Integer.toString(sa.state.xLocation) + Integer.toString(sa.state.yLocation) + Integer.toString(sa.state.xVel) + Integer.toString(sa.state.yVel);
        if(Frequency.containsKey(key))
            return Frequency.get(key);
        return 0;
    }
}
