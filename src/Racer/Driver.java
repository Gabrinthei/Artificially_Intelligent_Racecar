/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.util.*;

/**
 *
 * @author James Sonntag
 * 
 * The class that controls the car
 * 
 */
public class Driver {
    public State2 currentState;
    public int xVel;
    public int yVel;
    Main m;
    public Actions action;
    public HashMap<State2, Actions> save = new HashMap<State2, Actions>();
    public State2 previousState;

    public Driver(Main m, State2 s, int xV, int yV)
    {
        this.m = m;
        currentState = s;
        xVel = xV;
        yVel = yV;
    }
    
    public Actions makeChoice(HashMap<State2, Double> Utility, HashMap<State2, Double> Reward)
    {
        go(Utility);
        return action;
    }
    
    //This is the randomness of the actions
    public Actions nonDeterminism(Actions act)
    {
        double random = Math.random();
        if(random >= .2)
        {
            //System.out.println("Action Successful");
            return act;
        }
        else
        {
            //System.out.println("Action failed");
            return new Actions(0,0);
        }
    }
    
    //The piece that gets called from the main class to implement the decisions of the Driver
    public void go(HashMap<State2, Double> Utility)
    {
        Percept p = new Percept(currentState, m.Reward.get(currentState));
        Agent ag = new Agent(p, m);
        Actions act = ag.run();
        action = nonDeterminism(act);
        previousState = currentState;
        currentState = move2(act, Utility, ag);
        save.put(currentState, action);
    }
    
    public State2 move2(Actions act, HashMap<State2, Double> Utility, Agent agent)
    {
        int xChange = currentState.xVel + act.x;
        int yChange = currentState.yVel + act.y;
        State2 next = new State2(currentState.xLocation+xChange, currentState.yLocation+yChange, xChange, yChange, m);
        if(m.findState(next, Utility).xLocation != 9 && m.Raceway[m.findState(next, Utility).xLocation][m.findState(next, Utility).yLocation] != '#')
                    return findState(next, Utility);
        else
        {            
            ArrayList<Actions> l = m.negaPolicy.get(currentState.print());
            if(null == l)
            {
                l = new ArrayList<Actions>();
            }
            boolean test = false;
            for(Actions a: l)
            {
                if(a.x == act.x && a.y == act.y)
                {
                    test = true;
                }
            }
            if(test == false)
                l.add(act);
            
            m.negaPolicy.put(currentState.print(), l);
            next = m.findState(crashSite(act), Utility);
            m.crashes++;
            return next;
            
        }
    }
    
    //checks to see if the given stat and it's actions are within the bounds of the track
    public boolean withinBounds(State2 currentState, int x, int y)
    {
        if(currentState.xLocation+x > -1 && currentState.yLocation+y > -1 && currentState.xLocation+x < m.Max_X && currentState.yLocation+y < m.Max_Y)
        {
            return true;
        }
        return false;
    }
    
    
    public State2 crashSite(Actions act)
    {
        State2 split1 = new State2(currentState,0,0);
        State2 split2 = new State2(currentState,0,0);
        State2 converge = new State2(currentState,0,0);
        State2 crash = new State2(currentState,0,0);
        int xChange = currentState.xVel + act.x;
        int yChange = currentState.yVel + act.y;
        int xTry = 0;
        int yTry = 0;
        
        while(withinBounds(converge, converge.xLocation, converge.yLocation) && m.Raceway[converge.xLocation][converge.yLocation] != '#' && m.Raceway[converge.xLocation][converge.yLocation] != 'F')
        {
            if(xChange < 0)
                xTry--;
            else if(xChange > 0)
                xTry++;
            if(yChange < 0)
                yTry--;
            else if(yChange > 0)
                yTry++;
            
            split1.xLocation = xTry;
            split2.yLocation = yTry;

            if(withinBounds(split1, split1.xLocation, split1.yLocation) && m.Raceway[split1.xLocation][split1.yLocation] != '#' && m.Raceway[split1.xLocation][split1.yLocation] != 'F')
            {
                if(withinBounds(split2, split2.xLocation, split2.yLocation) && m.Raceway[split2.xLocation][split2.yLocation] != '#' && m.Raceway[split2.xLocation][split2.yLocation] != 'F')
                {
                    converge.xLocation = split1.xLocation;
                    converge.yLocation = split2.yLocation;
                    if(withinBounds(converge, converge.xLocation, converge.yLocation) && m.Raceway[converge.xLocation][converge.yLocation] != '#' && m.Raceway[converge.xLocation][converge.yLocation] != 'F')
                    {
                        split1.yLocation = converge.yLocation;
                        split2.xLocation = converge.yLocation;
                    }
                    else
                        crash = split1;

                }
                else
                    crash = split2;
            }
            else
                crash = converge;
        }
        return crash;
    }
    
