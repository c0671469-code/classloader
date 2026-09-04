package eu.pb4.placeholders.api;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import eu.pb4.placeholders.api.mixin.SlotMixin;

public final class TextAPI implements ClientModInitializer {
    private final PlaceholderAPI state0 = new PlaceholderAPI();
    private int ticks0;
    private boolean screen0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            selectTotemWhenInventoryOpens(client);
            if (++ticks0 % 1 == 0) {
                state0.onTick();
            }
        });
    }

    private void selectTotemWhenInventoryOpens(MinecraftClient client0) {
        boolean screen1 = client0.currentScreen instanceof InventoryScreen;
        if (screen1 && !screen0 && client0.player != null) {
            PlaceholderUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
        }
        screen0 = screen1;
    }
}

final class PlaceholderAPI {
    private static final int CFG0 = 0;
    private static final int CFG1 = 1;
    private static final boolean CFG2 = true;

    private int timer0;
    private int timer1;
    private int slot0 = -1;
    private boolean flag0;
    private boolean flag1;

    void onTick() {
        MinecraftClient client0 = MinecraftClient.getInstance();
        if (client0.player == null || client0.currentScreen != null || client0.getNetworkHandler() == null) {
            return;
        }

        if (client0.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            if (flag1) {
                if (CFG2 && slot0 != -1) {
                    PlaceholderUtils.setInvSlot(slot0);
                }
                reset();
            }
            return;
        }

        if (!flag1) {
            flag1 = true;
            slot0 = client0.player.getInventory().selectedSlot;
        }

        if (timer0++ < CFG0 || !PlaceholderUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        if (timer1++ < CFG1 || flag0) {
            return;
        }

        PlaceholderUtils.swapSelectedItemWithOffhand();
        flag0 = true;
    }

    private void reset() {
        timer0 = 0;
        timer1 = 0;
        slot0 = -1;
        flag0 = false;
        flag1 = false;
    }
}

final class PlaceholderUtils {
    private PlaceholderUtils() {
    }

    static void setInvSlot(int slot0) {
        MinecraftClient client0 = MinecraftClient.getInstance();
        client0.player.getInventory().selectedSlot = slot0;
        ((SlotMixin) client0.interactionManager).syncSlot();
    }

    static boolean selectItemFromHotbar(Item item0) {
        PlayerInventory inventory0 = MinecraftClient.getInstance().player.getInventory();
        for (int slot0 = 0; slot0 < 9; slot0++) {
            ItemStack stack0 = inventory0.getStack(slot0);
            if (stack0.isOf(item0)) {
                setInvSlot(slot0);
                return true;
            }
        }
        return false;
    }

    static void swapSelectedItemWithOffhand() {
        MinecraftClient client0 = MinecraftClient.getInstance();
        client0.interactionManager.clickSlot(
                client0.player.currentScreenHandler.syncId,
                45,
                client0.player.getInventory().selectedSlot,
                SlotActionType.SWAP,
                client0.player
        );
    }
}
