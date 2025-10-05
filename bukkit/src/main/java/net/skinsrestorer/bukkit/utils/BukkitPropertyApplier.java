/*
 * SkinsRestorer
 * Copyright (C) 2024  SkinsRestorer Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.skinsrestorer.bukkit.utils;

import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.shared.log.SRLogger;
import net.skinsrestorer.shared.utils.AuthLibHelper;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BukkitPropertyApplier implements SkinApplyBukkitAdapter {
    private static final Class<?> GAME_PROFILE_CLASS;
    private static final Method GET_PROPERTIES_METHOD;
    private static final Constructor<?> PROPERTY_CLASS_CONSTRUCTOR;

    static {
        Class<?> gameProfileClass;
        try {
            gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
        } catch (ClassNotFoundException e) {
            try {
                gameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException ex) {
                var error = new ExceptionInInitializerError("Failed to find GameProfile class");
                error.addSuppressed(ex);
                error.addSuppressed(e);

                throw error;
            }
        }

        GAME_PROFILE_CLASS = gameProfileClass;

        Method getPropertiesMethod;
        try {
            getPropertiesMethod = GAME_PROFILE_CLASS.getMethod("getProperties");
        } catch (NoSuchMethodException e) {
            try {
                getPropertiesMethod = GAME_PROFILE_CLASS.getMethod("properties");
            } catch (NoSuchMethodException ex) {
                var error = new ExceptionInInitializerError("Failed to find getProperties method");
                error.addSuppressed(ex);
                error.addSuppressed(e);

                throw error;
            }
        }

        GET_PROPERTIES_METHOD = getPropertiesMethod;

        Constructor<?> propertyClass;
        try {
            propertyClass = Class.forName("com.mojang.authlib.properties.Property").getConstructor(String.class, String.class, String.class);
        } catch (ReflectiveOperationException e) {
            try {
                propertyClass = Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property").getConstructor(String.class, String.class, String.class);
            } catch (ReflectiveOperationException ex) {
                var error = new ExceptionInInitializerError("Failed to find Property class");
                error.addSuppressed(ex);
                error.addSuppressed(e);

                throw error;
            }
        }

        PROPERTY_CLASS_CONSTRUCTOR = propertyClass;
    }

    private final SRLogger logger;

    @SuppressWarnings("unchecked")
    @Override
    public void applyProperty(Player player, SkinProperty property) {
        try {
            Multimap<String, Object> properties = (Multimap<String, Object>) GET_PROPERTIES_METHOD.invoke(getGameProfile(player, GAME_PROFILE_CLASS));
            properties.removeAll(SkinProperty.TEXTURES_NAME);
            properties.put(SkinProperty.TEXTURES_NAME, PROPERTY_CLASS_CONSTRUCTOR.newInstance(SkinProperty.TEXTURES_NAME, property.getValue(), property.getSignature()));
        } catch (ReflectiveOperationException e) {
            logger.severe("Failed to apply skin property to player %s".formatted(player.getName()), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<SkinProperty> getSkinProperty(Player player) {
        try {
            return ((Multimap<String, Object>) GET_PROPERTIES_METHOD.invoke(getGameProfile(player, GAME_PROFILE_CLASS))).values()
                    .stream()
                    .map(property -> SkinProperty.tryParse(
                            AuthLibHelper.getPropertyName(property),
                            AuthLibHelper.getPropertyValue(property),
                            AuthLibHelper.getPropertySignature(property))
                    )
                    .flatMap(Optional::stream)
                    .findFirst();
        } catch (ReflectiveOperationException e) {
            logger.severe("Failed to get skin property from player %s".formatted(player.getName()), e);
            return Optional.empty();
        }
    }
}
