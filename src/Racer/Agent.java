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
 *
 * @author James Sonntag
 * 
 * The class that makes the decision factors for the car.
 * 
 */
public class Agent {
    public Percept percept;
    HashMap<State2, Actions> Policy = new HashMap<State2, Actions>();
    
    public Main m;
    public MDP mdp = null;
    public HashMap<State2, Double> Reward = new HashMap<State2, Double>();
    public State2 previousState = null;
    public Actions previousAction = null;
//    public HashMap<String, Integer> Frequency = new HashMap<String, Integer>();
//    public HashMap<String, Integer> FrequencyPrime = new HashMap<String, Integer>();
    Frequency f = new Frequency();
    FrequencyPrime fp = new FrequencyPrime();
    public HashMap<String, State2> Prob = new HashMap<String, State2>();
    int times = 0;
    
    public Agent(Percept perc, Main m)
    {
        this.percept = perc;
        this.m = m;
    }
    
    public Actions run()
    {
        State2 sPrime = percept.state;
        double rPrime = percept.reward;
        
        if(!m.Utility.containsKey(sPrime))
        {
            m.Utility.put(sPrime, rPrime);
            Reward.put(sPrime, rPrime);
        }
        if(null != previousState)
        {
            StateActionPair sa = new StateActionPair(previousState, previousAction);
            f.increment(sa);
            fp.increment(sa);
            for(State2 t : m.states)
            {
                StateActionPair tsa = new StateActionPair(previousState, previousAction, t);
                if(fp.getFreq(tsa) != 0)
                {
                    Prob.put(tsa.toString(), t);
                }
            }
        }
//        if(m.timesAround > 0)
//            m.Utility.putAll(valIt(m.mdp, m.Utility, .001, .99));
        
        if(Terminal(sPrime))
        {
            System.out.println("Term");
            previousState = null;
            previousAction = null;
        }
        else{
            previousState = sPrime;
            //if(m.timesAround > 0)
            previousAction = doIt(sPrime);
                
//            else{
//                previousAction = needForSpeed(sPrime);
//            }
        }
        m.trial++;
        System.out.println(m.trial);
        return previousAction;
    }
    
    public Actions doIt(State2 s)
    {
        State2 better = s;
        Actions use = new Actions(0,0);
        double best = m.Utility.get(s);
        if(m.Utility.containsKey(s))
        {
            for(Actions a: s.possibleActions())
            {
                State2 next = new State2(s, a);
                next = m.findState(next, m.Utility);
                if(next.xLocation != 9)
                {
                    if(m.Utility.get(next) > best)
                    {
                        best = m.Utility.get(next);
                        better = next;
                        use = a;
                    }
                }
                else
                    use = needForSpeed(s);
            }
        }
        
        if(m.save.containsKey(s))
        {
            for(State2 state: m.save.keySet())
            {
                if(null != m.Utility.get(state))
                {
                    if(state.equals(s) && m.Utility.get(state) > best)
                    {
                        best = m.Utility.get(state);
                        better = state;
                        use = m.save.get(state);
                    }
                }
            }
        }
        Actions checkPast = best2(s);
        State2 next = new State2(s,checkPast);
        next = m.findState(next, m.Utility);
        if(next.xLocation != 9)
        {
            if(m.Utility.get(next) > best)
            {
                best = m.Utility.get(next);
                better = next;
                use = checkPast;
            }
        }
        return use;
    }
    
    
    public Actions needForSpeed(State2 s)
    {
        Actions action = m.decision.get(s);
        List<Actions> l = new ArrayList<Actions>();
        int xSpeed = s.xVel;
        int ySpeed = s.yVel;
        
        for(Actions a: s.possibleActions())
        {
            if(Math.abs(xSpeed) > Math.abs(ySpeed))
            {
                if(s.xVel <= 0 && a.x + s.xVel <= s.xVel)
                {
                    l.add(a);
                }
                else if(s.xVel >= 0 && a.x + s.xVel >= s.xVel)
                {
                    l.add(a);
                }
            }
            else if(Math.abs(ySpeed) > Math.abs(xSpeed))
            {
                if(s.yVel <= 0 && a.y + s.yVel <= s.yVel)
                {
                    l.add(a);
                }
                else if(s.yVel >= 0 && a.y + s.yVel >= s.yVel)
                {
                    l.add(a);
                }
            }
            else{
                if(s.xVel <= 0 && a.x + s.xVel <= s.xVel)
                {
                    l.add(a);
                }
                else if(s.xVel >= 0 && a.x + s.xVel >= s.xVel)
                {
                    l.add(a);
                }
                if(s.yVel <= 0 && a.y + s.yVel <= s.yVel)
                {
                    l.add(a);
                }
                else if(s.yVel >= 0 && a.y + s.yVel >= s.yVel)
                {
                    l.add(a);
                }
            }
        }
        
        l.add(action);
        
        int x = l.size();
        double rand = Math.random()*10;
        while((int)rand >= x || (int)rand < 0)
        {
            rand = Math.random()%l.size() * 10;
        }
        Actions act = l.get((int)rand);
        return act;
    }
    
