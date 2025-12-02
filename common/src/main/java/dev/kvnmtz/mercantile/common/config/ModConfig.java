package dev.kvnmtz.mercantile.common.config;

import com.mojang.datafixers.util.Pair;
import dev.architectury.platform.Platform;
import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.common.data.CoinType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public abstract class ModConfig {

    public static int RETRIEVE_COIN_BUTTON_WIDTH;
    public static int RETRIEVE_COIN_BUTTON_HEIGHT;
    public static int RETRIEVE_COIN_BUTTON_ICON_OFFSET_X;
    public static int RETRIEVE_COIN_BUTTON_ICON_OFFSET_Y;
    public static int COIN_TEXTURE_SIZE;

    public static final Map<String, Pair<Integer, Integer>> RETRIEVE_COIN_BUTTON_OFFSETS = new HashMap<>();
    public static final Map<String, Pair<Integer, Integer>> RETRIEVE_COIN_BUTTON_CREATIVE_OFFSETS = new HashMap<>();

    private static final Set<String> STATIC_PROPERTY_NAMES = Set.of(
            "retrieve_coin_button_width",
            "retrieve_coin_button_height",
            "retrieve_coin_button_icon_offset_x",
            "retrieve_coin_button_icon_offset_y",
            "coin_texture_size"
    );

    public static void load() {
        var configPath = Platform.getConfigFolder().resolve("mercantile-common.properties");

        if (!Files.exists(configPath)) {
            try {
                Files.writeString(configPath, """
                        # Here you can define all coin types with their respective values
                        # For example, "copper = 1" (without quotes) creates a mercantile:copper_coin item with a value of 1
                        
                        copper = 1
                        silver = 100
                        gold = 2500
                        
                        # GUI related settings
                        
                        retrieve_coin_button_width = 14
                        retrieve_coin_button_height = 13
                        retrieve_coin_button_icon_offset_x = -1
                        retrieve_coin_button_icon_offset_y = -2
                        
                        coin_texture_size = 16
                        
                        # If you add a new coin type, you'll also have to define the offsets for the button in the inventory below
                        # (for both survival and creative inventory)
                        
                        copper_retrieve_coin_button_offset_x = 77
                        copper_retrieve_coin_button_offset_y = 7
                        copper_retrieve_coin_button_creative_offset_x = 127
                        copper_retrieve_coin_button_creative_offset_y = 5
                        
                        silver_retrieve_coin_button_offset_x = 77
                        silver_retrieve_coin_button_offset_y = 21
                        silver_retrieve_coin_button_creative_offset_x = 127
                        silver_retrieve_coin_button_creative_offset_y = 19
                        
                        gold_retrieve_coin_button_offset_x = 77
                        gold_retrieve_coin_button_offset_y = 35
                        gold_retrieve_coin_button_creative_offset_x = 127
                        gold_retrieve_coin_button_creative_offset_y = 33
                        """, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                MercantileMod.LOGGER.error("Mercantile config file could not be created", e);
                return;
            }
        }

        try (var input = Files.newInputStream(configPath)) {
            var props = new Properties();
            props.load(input);

            var retrieveCoinButtonXOffsets = new HashMap<String, Integer>();
            var retrieveCoinButtonYOffsets = new HashMap<String, Integer>();
            var retrieveCoinButtonCreativeXOffsets = new HashMap<String, Integer>();
            var retrieveCoinButtonCreativeYOffsets = new HashMap<String, Integer>();

            for (var propertyName : props.stringPropertyNames()) {
                if (STATIC_PROPERTY_NAMES.contains(propertyName)) {
                    continue;
                }

                if (propertyName.contains("_retrieve_coin_button_offset_")) {
                    var coinName = propertyName.substring(0, propertyName.indexOf("_retrieve_coin_button_offset_"));

                    if (propertyName.endsWith("x")) {
                        retrieveCoinButtonXOffsets.put(coinName, Integer.parseInt(props.getProperty(propertyName)));
                    } else if (propertyName.endsWith("y")) {
                        retrieveCoinButtonYOffsets.put(coinName, Integer.parseInt(props.getProperty(propertyName)));
                    }

                    continue;
                }

                if (propertyName.contains("_retrieve_coin_button_creative_offset_")) {
                    var coinName = propertyName.substring(0, propertyName.indexOf(
                            "_retrieve_coin_button_creative_offset_"));

                    if (propertyName.endsWith("x")) {
                        retrieveCoinButtonCreativeXOffsets.put(coinName,
                                Integer.parseInt(props.getProperty(propertyName)));
                    } else if (propertyName.endsWith("y")) {
                        retrieveCoinButtonCreativeYOffsets.put(coinName,
                                Integer.parseInt(props.getProperty(propertyName)));
                    }

                    continue;
                }

                var valueStr = props.getProperty(propertyName);

                try {
                    var value = Integer.parseInt(valueStr.trim());
                    CoinType.register(propertyName, value);
                } catch (NumberFormatException e) {
                    MercantileMod.LOGGER.error("Skipping coin '{}': Invalid value '{}'. Must be an integer.", propertyName,
                            valueStr);
                } catch (IllegalArgumentException e) {
                    MercantileMod.LOGGER.error("Skipping coin '{}': {}", propertyName, e.getMessage());
                }
            }

            for (var key : retrieveCoinButtonXOffsets.keySet()) {
                var xOffset = retrieveCoinButtonXOffsets.get(key);
                var yOffset = retrieveCoinButtonYOffsets.get(key);
                RETRIEVE_COIN_BUTTON_OFFSETS.put(key, Pair.of(xOffset, yOffset));
            }

            for (var key : retrieveCoinButtonCreativeXOffsets.keySet()) {
                var xOffset = retrieveCoinButtonCreativeXOffsets.get(key);
                var yOffset = retrieveCoinButtonCreativeYOffsets.get(key);
                RETRIEVE_COIN_BUTTON_CREATIVE_OFFSETS.put(key, Pair.of(xOffset, yOffset));
            }

            RETRIEVE_COIN_BUTTON_WIDTH = Integer.parseInt(props.getProperty("retrieve_coin_button_width"));
            RETRIEVE_COIN_BUTTON_HEIGHT = Integer.parseInt(props.getProperty("retrieve_coin_button_height"));
            RETRIEVE_COIN_BUTTON_ICON_OFFSET_X = Integer.parseInt(props.getProperty(
                    "retrieve_coin_button_icon_offset_x"));
            RETRIEVE_COIN_BUTTON_ICON_OFFSET_Y = Integer.parseInt(props.getProperty(
                    "retrieve_coin_button_icon_offset_y"));
            COIN_TEXTURE_SIZE = Integer.parseInt(props.getProperty("coin_texture_size"));
        } catch (IOException e) {
            MercantileMod.LOGGER.error("Mercantile config file could not be read", e);
        }
    }
}