    public State2 move(Actions act, HashMap<State2, Double> Utility)
    {
        
        int xChange = currentState.xVel + act.x;
        int yChange = currentState.yVel + act.y;
        
        if(currentState.xLocation+xChange > -1 && currentState.xLocation+xChange < m.Max_X)
        {
            if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange < m.Max_Y)
            {
                action = act;
                State2 next = new State2(currentState.xLocation+xChange, currentState.yLocation+yChange, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange > m.Max_Y-1)
            {
                action = act;
                State2 next = new State2(currentState.xLocation+xChange, m.Max_Y-1, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange < 0)
            {
                action = act;
                State2 next = new State2(currentState.xLocation+xChange, 0, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
        }
        else if(currentState.xLocation+xChange > m.Max_X)
        {
            if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange < m.Max_Y)
            {
                action = act;
                State2 next = new State2(m.Max_X-1, currentState.yLocation+yChange, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange > m.Max_Y-1)
            {
                action = act;
                State2 next = new State2(m.Max_X-1, m.Max_Y-1, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange < 0)
            {
                action = act;
                State2 next = new State2(m.Max_X-1, 0, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
        }
        System.out.println("move failed: " + currentState.xLocation + " " + currentState.yLocation + " " + xChange + " " + yChange);
        return currentState;
    }
    
    public Actions choice3(HashMap<State2, Double> Utility, HashMap<State2, Double> Reward)
    {
        
        double best = Double.NEGATIVE_INFINITY;
        Actions use = new Actions(0,0);
        double currentUtil = Double.NEGATIVE_INFINITY;
        for(State2 next: currentState.possibleStates())
        {
            double total = 0;
            for(Actions act: m.getActions())
            {
                total += m.mdp.Probability(next, currentState, act, m)*Utility.get(next);
            
                if(total != 0.0)
                {
                    if(best < total)
                    {
                        System.out.println(currentState.xLocation + " " + currentState.yLocation);
                        System.out.println("TOtal " + total);
                        best = total;
                        use = act;
                    }
                }
            }
            if(m.mdp.Probability(next, currentState, use, m)*Utility.get(next)  > currentUtil)
            {
                currentUtil = m.mdp.Probability(next, currentState, use, m)*Utility.get(next);
                action = use;
            }
        }
        return use;
    }
    
    public State2 choice2(HashMap<State2, Double> Utility, HashMap<State2, Double> Reward)
    {
        State2 Keep = currentState;
        Actions a = new Actions(0,0);
        for(Map.Entry<State2, Actions> test: m.decision.entrySet())
        {
            if(currentState.xLocation == test.getKey().xLocation && currentState.yLocation == test.getKey().yLocation && currentState.xVel == test.getKey().xVel && currentState.yVel == test.getKey().yVel)
            {
                a = test.getValue();
            }
        }
        System.out.println("Accel: " + a.x + " " + a.y);
        int xChange = currentState.xVel + a.x;
        int yChange = currentState.yVel + a.y;
        System.out.println("Change: " + xChange + " " + yChange);
        
        if(currentState.xLocation+xChange > -1 && currentState.xLocation+xChange < m.Max_X)
        {
            if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange < m.Max_Y)
            {
                action = a;
                State2 next = new State2(currentState.xLocation+xChange, currentState.yLocation+yChange, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange > m.Max_Y-1)
            {
                action = a;
                State2 next = new State2(currentState.xLocation+xChange, m.Max_Y-1, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange < 0)
            {
                action = a;
                State2 next = new State2(currentState.xLocation+xChange, 0, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
        }
        else if(currentState.xLocation+xChange > m.Max_X-1)
        {
            if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange < m.Max_Y)
            {
                action = a;
                State2 next = new State2(m.Max_X-1, currentState.yLocation+yChange, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                        return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange > -1 && currentState.yLocation+yChange > m.Max_Y-1)
            {
                action = a;
                State2 next = new State2(m.Max_X-1, m.Max_Y-1, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
            else if(currentState.yLocation+yChange < 0)
            {
                action = a;
                State2 next = new State2(m.Max_X-1, 0, xChange, yChange, m);
                if(m.findState(next, Utility).xLocation != 9)
                    return findState(next, Utility);
            }
        }
                        
        action = a;
        return Keep;
    }
    
     public State2 findState(State2 next, HashMap<State2, Double> Utility)
    {
        String key = Integer.toString(next.xLocation)+ Integer.toString(next.yLocation) + Integer.toString(next.xVel) + Integer.toString(next.yVel);
        try{
            int x = m.keyLookup.get(key).xLocation;
        }catch(Exception e)
        {
            return new State2(9,9,9,9, m);
        }
                
        return m.keyLookup.get(key);
    }
    
    public State2 choice(HashMap<State2, Double> Utility)
    {
        State2 next = new State2();
        State2 Keep = new State2(currentState.xLocation, currentState.yLocation, currentState.xVel, currentState.yVel, m);
        int xChange=0;
        int yChange=0;
        Actions a = new Actions(0, 0);
        for(int i = -1; i < 2; i++)
            for(int j = -1; j < 2; j++)
            {
                if(xVel + i < 6 && xVel + i > -6)
                {
                    xChange = xVel + i;
                    if(yVel + j < 6 && yVel + j > -6)
                    {
                        yChange = yVel + j;
                        
                        if(currentState.xLocation+xChange > -1 && currentState.xLocation+xChange < m.Max_X && currentState.yLocation+yChange > -1 && currentState.yLocation+yChange < m.Max_Y)
                        {
                            next = new State2(currentState.xLocation+xChange, currentState.yLocation+yChange, xChange, yChange, m);
                            if(Utility.get(next) > Utility.get(Keep))
                                Keep = next;
                        }
                    }
                }
            }
        return Keep;
        
    }
}