    public Actions randomAct(State2 s)
    {
        Actions act = new Actions(0,0);
        int x = s.possibleActions().size();
        double rand = Math.random()*10;
        while((int)rand >= x || (int)rand < 0)
        {
            rand = Math.random()*10;
        }
        act = s.possibleActions().get((int)rand);
        
        return act;
    }
    
    public Actions best2(State2 s)
    {    
        if(m.save.containsKey(s))
        {
            return m.save.get(s);
        }else if(m.save.containsKey(s.possibleStates()))
        {
            for(Actions a: s.possibleActions())
            {
                State2 next = new State2(s,a);
                if(m.findState(next, m.Utility).xLocation != 9)
                {
                    if(m.save.containsKey(next))
                    {
                        return m.save.get(next);
                    }
                }
            }
        }else 
            return needForSpeed(s);
        
        
        
        double max = Double.NEGATIVE_INFINITY;
        Actions act = new Actions(0,0);
        for(Actions q: s.possibleActions())
        {
            State2 next = new State2(s,act);
            if(m.findState(next, m.Utility).xLocation != 9)
            {
                if(m.Utility.get(m.findState(next, m.Utility)) > max)
                {
                    max = m.Utility.get(m.findState(next, m.Utility));
                    //System.out.println("Max: " + max);
                    act = q;
                }
            }
        }
        return act;
    }
    
    
    
    public Actions best(State2 s)
    {
        double good = Double.NEGATIVE_INFINITY;
        Actions act = new Actions(0,0);
        for(Actions a: s.possibleActions())
        {
            if(m.Utility.containsKey(s))
            {
                
                double best = m.Utility.get(s);
                if(best > good)
                {
                    good = best;
                    act = a;
                }
            }
        }
        if(act.x == 0 && act.y == 0)
        {
            int x = s.possibleActions().size();
            double rand = Math.random()*10;
            //System.out.println("Size " + x);
            while((int)rand >= x)
            {
                rand = Math.random()*10;
            }
            //System.out.println("Rand " + (int)rand);
            act = s.possibleActions().get((int)rand);
        }
        return act;
    }
    
    public HashMap<State2, Double> valIt(MDP mdp, HashMap<State2, Double> Utility, double error, double discount)
    {
        HashMap<State2, Double> Utilities = new HashMap<State2, Double>();
        Utilities.putAll(Utility);
        HashMap<State2, Double> UtilityPrime = createUtility(m.states, 0.0);
        UtilityPrime.putAll(Utilities);
        double maxChange = 0;
        double stopper = (error * (1 - discount) / discount);
        int checker = 0;
        
        do{
            Utilities.putAll(UtilityPrime);
            maxChange = 0;
            for(State2 current : mdp.returnStates())
            {
                List<Actions> actions = m.getActions();
                double bestAction = -100000.0;

                for(Actions a: current.possibleActions())
                {
                    double totalAction = 0.0;
                    for(State2 sPrime: mdp.returnStates())
                    {
                        totalAction += (mdp.Probability(sPrime, current, a, m) * Utilities.get(sPrime));
                    }
                    if(totalAction > bestAction)
                    {
                        bestAction = totalAction;
                        //System.out.println("Best: " + bestAction + " " + a.x + " " + a.y + "at " + current.xLocation + " " + current.yLocation + "with " + current.xVel + " " + current.yVel);
                    }
                }
                //System.out.println("do");
                //System.out.println(current.xLocation + " " + current.yLocation + " " + current.xVel + " " + current.yVel);
                
                //System.out.println(current.equals(new State2(1,1,-5,-5)))
                if(null != Utility.get(current))
                    UtilityPrime.put(current, Utility.get(current) + discount * bestAction);
                
//                System.out.println("Utilities: " +UtilityPrime.get(current) + " " + Utility.get(current));
                double difference = Math.abs(UtilityPrime.get(current) - Utilities.get(current));
//                System.out.println(difference);
//                System.out.println(maxChange);
//                System.out.println(stopper);
                if(difference > maxChange)
                    maxChange = difference;
//                System.out.println(maxChange);
            }
            //System.out.println("WorksAgain: " + maxChange + " and stopper: " + stopper);
            checker++;
        } while(maxChange > stopper);
        
        //ValueRacer.keyLookup = getKey;
        
        return Utilities;
    }
    
    public boolean Terminal(State2 s)
    {
        boolean term = false;
        if(s.possibleActions().size() == 0)
            term = true;
        return term;
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
