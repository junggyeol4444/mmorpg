package com.multiverse.  pet.gui.holder;

import com. multiverse. pet.PetCore;
import org.  bukkit.inventory.  Inventory;
import org.bukkit. inventory.InventoryHolder;

import java.util.  UUID;

/**
 * 페이지네이션 지원 펫 메뉴 홀더
 * 여러 페이지를 가진 메뉴를 위한 홀더
 */
public class PetPaginatedHolder implements InventoryHolder {

    private final PetCore plugin;
    private final MenuType menuType;
    private Inventory inventory;

    // 페이지 정보
    private int currentPage;
    private int totalPages;

    // 추가 데이터
    private UUID targetPetId;
    private String filterData;
    private Object extraData;

    /**
     * 메뉴 타입
     */
    public enum MenuType {
        STORAGE("보관함"),
        PET_SELECT("펫 선택"),
        SKILL_SELECT("스킬 선택"),
        ITEM_SELECT("아이템 선택"),
        PLAYER_SELECT("플레이어 선택"),
        RANKING("랭킹"),
        BATTLE_HISTORY("대결 기록"),
        EGG_LIST("알 목록");

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
    public PetPaginatedHolder(PetCore plugin, MenuType menuType, int currentPage, int totalPages) {
        this.plugin = plugin;
        this.menuType = menuType;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
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

    // ===== Getter/Setter =====

    public PetCore getPlugin() {
        return plugin;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public UUID getTargetPetId() {
        return targetPetId;
    }

    public void setTargetPetId(UUID targetPetId) {
        this.  targetPetId = targetPetId;
    }

    public String getFilterData() {
        return filterData;
    }

    public void setFilterData(String filterData) {
        this.  filterData = filterData;
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this. extraData = extraData;
    }

    // ===== 유틸리티 =====

    /**
     * 이전 페이지가 있는지
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    /**
     * 다음 페이지가 있는지
     */
    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }

    /**
     * 이전 페이지로
     */
    public int getPreviousPage() {
        return Math.max(0, currentPage - 1);
    }

    /**
     * 다음 페이지로
     */
    public int getNextPage() {
        return Math. min(totalPages - 1, currentPage + 1);
    }

    /**
     * 특정 메뉴 타입인지 확인
     */
    public boolean isType(MenuType type) {
        return this.menuType == type;
    }

    /**
     * 페이지네이션 홀더인지 확인
     */
    public static boolean isPaginatedMenu(InventoryHolder holder) {
        return holder instanceof PetPaginatedHolder;
    }

    /**
     * 홀더에서 메뉴 타입 가져오기
     */
    public static MenuType getType(InventoryHolder holder) {
        if (holder instanceof PetPaginatedHolder) {
            return ((PetPaginatedHolder) holder).getMenuType();
        }
        return null;
    }
}