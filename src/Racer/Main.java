/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

import java.io.*;
import java.util.*;

/**
 *
 * @author James Sonntag
 * 
 * This is the main process that runs through all of the other classes and keeps track of all of the global variables
 */
public class Main {
    int growingTime = 20;
    public Object[][] arr;
    public int Max_X;
    public int Max_Y;
    public char[][] Raceway;
    public char[][] tempRaceway;
    public int[][] Policy;
    public int[][] QValue;
    public double Wall = -10.0;
    public double Finish = 1.0;
    public double Move = -1.0;
    public double Start = -10.0;
    public MDP mdp;
    public HashMap<State2, Double> Utility = new HashMap<State2, Double>();
    public HashMap<State2, Double> Reward = new HashMap<State2, Double>();
    public Driver car;
    public HashMap<State2, Actions> decision;
    public HashMap<String, State2> keyLookup;
    public List<State2> states = new ArrayList<State2>();
    public List<Double> testing = Arrays.asList(.1, .2, .3, .4, .5, .6, .7, .8, .9, .95, .98);
    public int crashes;
    public int trial = 0;
    public int timesAround = 0;
    private State2 firstState;
    public HashMap<String, ArrayList<Actions>> negaPolicy = new HashMap<String, ArrayList<Actions>>();
    public HashMap<State2, Actions> save = new HashMap<State2, Actions>();
    Policy policy;
    ValueIteration2 valueIteration;
    static String L = "L.txt";
    static String R = "R.txt";
    static String O = "O.txt";
    
    //This creates and runs through each track type.
    public static void main(String [] args)
    {
        Main l = new Main(L);
        Main r = new Main(R);
        Main o = new Main(O);
    }
    
    public void setInitial(State2 s)
    {
        firstState = s;
    }
    
    //This returns the initial state of the car.
    public State2 getInitial()
    {
        return firstState;
    }
    
    //The main constructor that is passes the string of the file name.
    public Main(String x)
    {
        read(x);
        arr = new Object[Max_X][Max_Y];
        tempRaceway = new char[Max_X][Max_Y];
        for(int i = 0; i < Max_X; i++)
        {
            for(int j = 0; j < Max_Y; j++)
            {
                System.out.print(Raceway[i][j]);
            }
            System.out.println();
        }
        crashes = 0;
        valueIteration = new ValueIteration2(this, .6);

        Utility = valueIteration.go();

        states = valueIteration.states;
        mdp = new MDP(states, getActions()); //creates a new MDP type
        setInitial(car.currentState);
        
        //This creates a temporary racetrack to reset the original when needed.
        for(int i=0; i<Max_X; i++)
            for(int j = 0; j < Max_Y; j++)
                tempRaceway[i][j] = Raceway[i][j];

        //This loops for ten times through the same racetrack
        while(timesAround < 10)
        {
            if(null == save.get(car.previousState) || save.get(car.previousState) != car.save.get(car.previousState))
                save.put(car.previousState, car.save.get(car.previousState));
            resetRaceway();
            go();
            timesAround++;
            System.out.println("Start: " + x + " Racetrack" + "Trial: " + timesAround);
        }
    }
    
    //This resets the whole system to the original state.
    public void resetRaceway()
    {
        for(int i=0; i<Max_X; i++)
            for(int j = 0; j < Max_Y; j++)
                Raceway[i][j] =  tempRaceway[i][j];
        car.currentState = getInitial();
        trial = 0;
    }
    
    public HashMap<State2, Double> getUtility()
    {
        return Utility;
    }
    
    public void setUtility(HashMap<State2, Double> Util)
    {
        Utility.putAll(Util);
    }
    
    public void printRaceway()
    {
        for(int i = 0; i < Max_X; i++)
        {
            for(int j = 0; j < Max_Y; j++)
                System.out.print(Raceway[i][j]);
            System.out.println();
        }
    }
    
    public void reset()
    {
        
        for(int i=0; i<Max_X; i++)
            for(int j = 0; j < Max_Y; j++)
                Raceway[i][j] =  tempRaceway[i][j];
    }
    
    //This runs through all of the decision processes and has the car move.
    public void go()
    {
        policy = new Policy(mdp, Utility, .2, this);
        int count = 0;
        while(Raceway[car.currentState.xLocation][car.currentState.yLocation] != 'F')
        {
            count++;
            decision = policy.go();
            if(count > growingTime)
            {
                Utility = valueIteration.iterate(mdp, .6);
                count = 0;
            }
            
            double prev = Utility.get(car.currentState);
            reset();
            State2 previous = car.currentState;
            Actions act = car.makeChoice(Utility, Reward);
            
            //System.out.println("Utility at " + car.currentState.print() + " is " + Utility.get(car.currentState));
            if(Raceway[car.currentState.xLocation][car.currentState.yLocation] != '#')
            {
                if(Raceway[car.currentState.xLocation][car.currentState.yLocation] == 'F' || checkCondition(previous, car.currentState))
                {
                    System.out.println("Win! The number of actions taken is: " + trial + " and the number of crashes for this round is: " + crashes);
                    Raceway[car.currentState.xLocation][car.currentState.yLocation] = 'C';
                    break;
                }
                else
                {
                    Raceway[car.currentState.xLocation][car.currentState.yLocation] = 'C';
                    if(Utility.get(car.currentState) > prev)
                        decision.put(car.currentState, car.action);
                    double add = Utility.get(car.currentState) + .8*Reward.get(car.currentState);
                    Utility.put(car.currentState, add);
                }
                
            }
        }
        //System.out.println("Win!");
        Raceway[car.currentState.xLocation][car.currentState.yLocation] = 'C';
    }
    
