/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author James Sonntag
 * 
 * A class that holds the values of each location and it's possible velocities.
 * 
 */
public class State2 {
    
    
    int xLocation = 0;
    int yLocation = 0;
    int xVel = 0;
    int yVel = 0;
    Main m;
    
    
    public State2()
    {
        
    }
    
    public State2(int xloc, int yloc, int xvel, int yvel, Main m)
    {
        xLocation = xloc;
        yLocation = yloc;
        xVel = xvel;
        yVel = yvel;
        this.m = m;
    }
    
    public State2(State2 last, Actions act)
    {
        xLocation = last.xLocation+last.xVel+act.x;
        yLocation = last.yLocation+last.yVel+act.y;
        xVel = last.xVel+act.x;
        yVel = last.yVel+act.y;
    }
    
    public State2(State2 copy)
    {
        xLocation = copy.xLocation;
        yLocation = copy.yLocation;
        xVel = copy.xVel;
        yVel = copy.yVel;
        this.m = copy.m;
    }
    
    public State2(State2 copy, int xv, int yv)
    {
        xLocation = copy.xLocation;
        yLocation = copy.yLocation;
        xVel = xv;
        yVel = yv;
        this.m = copy.m;
    }
    
    public List<State2> possibleStates()
    {
        List<State2> list = new ArrayList<State2>();
        for(int x = -1; x < 2; x++)
        {
            for(int y = -1; y < 2; y++)
            {
                if(xVel+x > -6 && xVel+x < m.Max_X && yVel+y > -6 && yVel+y < m.Max_Y)
                {
                    State2 next = new State2(xLocation+xVel+x, yLocation+yVel+y, xVel+x, yVel+y, m);
                    if(m.findState(next, m.getUtility()).xLocation != 9)
                        list.add(m.findState(next, m.getUtility()));
                }
            }
        }
        return list;
    }
    
    public List<State2> possibleStates(Actions a)
    {
        List<State2> list = new ArrayList<State2>();
        if(xVel+a.x > -6 && xVel+a.x < m.Max_X && yVel+a.y > -6 && yVel+a.y < m.Max_Y)
        {
            State2 next = new State2(xLocation+xVel+a.x, yLocation+yVel+a.y, xVel+a.x, yVel+a.y, m);
            if(m.findState(next, m.getUtility()).xLocation != 9)
                list.add(m.findState(next, m.getUtility()));
        }
        return list;
    }
    
    public List<Actions> possibleActions()
    {
        List<Actions> list = new ArrayList<Actions>();
        List<Actions> l = Arrays.asList(new Actions(0,0));
        Actions a = new Actions(0,0);
        for(int x = -1; x < 2; x++)
        {
            for(int y = -1; y < 2; y++)
            {
                boolean test = false;
                if(xVel+x > -6 && xVel+x < 6 && yVel+y > -6 && yVel+y < 6)
                {
                    if(m.negaPolicy.containsKey(this.print()))
                    {
                        for(Map.Entry<String, ArrayList<Actions>> s: m.negaPolicy.entrySet())
                        {
                            if(s.getKey().equals(this.print()))
                            {
                                for(Actions p: s.getValue())
                                {   
                                    if(p.x == x && p.y == y)
                                    {
                                        test = true;
                                    }
                                }
                                if(test == false)
                                {
                                    list.add(new Actions(x,y));
                                }
                            }
                        }
                    }else
                    {
                        list.add(new Actions(x,y));
                    }
                }
            }
        }
        return list;
    }
    
    public String print()
    {
        String s = (Integer.toString(xLocation) + " " + Integer.toString(yLocation) + " " + Integer.toString(xVel) + " " + Integer.toString(yVel));
        return s;
    }
    
}
