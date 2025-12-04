package com.multiverse.dungeon.gui;

import org.bukkit. Bukkit;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory. InventoryHolder;
import org. bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * 던전 GUI 추상 클래스
 */
public abstract class AbstractDungeonGUI implements InventoryHolder {

    protected final int INVENTORY_SIZE = 54;
    protected Inventory inventory;
    protected Player player;

    /**
     * 생성자
     *
     * @param player 플레이어
     * @param title GUI 제목
     */
    public AbstractDungeonGUI(Player player, String title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, title);
    }

    /**
     * GUI 초기화
     */
    public abstract void initialize();

    /**
     * 아이템 클릭 처리
     *
     * @param event 클릭 이벤트
     */
    public abstract void handleClick(InventoryClickEvent event);

    /**
     * GUI 열기
     */
    public void open() {
        if (player != null && player.isOnline()) {
            initialize();
            player.openInventory(inventory);
        }
    }

    /**
     * GUI 닫기
     */
    public void close() {
        if (player != null && player. isOnline()) {
            player.closeInventory();
        }
    }

    /**
     * 인벤토리 가져오기
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 배경 채우기 (회색 판)
     */
    protected void fillBackground() {
        var filler = new org.bukkit.inventory.ItemStack(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
        var meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r");
            filler.setItemMeta(meta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    /**
     * 보더 채우기 (검은색 판)
     */
    protected void fillBorder() {
        var border = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLACK_STAINED_GLASS_PANE);
        var meta = border.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r");
            border.setItemMeta(meta);
        }

        // 위쪽 줄
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
        }

        // 아래쪽 줄
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, border);
        }

        // 왼쪽과 오른쪽
        for (int i = 0; i < 54; i += 9) {
            inventory. setItem(i, border);
            inventory.setItem(i + 8, border);
        }
    }

    /**
     * 중앙 영역 채우기
     *
     * @param rows 행 수
     */
    protected void fillCenter(int rows) {
        var filler = new org.bukkit. inventory.ItemStack(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
        var meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r");
            filler. setItemMeta(meta);
        }

        int startRow = 1;
        int endRow = startRow + rows;

        for (int row = startRow; row < endRow; row++) {
            for (int col = 1; col < 8; col++) {
                int slot = row * 9 + col;
                if (inventory.getItem(slot) == null) {
                    inventory.setItem(slot, filler);
                }
            }
        }
    }

    /**
     * 슬롯에 아이템 설정
     *
     * @param slot 슬롯 번호
     * @param item 아이템
     */
    protected void setItem(int slot, org.bukkit.inventory.ItemStack item) {
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
    }

    /**
     * 이전 페이지 버튼 추가
     *
     * @param page 현재 페이지
     */
    protected void addPreviousButton(int page) {
        if (page > 0) {
            var item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            var meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e이전 페이지");
                item. setItemMeta(meta);
            }
            setItem(48, item);
        }
    }

    /**
     * 다음 페이지 버튼 추가
     *
     * @param page 현재 페이지
     * @param maxPage 최대 페이지
     */
    protected void addNextButton(int page, int maxPage) {
        if (page < maxPage) {
            var item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            var meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e다음 페이지");
                item.setItemMeta(meta);
            }
            setItem(50, item);
        }
    }

    /**
     * 닫기 버튼 추가
     */
    protected void addCloseButton() {
        var item = new org.bukkit.inventory.ItemStack(org. bukkit.Material.BARRIER);
        var meta = item. getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c닫기");
            item.setItemMeta(meta);
        }
        setItem(49, item);
    }
}