package com.multiverse.party.skills.skills;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartySkill;
import org.bukkit.entity.Player;

/**
 * 파티 부활 스킬
 */
public class PartyReviveSkill {

    public boolean execute(Party party, Player caster, PartySkill skill) {
        if (party == null || caster == null || skill == null) return false;

        int revived = 0;
        for (java.util.UUID memberUUID : party.getMembers()) {
            Player member = caster.getServer().getPlayer(memberUUID);
            if (member != null && member.isOnline() && member.isDead()) {
                member.spigot().respawn();
                revived++;
            }
        }
        caster.sendMessage("파티원 " + revived + "명 부활 완료!");
        return revived > 0;
    }
}