package net.neferett.socialmedia;

import lombok.Getter;
import net.neferett.coreengine.CoreEngine;
import net.neferett.coreengine.Processors.Config.PreConfig;
import net.neferett.coreengine.Processors.Logger.Logger;
import net.neferett.coreengine.Processors.Plugins.ExtendablePlugin;
import net.neferett.coreengine.Processors.Plugins.Plugin;
import net.neferett.socialmedia.Config.ConfigFile;
import net.neferett.socialmedia.Manager.BotsManager;
import net.neferett.socialmedia.Medias.TelegramBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@Plugin(name = "SocialMedia", configPath = "SocialMedia/config.json")
@Getter
public class SocialMedia extends ExtendablePlugin {

    public static SocialMedia getInstance() {
        return (SocialMedia) CoreEngine.getInstance().getPlugin(SocialMedia.class);
    }

    private ConfigFile configFile;

    private BotsManager botsManager;

    private void loadConfig() {
        PreConfig preConfig = new PreConfig(this.getConfigPath(), ConfigFile.class);
        this.configFile = (ConfigFile) preConfig.loadPath().loadClazz().getConfig();
    }

    @Override
    public void onEnable() {
        this.loadConfig();

        this.botsManager = new BotsManager();

        this.botsManager.add(new TelegramBot());

        this.botsManager.registerBots();

        this.botsManager.sendMessage("Trading Bot activated !");
    }

    @Override
    public void onDisable() {

    }
}
