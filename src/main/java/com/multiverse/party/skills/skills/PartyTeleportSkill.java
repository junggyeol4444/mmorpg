package com.multiverse.party.skills.skills;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartySkill;
import org.bukkit.entity.Player;
import org.bukkit.Location;

/**
 * 파티 텔레포트 스킬 상세 구현
 */
public class PartyTeleportSkill {

    public boolean execute(Party party, Player caster, PartySkill skill) {
        if (party == null || caster == null || skill == null) return false;

        Location targetLocation = caster.getLocation();
        int teleported = 0;
        for (java.util.UUID memberUUID : party.getMembers()) {
            if (memberUUID.equals(caster.getUniqueId())) continue;
            Player member = caster.getServer().getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.teleport(targetLocation);
                teleported++;
            }
        }
        caster.sendMessage("파티원 " + teleported + "명 텔레포트 완료!");
        return teleported > 0;
    }
}