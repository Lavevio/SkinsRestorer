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
package net.skinsrestorer;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.Injector;
import lombok.extern.slf4j.Slf4j;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.shared.config.APIConfig;
import net.skinsrestorer.shared.config.AdvancedConfig;
import net.skinsrestorer.shared.connections.MineSkinAPIImpl;
import net.skinsrestorer.shared.subjects.messages.SkinsRestorerLocale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith({MockitoExtension.class, SRExtension.class})
public class AxolotlUrlTest {
    @Mock
    private SettingsManager settings;
    @Mock
    private SkinsRestorerLocale skinsRestorerLocale;

    @Test
    public void testAxolotlUrlDecryption(Injector injector) {
        injector.register(SkinsRestorerLocale.class, skinsRestorerLocale);

        when(settings.getProperty(APIConfig.MINESKIN_API_KEY)).thenReturn("");
        when(settings.getProperty(AdvancedConfig.NO_CONNECTIONS)).thenReturn(false);

        injector.register(SettingsManager.class, settings);

        // Test that an invalid axolotl URL throws an exception
        String invalidAxolotlUrl = "skinsrestorer-axolotl://invalid-encrypted-data";

        MineSkinAPIImpl mineSkinAPI = injector.getSingleton(MineSkinAPIImpl.class);

        // This should throw a DataRequestException because the URL is invalid
        assertThrows(DataRequestException.class, () -> {
            mineSkinAPI.genSkin(invalidAxolotlUrl, null);
        });

        log.info("Axolotl URL decryption test completed");
    }
}