    //Checks to make sure the transition from one state to another is allowed
    public boolean checkCondition(State2 previous, State2 current)
    {
        int xChange = current.xLocation - previous.xLocation;
        int yChange = current.yLocation - previous.yLocation;
        char xDir = ' ';
        char yDir = ' ';
        if(xChange < 0)
            xDir = 'L';
        if(xChange > 0)
            xDir = 'R';
        if(yChange < 0)
            yDir = 'U';
        if(yChange < 0)
            yDir = 'D';
        
        if(yDir == 'U' && xDir == ' ')
        {
            for(int i = current.yVel; i > 0; i++)
            {
                if((previous.yLocation+i > -1) && (previous.yLocation+i < Max_Y) && Raceway[current.xLocation][previous.yLocation + i] == 'F')
                {
                    return true;
                }
            }
        }
        else if(yDir == 'D' && xDir == ' ')
        {
            for(int i = current.yVel; i < 0; i--)
            {
                if((previous.yLocation+i < Max_Y) && (previous.yLocation+i > -1) && Raceway[current.xLocation][previous.yLocation + i] == 'F')
                {
                    return true;
                }
            }
        }
        
        else if(xDir == 'L' && yDir == ' ')
        {
            for(int i = current.xVel; i > 0; i++)
            {
                if((previous.xLocation+i > -1) && Raceway[current.xLocation+i][previous.yLocation] == 'F')
                {
                    return true;
                }
            }
        }
        else if(xDir == 'R' && yDir == ' ')
        {
            for(int i = current.xVel; i < 0; i--)
            {
                if((previous.xLocation+i < Max_X) && Raceway[current.xLocation+i][previous.yLocation] == 'F')
                {
                    return true;
                }
            }
        }
        
        else if(yDir == 'U' && xDir == 'L')
        {
            for(int i = current.yVel; i > 0; i++)
            {
                for(int j = current.xVel; j > 0; j++)
                {
                    if((previous.yLocation+i > -1) && previous.xLocation+j > -1 && Raceway[current.xLocation+j][previous.yLocation + i] == 'F')
                    {
                        return true;
                    }
                }
                
            }
        }
        
        else if(yDir == 'D' && xDir == 'R')
        {
            for(int i = current.yVel; i < 0; i--)
            {
                 for(int j = current.xVel; j<0; j--)
                 {
                    if((previous.yLocation+i < Max_Y) && previous.xLocation+j < Max_X && Raceway[current.xLocation+j][previous.yLocation + i] == 'F')
                    {
                        return true;
                    }
                 }
            }
        }
        
        else if(yDir == 'U' && xDir == 'R')
        {
            for(int i = current.yVel; i > 0; i++)
            {
                for(int j = current.xVel; j < 0; j--)
                {
                    if((previous.yLocation+i > -1) && previous.xLocation+j < Max_X && Raceway[current.xLocation+j][previous.yLocation + i] == 'F')
                    {
                        return true;
                    }
                }
                
            }
        }
        
        else if(yDir == 'D' && xDir == 'L')
        {
            for(int i = current.yVel; i < 0; i--)
            {
                 for(int j = current.xVel; j>0; j++)
                 {
                    if((previous.yLocation+i < Max_Y) && previous.xLocation+j > -1 && Raceway[current.xLocation+j][previous.yLocation + i] == 'F')
                    {
                        return true;
                    }
                 }
            }
        }
        return false;
        
    }
    
