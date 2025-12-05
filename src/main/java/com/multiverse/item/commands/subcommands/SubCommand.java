package com.multiverse.item. commands. subcommands;

import org.bukkit.entity.Player;

public interface SubCommand {
    
    /**
     * 서브커맨드 실행
     * 
     * @param player 실행한 플레이어
     * @param args 커맨드 인자
     */
    void execute(Player player, String[] args);
}