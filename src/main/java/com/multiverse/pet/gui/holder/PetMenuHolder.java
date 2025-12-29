package com.multiverse.pet. gui. holder;

import com.multiverse.  pet.PetCore;
import org.bukkit.inventory.  Inventory;
import org. bukkit.  inventory.InventoryHolder;

/**
 * 펫 메뉴 홀더
 * 인벤토리 타입을 식별하기 위한 홀더
 */
public class PetMenuHolder implements InventoryHolder {

    private final PetCore plugin;
    private final MenuType menuType;
    private Inventory inventory;

    /**
     * 메뉴 타입 enum
     */
    public enum MenuType {
        MAIN_MENU("메인 메뉴"),
        STORAGE("보관함"),
        PET_INFO("펫 정보"),
        SKILL("스킬"),
        EVOLUTION("진화"),
        EQUIPMENT("장비"),
        BREEDING("교배"),
        BATTLE("대결"),
        CARE("케어"),
        RANKING("랭킹"),
        SETTINGS("설정"),
        EGG("알"),
        FILTER("필터"),
        CONFIRM("확인"),
        PET_SELECT("펫 선택"),
        ITEM_SELECT("아이템 선택"),
        SKILL_LEARN("스킬 배우기"),
        EQUIPMENT_SELECT("장비 선택"),
        BATTLE_PLAYER_SELECT("대결 상대 선택"),
        BREEDING_PET_SELECT("교배 펫 선택");

        private final String displayName;

        MenuType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 생성자
     */
    public PetMenuHolder(PetCore plugin, MenuType menuType) {
        this.plugin = plugin;
        this.menuType = menuType;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 인벤토리 설정
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * 플러그인 가져오기
     */
    public PetCore getPlugin() {
        return plugin;
    }

    /**
     * 메뉴 타입 가져오기
     */
    public MenuType getMenuType() {
        return menuType;
    }

    /**
     * 특정 메뉴 타입인지 확인
     */
    public boolean isType(MenuType type) {
        return this.menuType == type;
    }

    /**
     * 펫 메뉴 홀더인지 확인
     */
    public static boolean isPetMenu(InventoryHolder holder) {
        return holder instanceof PetMenuHolder;
    }

    /**
     * 홀더에서 메뉴 타입 가져오기
     */
    public static MenuType getType(InventoryHolder holder) {
        if (holder instanceof PetMenuHolder) {
            return ((PetMenuHolder) holder).getMenuType();
        }
        return null;
    }
}