    //This checks for the closest open place to the crash site
    public State2 crashSite(State2 previous, State2 crash)
    {
        State2 place = new State2();
        for(int i = -1; i < 2; i++)
        {
            if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && Raceway[crash.xLocation + i][crash.yLocation] != '#')
            {
                place = new State2(crash.xLocation + i, crash.yLocation, 0, 0, this);
                return place;
            }
            if(crash.yLocation + i > -1 && crash.yLocation + i < Max_Y && Raceway[crash.xLocation][crash.yLocation+i] != '#')
            {
                place = new State2(crash.xLocation, crash.yLocation+i, 0, 0, this);
                return place;
            }
            for(int j = -1; j < 2; j++)
            {
                if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && crash.yLocation + j > -1 && crash.yLocation + j < Max_Y && Raceway[crash.xLocation + i][crash.yLocation+j] != '#')
                {
                    place = new State2(crash.xLocation+i, crash.yLocation+j, 0, 0, this);
                    return place;
                }
            }
        }
        for(int i = -2; i < 3; i++)
        {
            if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && Raceway[crash.xLocation + i][crash.yLocation] != '#')
            {
                place = new State2(crash.xLocation + i, crash.yLocation, 0, 0, this);
                return place;
            } else if(crash.yLocation + i > -1 && crash.yLocation + i < Max_Y && Raceway[crash.xLocation][crash.yLocation+i] != '#')
            {
                place = new State2(crash.xLocation, crash.yLocation+i, 0, 0, this);
                return place;
            }
            for(int j = -2; j < 3; j++)
            {
                if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && crash.yLocation + j > -1 && crash.yLocation + j < Max_Y && Raceway[crash.xLocation + i][crash.yLocation+j] != '#')
                {
                    place = new State2(crash.xLocation+i, crash.yLocation+j, 0, 0, this);
                    return place;
                }
            }
        }
        for(int i = -3; i < 4; i++)
        {
            if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && Raceway[crash.xLocation + i][crash.yLocation] != '#')
            {
                place = new State2(crash.xLocation + i, crash.yLocation, 0, 0, this);
                return place;
            } else if(crash.yLocation + i > -1 && crash.yLocation + i < Max_Y && Raceway[crash.xLocation][crash.yLocation+i] != '#')
            {
                place = new State2(crash.xLocation, crash.yLocation+i, 0, 0, this);
                return place;
            }
            for(int j = -3; j < 4; j++)
            {
                if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && crash.yLocation + j > -1 && crash.yLocation + j < Max_Y && Raceway[crash.xLocation + i][crash.yLocation+j] != '#')
                {
                    place = new State2(crash.xLocation+i, crash.yLocation+j, 0, 0, this);
                    return place;
                }
            }
        }
        for(int i = -4; i < 5; i++)
        {
            if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && Raceway[crash.xLocation + i][crash.yLocation] != '#')
            {
                place = new State2(crash.xLocation + i, crash.yLocation, 0, 0, this);
                return place;
            } else if(crash.yLocation + i > -1 && crash.yLocation + i < Max_Y && Raceway[crash.xLocation][crash.yLocation+i] != '#')
            {
                place = new State2(crash.xLocation, crash.yLocation+i, 0, 0, this);
                return place;
            }
            for(int j = -4; j < 5; j++)
            {
                if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && crash.yLocation + j > -1 && crash.yLocation + j < Max_Y && Raceway[crash.xLocation + i][crash.yLocation+j] != '#')
                {
                    place = new State2(crash.xLocation+i, crash.yLocation+j, 0, 0, this);
                    return place;
                }
            }
        }
        for(int i = -5; i < 6; i++)
        {
            if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && Raceway[crash.xLocation + i][crash.yLocation] != '#')
            {
                place = new State2(crash.xLocation + i, crash.yLocation, 0, 0, this);
                return place;
            } else if(crash.yLocation + i > -1 && crash.yLocation + i < Max_Y && Raceway[crash.xLocation][crash.yLocation+i] != '#')
            {
                place = new State2(crash.xLocation, crash.yLocation+i, 0, 0, this);
                return place;
            }
            for(int j = -5; j < 6; j++)
            {
                if(crash.xLocation + i > -1 && crash.xLocation + i < Max_X && crash.yLocation + j > -1 && crash.yLocation + j < Max_Y && Raceway[crash.xLocation + i][crash.yLocation+j] != '#')
                {
                    place = new State2(crash.xLocation+i, crash.yLocation+j, 0, 0, this);
                    return place;
                }
            }
        }
        
        return previous;
    }
    
    //This converts states to strings and checks to see if they're legit states
    public State2 findState(State2 next, HashMap<State2, Double> Utility)
    {
        String key = Integer.toString(next.xLocation)+ Integer.toString(next.yLocation) + Integer.toString(next.xVel) + Integer.toString(next.yVel);
        try{
            int x = keyLookup.get(key).xLocation;
        }catch(Exception e)
        {
            return new State2(9,9,9,9, this);
        }
                
        return keyLookup.get(key);
    }
    
    //Reads in the specified document and puts everything into the matrix called "Raceway"
    public void read(String fName)
    {
        String in = "";
        int i = 0;
        try {
            Scanner sc = new Scanner(new File(fName));
            String first = sc.nextLine();
            String[] f = new String[2];
            f = first.split(",");
            Max_X = Integer.parseInt(f[0]);
            System.out.println(Max_X);
            Max_Y = Integer.parseInt(f[1]);
            System.out.println(Max_Y);
            Raceway = new char[Max_X][Max_Y];
            
            while (sc.hasNextLine()) {
                in = sc.nextLine();
                
                    for(int j = 0; j < in.length(); j++)
                    {
                        Raceway[i][j] = in.charAt(j);
                    }
                i++;
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        }
    }
    
    public void printUtil()
    {
        for(State2 s: states)
        {
            System.out.println("At state: " + s.print() + " with Utility: " + Utility.get(s));
        }
    }
    
    //Returns a list of actions.
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
}
