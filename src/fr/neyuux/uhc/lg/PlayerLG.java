package fr.neyuux.uhc.lg;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.entity.Player;

public class PlayerLG extends PlayerUHC {

    public PlayerLG(Player player, UHC main) {
        super(player, main);
    }

    private Roles role;
    private Camp camp;
    private Aura aura;
    private PlayerLG couple;


    public Roles getRole() {
        return role;
    }

    public Camp getCamp() {
        return camp;
    }

    public Aura getAura() {
        return aura;
    }

    public PlayerLG getCouple() {
        return couple;
    }
}
