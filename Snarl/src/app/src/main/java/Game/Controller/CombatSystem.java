package Game.Controller;

import Game.Model.Actor;

public class CombatSystem {

    private static CombatSystem instance;

    public CombatSystem() {
    }

    /**
     *  Singleton combat system that  provides an instance of the system
     * @return
     */
    public static CombatSystem getInstance() {
        if (instance == null) {
            instance = new CombatSystem();
        }
        return instance;
    }

    /**
     * Duel conducts a duel between a player and adversary
     * Also sets the hitpoints after the battle is over
     * @param player - Actor representing player
     * @param adversary - Actor representing adversary
     */
    public void duel(Actor player, Actor adversary) {
        setHp(adversary, getDamageFrom(player, adversary.getDefense()));

        if (adversary.getHitPoints() != 0) {
            setHp(player, getDamageFrom(adversary, player.getDefense()));
        }
    }

    /**
     * Calculates damage from an actor and finds the damage dealt based on opponenet defense
     * @param actor - Actor who is dealing the damage
     * @param opponentDefense - the defense stat of the opponent
     * @return the raw damage value of the actor against opp defense
     */
    private int getDamageFrom(Actor actor, int opponentDefense) {
        return Math.max(0, (actor.getAttack() * actor.getModifier()) - opponentDefense);
    }

    /**
     * Sets the hitpoints for an actor after dealing opponent damage.
     * Checks to make sure hp cannot be negative
     * @param actor - actor whose hp is to be set
     * @param opponentDamage - damage dealt to the actor
     */
    private void setHp(Actor actor, int opponentDamage) {
        actor.setHitPoints(Math.max(0, actor.getHitPoints() - opponentDamage));
    }
}
