package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import org.bukkit.command.CommandExecutor;

import java.util.Arrays;

public class CommandManager {

    private final EconomyCore plugin;

    public CommandManager(EconomyCore plugin, Object configUtil, Object messageUtil) {
        this.plugin = plugin;
    }

    public CommandManager register(CommandExecutor... executors) {
        // 명령어 등록은 각 명령어 클래스 생성자에서 executor 세팅을 하므로, 별도의 동작 불필요, 체이닝용 반환
        Arrays.stream(executors).forEach(executor -> {
            // 각 명령어가 스스로 plugin.getCommand().setExecutor(this)를 호출
        });
        return this;
    }
}