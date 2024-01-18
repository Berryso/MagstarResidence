package top.magstar.residence.handlers;

import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.magstar.economy.objects.Currency;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.ResidentCreateManager;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.objects.Loc;
import top.magstar.residence.objects.ResidentFactory;
import top.magstar.residence.objects.PlayerFace;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.GeneralUtils;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.Message;

import java.util.Objects;

public final class ConversationHandlers {
    public final static class ResidentCreation extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext c, @NotNull String s) {
            Player p = (Player) c.getForWhom();
            if (s.equalsIgnoreCase("y")) {
                Currency coin = Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency")));
                int bal = CurrencyHandlers.getCurrency(p.getUniqueId(), coin);
                int price = GeneralUtils.getBuyPrice(((Player) c.getForWhom()), Objects.requireNonNull(ResidentCreateManager.getFirst(p)), Objects.requireNonNull(ResidentCreateManager.getSecond(p)));
                if (bal >= price) {
                    c.getForWhom().sendRawMessage(Message.accepted.getTranslate());
                    Resident resident = ResidentFactory.buildResident(
                            p.getUniqueId(),
                            Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getWorld(),
                            new Loc(ResidentCreateManager.getFirst(p).getBlockX(), ResidentCreateManager.getFirst(p).getBlockY(), ResidentCreateManager.getFirst(p).getBlockZ()),
                            new Loc(ResidentCreateManager.getSecond(p).getBlockX(), ResidentCreateManager.getSecond(p).getBlockY(), ResidentCreateManager.getSecond(p).getBlockZ()));
                    RuntimeDataManager.loadData(resident);
                    CurrencyHandlers.takeCurrency(p.getUniqueId(), coin, price);
                }
            } else {
                c.getForWhom().sendRawMessage(Message.denied.getTranslate());
            }
            ResidentCreateManager.removeLocation(p);
            ResidentCreateManager.removePlayer(p);
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            Player p = (Player) conversationContext.getForWhom();
            return Message.confirm.getTranslate()
                    .replace("{price}", String.valueOf(GeneralUtils.getBuyPrice(((Player) conversationContext.getForWhom()), Objects.requireNonNull(ResidentCreateManager.getFirst(p)), Objects.requireNonNull(ResidentCreateManager.getSecond(p)))))
                    .replace("{currency}", Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))).getName())
                    .replace("{balance}", String.valueOf(CurrencyHandlers.getCurrency(p.getUniqueId(), Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))))));
        }
    }
    public final static class ResidentExpand extends ValidatingPrompt {

        private final PlayerFace face;
        private final int count;
        private final Currency currency;
        private final int price;
        private final Player p;
        private final Resident resident;

        public ResidentExpand(PlayerFace face, int count, Currency currency, int price, Player p, Resident resident) {
            this.face = face;
            this.count = count;
            this.currency = currency;
            this.price = price;
            this.p = p;
            this.resident = resident;
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String s) {
            if (s.equalsIgnoreCase("y")) {
                Loc first = resident.getFirstLocation();
                Loc second = resident.getSecondLocation();
                CurrencyHandlers.takeCurrency(p.getUniqueId(), currency, price);
                context.getForWhom().sendRawMessage(Message.expandAccepted.getTranslate());
                //Do something to expand the resident...
                if (face == PlayerFace.UP) {
                    boolean b;
                    Loc loc;
                    if (first.getY() > second.getY()) {
                        loc = first;
                        b = true;
                    } else {
                        loc = second;
                        b = false;
                    }
                    loc.setY(loc.getY() + count);
                    if (b) {
                        resident.setFirstLocation(loc);
                    } else {
                        resident.setSecondLocation(loc);
                    }
                } else if (face == PlayerFace.DOWN) {
                    Loc loc;
                    boolean b;
                    if (first.getY() < second.getY()) {
                        loc = first;
                        b = true;
                    } else {
                        loc = second;
                        b = false;
                    }
                    loc.setY(loc.getY() - count);
                    if (b) {
                        resident.setFirstLocation(loc);
                    } else {
                        resident.setSecondLocation(loc);
                    }
                } else if (face == PlayerFace.NORTH) {
                    Loc loc;
                    boolean b;
                    if (first.getZ() < second.getZ()) {
                        loc = first;
                        b = true;
                    } else {
                        loc = second;
                        b = false;
                    }
                    loc.setZ(loc.getZ() - count);
                    if (b) {
                        resident.setFirstLocation(loc);
                    } else {
                        resident.setSecondLocation(loc);
                    }
                } else if (face == PlayerFace.SOUTH) {
                    Loc loc;
                    boolean b;
                    if (first.getZ() > second.getZ()) {
                        loc = first;
                        b = true;
                    } else {
                        loc = second;
                        b = false;
                    }
                    loc.setZ(loc.getZ() + count);
                    if (b) {
                        resident.setFirstLocation(loc);
                    } else {
                        resident.setSecondLocation(loc);
                    }
                } else if (face == PlayerFace.EAST) {
                    Loc loc;
                    boolean b;
                    if (first.getX() > second.getX()) {
                        loc = first;
                        b = true;
                    } else {
                        loc = second;
                        b = false;
                    }
                    loc.setX(loc.getX() + count);
                    if (b) {
                        resident.setFirstLocation(loc);
                    } else {
                        resident.setSecondLocation(loc);
                    }
                } else if (face == PlayerFace.WEST) {
                    Loc loc;
                    boolean b;
                    if (first.getX() < second.getX()) {
                        loc = first;
                        b = true;
                    } else {
                        loc = second;
                        b = false;
                    }
                    loc.setX(loc.getX() - count);
                    if (b) {
                        resident.setFirstLocation(loc);
                    } else {
                        resident.setSecondLocation(loc);
                    }
                }
            } else {
                context.getForWhom().sendRawMessage(Message.expandDenied.getTranslate());
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return Message.expandConfirm.getTranslate()
                    .replace("{face}", face.toString())
                    .replace("{count}", String.valueOf(count))
                    .replace("{price}", String.valueOf(price))
                    .replace("{currency}", currency.getName())
                    .replace("{balance}", String.valueOf(CurrencyHandlers.getCurrency(p.getUniqueId(), currency)));
        }
    }
    public final static class ResidentRemove extends ValidatingPrompt {

        private final Resident res;

        public ResidentRemove(Resident res) {
            this.res = res;
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String s) {
            if (s.equalsIgnoreCase("y")) {
                Player p = Bukkit.getPlayer(res.getOwner());
                if (p == null) {
                    throw new RuntimeException("Player not online!");
                }
                RuntimeDataManager.removeResident(res);
                CurrencyHandlers.addCurrency(res.getOwner(), Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))), GeneralUtils.getSellPrice(res));
                context.getForWhom().sendRawMessage(Message.removeAccepted.getTranslate()
                        .replace("{price}", String.valueOf(GeneralUtils.getSellPrice(res)))
                        .replace("{currency}", Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))).getName())
                );
            } else {
                context.getForWhom().sendRawMessage(Message.removeDenied.getTranslate());
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return Message.removeConfirm.getTranslate()
                    .replace("{price}", String.valueOf(GeneralUtils.getSellPrice(res)))
                    .replace("{currency}", Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))).getName());
        }
    }
    public final static class ResidentRemoveAll extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String s) {
            if (s.equalsIgnoreCase("y")) {
                Player p = (Player) context.getForWhom();
                int sum = 0;
                for (Resident res : RuntimeDataManager.getPlayerResidents(p.getUniqueId()).values()) {
                    sum += GeneralUtils.getSellPrice(res);
                    RuntimeDataManager.removeResident(res);
                }
                CurrencyHandlers.addCurrency(p.getUniqueId(), Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))), sum);
                context.getForWhom().sendRawMessage(Message.removeAccepted.getTranslate()
                        .replace("{price}", String.valueOf(sum))
                        .replace("{currency}", Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))).getName())
                );
            } else {
                context.getForWhom().sendRawMessage(Message.removeDenied.getTranslate());
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            Player p = (Player) context.getForWhom();
            int sum = 0;
            for (Resident res : RuntimeDataManager.getPlayerResidents(p.getUniqueId()).values()) {
                sum += GeneralUtils.getSellPrice(res);
            }
            return Message.removeAllConfirm.getTranslate()
                    .replace("{price}", String.valueOf(sum))
                    .replace("{currency}", Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"))).getName());
        }
    }
    public final static class ResidentGive extends ValidatingPrompt {

        Resident res;
        Player to;
        int price;

        public ResidentGive(Resident res, Player to, int price) {
            this.res = res;
            this.to = to;
            this.price = price;
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String s) {
            if (s.equalsIgnoreCase("y")) {
                context.getForWhom().sendRawMessage(Message.giveAccepted.getTranslate());
                Conversation conv = new ConversationFactory(Residence.getInstance())
                        .withFirstPrompt(new ResidentReceive(res, price))
                        .withTimeout(60)
                        .buildConversation(to);
                conv.begin();
            } else {
                context.getForWhom().sendRawMessage(Message.giveDenied.getTranslate());
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return Message.giveConfirm.getTranslate()
                    .replace("{player}", to.getName());
        }
    }
    public final static class ResidentReceive extends ValidatingPrompt {

        Resident res;
        int price;

        public ResidentReceive(Resident res, int price) {
            this.res = res;
            this.price = price;
        }

        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String s) {
            Player player = Bukkit.getPlayer(res.getOwner());
            if (s.equalsIgnoreCase("y")) {
                if (player != null) {
                    player.sendMessage(Message.giveSuccess.getTranslate()
                            .replace("{player}", ((Player)context.getForWhom()).getName()));
                }
                context.getForWhom().sendRawMessage(Message.receiveAccepted.getTranslate());
                res.setOwner(((Player) context.getForWhom()).getUniqueId());
            } else {
                if (player != null) {
                    player.sendMessage(Message.giveFailed.getTranslate()
                            .replace("{player}", ((Player)context.getForWhom()).getName()));
                }
                context.getForWhom().sendRawMessage(Message.receiveDenied.getTranslate());
            }
            return Prompt.END_OF_CONVERSATION;
        }
        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            if (price == 0) {
                return Message.receiveConfirm.getTranslate()
                        .replace("{player}", Objects.requireNonNull(Bukkit.getOfflinePlayer(res.getOwner()).getName()))
                        .replace("{world}", res.getWorld().getName())
                        .replace("{first_location_x}", String.valueOf(res.getFirstLocation().getX()))
                        .replace("{first_location_y}", String.valueOf(res.getFirstLocation().getY()))
                        .replace("{first_location_z}", String.valueOf(res.getFirstLocation().getZ()))
                        .replace("{second_location_x}", String.valueOf(res.getSecondLocation().getX()))
                        .replace("{second_location_y}", String.valueOf(res.getSecondLocation().getY()))
                        .replace("{second_location_z}", String.valueOf(res.getSecondLocation().getZ()));
            } else {
                context.getForWhom().sendRawMessage(Message.receiveConfirm.getTranslate()
                        .replace("{player}", Objects.requireNonNull(Bukkit.getOfflinePlayer(res.getOwner()).getName()))
                        .replace("{world}", res.getWorld().getName())
                        .replace("{first_location_x}", String.valueOf(res.getFirstLocation().getX()))
                        .replace("{first_location_y}", String.valueOf(res.getFirstLocation().getY()))
                        .replace("{first_location_z}", String.valueOf(res.getFirstLocation().getZ()))
                        .replace("{second_location_x}", String.valueOf(res.getSecondLocation().getX()))
                        .replace("{second_location_y}", String.valueOf(res.getSecondLocation().getY()))
                        .replace("{second_location_z}", String.valueOf(res.getSecondLocation().getZ())));
                return Message.receiveCost.getTranslate();
            }
        }
    }
}
