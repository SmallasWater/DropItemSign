package DropItem;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.LinkedHashMap;

public class sign extends PluginBase implements Listener{

    private LinkedHashMap<Player,Item> dropItem = new LinkedHashMap<>();
    @Override
    public void onEnable() {
        this.getLogger().info("loading ok");
        if(!new File(this.getDataFolder()+"/config.yml").exists()){
            this.saveDefaultConfig();
            this.reloadConfig();
        }

        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @EventHandler
    public void onWriteSign(SignChangeEvent event) throws Exception {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(event.getLine(0).equals("drop")){
            for (int i = 0;i < 4;i++){
                event.setLine(i,(String) getConfig().getList("showMessage").get(i));
            }
            player.getLevel().setBlock(new Vector3(block.getX(),block.getY(),block.getZ()),block);
            player.sendMessage("§l§b [Trash] Setting Success");
        }
    }

    public Config getConfig(){
        return new Config(this.getDataFolder()+"/config.yml",Config.YAML);
    }

   @EventHandler
    public void PlayerCanDrop(PlayerInteractEvent event) throws Exception {
        Player player = event.getPlayer();
        if(isTrash(event.getBlock())){
            if(player.isSneaking()){
                if(player.getInventory().getItemInHand().getId() != 0){
                    if(dropItem.containsKey(player)){
                        if(dropItem.get(player).equals(player.getInventory().getItemInHand(),true,true)){
                            player.getInventory().removeItem(player.getInventory().getItemInHand());
                            player.sendMessage("§l§6 [Trash] Item is Drop");
                            dropItem.remove(player);
                        }else{
                            player.sendMessage("§l§c [Trash] Item is't Last Click");
                            dropItem.remove(player);
                        }
                    }else{
                        player.sendMessage("§l§e [Trash]  Click Again this Place");
                        dropItem.put(player,player.getInventory().getItemInHand());
                    }
                }else{
                    player.sendMessage("§l§6 [Trash] You Can't Drop Air");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for (Level level : Server.getInstance().getLevels().values()){
            level.save();
        }
        this.getLogger().info("is Disable");
    }
    private boolean isTrash(Block block){
        if(block.getId() == 68 || block.getId() == 63){
            BlockEntitySign Sign = (BlockEntitySign) block.getLevel()
                    .getBlockEntity(new Vector3(block.getX(), block.getY(), block.getZ()));
            String[] text = Sign.getText();
            int c = 0;
            for (int i = 0;i < 4;i++){
                if(getConfig().getList("showMessage").contains(text[i]))
                    c++;
            }
            if(c == getConfig().getList("showMessage").size())
                return true;
        }
        return false;
    }
}
