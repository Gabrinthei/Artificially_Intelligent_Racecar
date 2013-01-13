/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Racer;

/**
 *
 * @author James Sonntag
 */
public class Percept {
    public State2 state;
    public double reward;
    
    public Percept(State2 state, double reward)
    {
        this.state = state;
        this.reward = reward;
    }
}
