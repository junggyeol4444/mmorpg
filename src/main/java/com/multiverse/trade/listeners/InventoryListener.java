package com.multiverse.trade.listeners;

import com. multiverse.trade. TradeCore;
import com.multiverse. trade.gui.*;
import com.multiverse.trade.managers.TradeManager;
import com. multiverse.trade. models.Trade;
import com. multiverse.trade. models.TradeStatus;
import com. multiverse.trade. utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org. bukkit.event. Listener;
import org.bukkit. event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit. event.inventory. InventoryDragEvent;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    private final TradeCore plugin;
    private final TradeManager tradeManager;
    private final GUIManager guiManager;

    public InventoryListener(TradeCore plugin) {
        this. plugin = plugin;
        this.tradeManager = plugin.getTradeManager();
        this.guiManager = plugin.getGuiManager();
    }

    @EventHandler(priority = EventPriority. HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder == null) {
            return;
        }

        if (holder instanceof TradeGUI) {
            handleTradeGUIClick(event, player, (TradeGUI) holder);
        } else if (holder instanceof ShopGUI) {
            handleShopGUIClick(event, player, (ShopGUI) holder);
        } else if (holder instanceof ShopManageGUI) {
            handleShopManageGUIClick(event, player, (ShopManageGUI) holder);
        } else if (holder instanceof ShopListGUI) {
            handleShopListGUIClick(event, player, (ShopListGUI) holder);
        } else if (holder instanceof AuctionGUI) {
            handleAuctionGUIClick(event, player, (AuctionGUI) holder);
        } else if (holder instanceof AuctionCreateGUI) {
            handleAuctionCreateGUIClick(event, player, (AuctionCreateGUI) holder);
        } else if (holder instanceof MarketGUI) {
            handleMarketGUIClick(event, player, (MarketGUI) holder);
        } else if (holder instanceof MarketOrderGUI) {
            handleMarketOrderGUIClick(event, player, (MarketOrderGUI) holder);
        } else if (holder instanceof MailGUI) {
            handleMailGUIClick(event, player, (MailGUI) holder);
        } else if (holder instanceof MailComposeGUI) {
            handleMailComposeGUIClick(event, player, (MailComposeGUI) holder);
        }
    }

    private void handleTradeGUIClick(InventoryClickEvent event, Player player, TradeGUI gui) {
        int slot = event.getRawSlot();
        Trade trade = gui.getTrade();

        if (trade == null || trade.getStatus() != TradeStatus. ACTIVE) {
            event.setCancelled(true);
            player.closeInventory();
            return;
        }

        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        boolean isPlayer1 = trade.getPlayer1().equals(player.getUniqueId());

        if (slot >= 0 && slot <= 3 || slot >= 9 && slot <= 12 || 
            slot >= 18 && slot <= 21 || slot >= 27 && slot <= 30) {
            if (isPlayer1) {
                return;
            } else {
                event.setCancelled(true);
            }
        }

        if (slot >= 5 && slot <= 8 || slot >= 14 && slot <= 17 ||
            slot >= 23 && slot <= 26 || slot >= 32 && slot <= 35) {
            if (! isPlayer1) {
                return;
            } else {
                event. setCancelled(true);
            }
        }

        if (slot == 4 || slot == 13 || slot == 22 || slot == 31) {
            event.setCancelled(true);
        }

        if (slot == 36) {
            event.setCancelled(true);
            boolean currentReady = isPlayer1 ? trade.isPlayer1Ready() : trade.isPlayer2Ready();
            tradeManager.setReady(trade, player, ! currentReady);
            guiManager.updateTradeGUI(player, trade);
        }

        if (slot == 44) {
            event.setCancelled(true);
            tradeManager.cancelTrade(trade);
        }

        if (slot == 40) {
            event.setCancelled(true);
            player.sendMessage(MessageUtil. color("&e채팅창에 금액을 입력하세요.  (취소:  cancel)"));
            guiManager.startMoneyInput(player, trade);
        }
    }

    private void handleShopGUIClick(InventoryClickEvent event, Player player, ShopGUI gui) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 45) {
            return;
        }

        if (slot < 27) {
            gui.handleItemClick(player, slot, event.isShiftClick());
        } else if (slot == 45) {
            gui.previousPage();
        } else if (slot == 53) {
            gui.nextPage();
        }
    }

    private void handleShopManageGUIClick(InventoryClickEvent event, Player player, ShopManageGUI gui) {
        int slot = event.getRawSlot();

        if (slot >= 54) {
            return;
        }

        if (slot < 27) {
            if (event.getCursor() != null && ! event.getCursor().getType().isAir()) {
                gui.handleAddItem(player, slot, event.getCursor().clone());
                event.setCursor(null);
                event.setCancelled(true);
            } else if (event.getCurrentItem() != null && !event.getCurrentItem().getType().isAir()) {
                gui.handleRemoveItem(player, slot);
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);

            if (slot == 45) {
                gui.previousPage();
            } else if (slot == 53) {
                gui.nextPage();
            } else if (slot == 49) {
                gui.toggleShopStatus();
            }
        }
    }

    private void handleShopListGUIClick(InventoryClickEvent event, Player player, ShopListGUI gui) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 54) {
            return;
        }

        if (slot < 45) {
            gui.handleShopClick(player, slot);
        } else if (slot == 45) {
            gui.previousPage();
        } else if (slot == 53) {
            gui. nextPage();
        }
    }

    private void handleAuctionGUIClick(InventoryClickEvent event, Player player, AuctionGUI gui) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 54) {
            return;
        }

        if (slot < 45) {
            gui.handleAuctionClick(player, slot, event.isShiftClick(), event.isRightClick());
        } else if (slot == 45) {
            gui.previousPage();
        } else if (slot == 47) {
            gui.sortByPrice();
        } else if (slot == 49) {
            gui.sortByTime();
        } else if (slot == 51) {
            gui.openSearch(player);
        } else if (slot == 53) {
            gui.nextPage();
        }
    }

    private void handleAuctionCreateGUIClick(InventoryClickEvent event, Player player, AuctionCreateGUI gui) {
        int slot = event.getRawSlot();

        if (slot == 13) {
            return;
        }

        event.setCancelled(true);

        if (slot == 29) {
            gui.setStartingPrice(player);
        } else if (slot == 31) {
            gui.setBuyoutPrice(player);
        } else if (slot == 33) {
            gui.setDuration(player);
        } else if (slot == 49) {
            gui.confirmCreate(player);
        }
    }

    private void handleMarketGUIClick(InventoryClickEvent event, Player player, MarketGUI gui) {
        event. setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 54) {
            return;
        }

        if (slot < 45) {
            gui.handleOrderClick(player, slot, event.isShiftClick());
        } else if (slot == 45) {
            gui.previousPage();
        } else if (slot == 47) {
            gui.showSellOrders();
        } else if (slot == 49) {
            gui.showBuyOrders();
        } else if (slot == 51) {
            gui. openSearch(player);
        } else if (slot == 53) {
            gui.nextPage();
        }
    }

    private void handleMarketOrderGUIClick(InventoryClickEvent event, Player player, MarketOrderGUI gui) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        gui.handleClick(player, slot);
    }

    private void handleMailGUIClick(InventoryClickEvent event, Player player, MailGUI gui) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 54) {
            return;
        }

        if (slot < 45) {
            gui.handleMailClick(player, slot, event.isShiftClick());
        } else if (slot == 45) {
            gui.previousPage();
        } else if (slot == 47) {
            gui.showInbox();
        } else if (slot == 49) {
            gui.showSent();
        } else if (slot == 51) {
            gui.claimAll(player);
        } else if (slot == 53) {
            gui.nextPage();
        }
    }

    private void handleMailComposeGUIClick(InventoryClickEvent event, Player player, MailComposeGUI gui) {
        int slot = event.getRawSlot();

        if (slot >= 0 && slot <= 4) {
            return;
        }

        event.setCancelled(true);

        if (slot == 20) {
            gui.setRecipient(player);
        } else if (slot == 22) {
            gui.setSubject(player);
        } else if (slot == 24) {
            gui.setMoney(player);
        } else if (slot == 40) {
            gui.sendMail(player);
        } else if (slot == 44) {
            gui.cancel(player);
        }
    }

    @EventHandler(priority = EventPriority. HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event. getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof TradeGUI || holder instanceof ShopGUI || 
            holder instanceof AuctionGUI || holder instanceof MarketGUI ||
            holder instanceof MailGUI) {
            
            for (int slot :  event.getRawSlots()) {
                if (slot < inventory.getSize()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof TradeGUI) {
            Trade trade = ((TradeGUI) holder).getTrade();
            if (trade != null && trade. getStatus() == TradeStatus.ACTIVE) {
            }
        }

        if (holder instanceof ShopManageGUI) {
            ((ShopManageGUI) holder).saveChanges();
        }

        if (holder instanceof AuctionCreateGUI) {
            ((AuctionCreateGUI) holder).returnItem(player);
        }

        if (holder instanceof MailComposeGUI) {
            ((MailComposeGUI) holder).returnItems(player);
        }

        guiManager.removePlayerGUI(player);
    }
}