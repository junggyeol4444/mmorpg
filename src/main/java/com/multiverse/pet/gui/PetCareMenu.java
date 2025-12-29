            player.closeInventory();
            return;
        }

        // 음식 슬롯
        int[] foodSlots = {27, 28, 29, 36, 37, 38};
        List<PetCareManager. FoodEffect> availableFoods = getAvailableFoods(player);

        for (int i = 0; i < foodSlots.length; i++) {
            if (foodSlots[i] == slot && i < availableFoods.  size()) {
                PetCareManager. FoodEffect food = availableFoods.get(i);
                if (hasFood(player, food.getItemId())) {
                    plugin.getPetCareManager().feedPet(player, pet, food.getItemId());
                    open(player, pet);
                }
                return;
            }
        }

        // 장난감 슬롯
        int[] toySlots = {33, 34, 35, 42, 43, 44};
        List<PetCareManager.ToyEffect> availableToys = getAvailableToys(player);

        for (int i = 0; i < toySlots. length; i++) {
            if (toySlots[i] == slot && i < availableToys. size()) {
                PetCareManager.  ToyEffect toy = availableToys. get(i);
                if (hasToy(player, toy.getItemId())) {
                    plugin.getPetCareManager().playWithPet(player, pet, toy.getItemId());
                    open(player, pet);
                }
                return;
            }
        }

        switch (slot) {
            case 22:  // 치료
                if (pet.  getHealth() < pet.getMaxHealth()) {
                    double healCost = (pet.getMaxHealth() - pet.getHealth()) * 2;
                    if (plugin.getPetCareManager().healPet(player, pet, pet.getMaxHealth() - pet.getHealth())) {
                        open(player, pet);
                    }
                }
                break;

            case 31: // 부활
                if (pet. getStatus() == PetStatus.FAINTED) {
                    double reviveCost = plugin.getConfigManager().getCareSettings().getReviveCost();
                    if (plugin.getPetCareManager().revivePet(player, pet, reviveCost)) {
                        open(player, pet);
                    }
                }
                break;

            case 40: // 케어 필요한 펫 목록
                plugin.getGUIManager().openPetsNeedingCareMenu(player);
                break;

            case 47: // 다른 펫 선택
                plugin.getGUIManager().openCarePetSelectMenu(player);
                break;

            case 49: // 전체 자동 케어
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    plugin.getPetCareManager().autoCaraAllPets(player);
                    open(player, pet);
                }
                break;

            case 45: // 뒤로가기
                plugin.getGUIManager().openMainMenu(player);
                break;

            case 53: // 새로고침
                pet = plugin.getPetManager().getPetById(playerId, petId);
                if (pet != null) {
                    open(player, pet);
                }
                break;
        }
    }

    /**
     * 정리
     */
    public void cleanup(UUID playerId) {
        viewingPet.remove(playerId);
    }
}