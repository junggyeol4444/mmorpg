package com.multiverse.npcai.gui;

import com.multiverse.npcai.models.enums.ShopType;
import com.multiverse.npcai.models.enums.TransactionType;
import java.util.List;

/**
 * NPC 상점 인터페이스를 관리하는 GUI 클래스
 */
public class ShopGUI {
    private String shopName;
    private ShopType shopType;
    private List<String> itemList;
    private List<TransactionType> availableTransactions;

    public ShopGUI(String shopName, ShopType shopType, List<String> itemList, List<TransactionType> availableTransactions) {
        this.shopName = shopName;
        this.shopType = shopType;
        this.itemList = itemList;
        this.availableTransactions = availableTransactions;
    }

    public String getShopName() {
        return shopName;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public List<String> getItemList() {
        return itemList;
    }

    public List<TransactionType> getAvailableTransactions() {
        return availableTransactions;
    }

    // 상점 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displayShop() {
        System.out.println("[" + shopName + "] 상점 (" + shopType + ")");
        for (String item : itemList) {
            System.out.println("- " + item);
        }
    }
}