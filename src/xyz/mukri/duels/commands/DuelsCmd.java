package xyz.mukri.duels.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mukri.duels.Core;
import xyz.mukri.duels.arena.Arena;
import xyz.mukri.duels.file.ArenaFile;
import xyz.mukri.duels.file.KitFile;
import xyz.mukri.duels.file.MsgFile;
import xyz.mukri.duels.file.StorageFile;
import xyz.mukri.duels.utils.CustomInventory;

import java.util.List;

public class DuelsCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {

            sender.sendMessage("You must be a player to perform this commands.");

            return false;
        }

        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("duels")) {

            if (args.length == 0) {
                if (p.hasPermission("duels.players")) {
                    CustomInventory.getArenaListGUI(p);
                }
                return false;
            }

            // EG: /duels test
            else if (args.length == 1) {

                if (args[0].equalsIgnoreCase("leave")) {
                    if (p.hasPermission("duels.players")) {
                        Arena arena = Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId());

                        if (arena != null) {
                            if (arena.getPlayers().contains(p.getUniqueId())) {
                                arena.userLeave(p);
                            } else {
                                p.sendMessage(Core.getInstance().msgFile.getYouareNotIngameMsg());
                            }
                        }
                    }
                }

                else if (args[0].equalsIgnoreCase("top")) {
                    if (p.hasPermission("duels.players")) {
                        List<String> top3 = Core.getInstance().storageFile.getTop3();

                        for (int i = 0; i < top3.size(); i++) {
                            String[] s = top3.get(i).split(":");

                            top3.set(i, s[0] + ": W: " + s[1]);
                        }

                        String p1 = top3.get(0);
                        String p2 = top3.get(1);
                        String p3 = top3.get(2);

                        List<String> msgfromFile = Core.getInstance().msgFile.getTop3Msg();

                        for (String msg : msgfromFile) {
                            p.sendMessage(msg.replaceAll("%top1%", p1).replaceAll("%top2%", p2)
                                    .replaceAll("%top3%", p3).replaceAll("&", "§"));
                        }
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                    }
                }

                else if (args[0].equalsIgnoreCase("reload")) {

                    if (p.hasPermission("duels.admin")) {

                        Core.getInstance().kitFile = new KitFile();
                        Core.getInstance().msgFile = new MsgFile();
                        Core.getInstance().arenaFile = new ArenaFile();
                        Core.getInstance().storageFile = new StorageFile();

                        p.sendMessage(Core.getInstance().msgFile.getReloadedMsg());

                    }
                }

                else if (args[0].equalsIgnoreCase("help")) {
                    if (p.hasPermission("duels.help")) {
                        p.sendMessage(" ");
                        p.sendMessage("/duels - Open Arena GUI");
                        p.sendMessage("/duels leave - Leave an arena");
                        p.sendMessage("/duels join <arena> - Join an arena");
                        p.sendMessage("/duels reload - Reload all .yml");
                        p.sendMessage("/duels addarena <arena> - Create an arena");
                        p.sendMessage("/duels delarena <arena> - Delete an arena");
                        p.sendMessage("/duels settings <arena> - Set an arena");
                        p.sendMessage("/duels setspawn1 <arena> - Set playerone spawn");
                        p.sendMessage("/duels setspawn2 <arena> - Set playertwo spawn");
                        p.sendMessage("/duels setspawn <arena> - Set main spawn");
                        p.sendMessage(" ");
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                    }
                }

                else {
                    p.sendMessage("§aUnkown command! /Duels help");
                }

            }

            // EG: /duels addarena arenaname
            else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("addarena")) {
                    if (!p.hasPermission("duels.admin")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena == null) {

                        Arena newArena = new Arena(arenaName, null, null, null, 120);

                        Core.getInstance().arenaManager.addArena(newArena);
                        Core.getInstance().arenaFile.addNewArena(arenaName);

                        p.sendMessage(Core.getInstance().msgFile.getArenaCreatedMsg(arenaName));
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaAlreadyExistsMsg(arenaName));
                    }
                }

                else if (args[0].equalsIgnoreCase("delarena")) {
                    if (!p.hasPermission("duels.admin")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena != null) {
                        Core.getInstance().arenaManager.removeArena(arena);
                        Core.getInstance().arenaFile.getConfig().set("arena." + arena.getArenaName(), null);
                        p.sendMessage(Core.getInstance().msgFile.getArenaRemoved(arenaName));
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaDoesNotExists(arenaName));
                    }
                }

                else if (args[0].equalsIgnoreCase("setspawn")) {
                    if (!p.hasPermission("duels.admin")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena != null) {
                        arena.setSpawn(p.getLocation());

                        Core.getInstance().arenaFile.setArenaSpawn(p.getLocation(), arena);
                        Core.getInstance().arenaFile.save();
                        p.sendMessage(Core.getInstance().msgFile.getSetSpawnMsg("Main Spawn"));
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaAlreadyExistsMsg(arenaName));
                    }

                }

                else if (args[0].equalsIgnoreCase("setspawn1")) {
                    if (!p.hasPermission("duels.admin")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena != null) {
                        arena.setPlayerOneSpawn(p.getLocation());

                        Core.getInstance().arenaFile.setPlayerSpawn(p.getLocation(), arena, 1);
                        Core.getInstance().arenaFile.save();
                        p.sendMessage(Core.getInstance().msgFile.getSetSpawnMsg("Player One"));
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaAlreadyExistsMsg(arenaName));
                    }

                }

                else if (args[0].equalsIgnoreCase("setspawn2")) {
                    if (!p.hasPermission("duels.admin")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena != null) {
                        arena.setPlayerTwoSpawn(p.getLocation());

                        Core.getInstance().arenaFile.setPlayerSpawn(p.getLocation(), arena, 2);
                        Core.getInstance().arenaFile.save();
                        p.sendMessage(Core.getInstance().msgFile.getSetSpawnMsg("Player Two"));
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaAlreadyExistsMsg(arenaName));
                    }

                }

                else if (args[0].equals("join")) {
                    if (!p.hasPermission("duels.players")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena != null) {
                        arena.userJoin(p);
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaDoesNotExists(arenaName));
                    }
                }

                else if (args[0].equalsIgnoreCase("settings")) {
                    if (!p.hasPermission("duels.admin")) {
                        p.sendMessage(Core.getInstance().msgFile.getNoPermissionsMsg());
                        return false;
                    }

                    String arenaName = args[1];

                    Arena arena = Core.getInstance().arenaManager.getArenaByName(arenaName);

                    if (arena != null) {
                        Core.getInstance().playerSettings.put(p, arena);
                        CustomInventory.openArenaSettings(p, arena);
                    }
                    else {
                        p.sendMessage(Core.getInstance().msgFile.getArenaDoesNotExists(arenaName));
                    }
                }
                else {
                    p.sendMessage("§aUnkown command! /Duels help");
                }

            }

            else {
                p.sendMessage("§aUnkown command! /Duels help");
            }

        }

        return false;
    }
}
