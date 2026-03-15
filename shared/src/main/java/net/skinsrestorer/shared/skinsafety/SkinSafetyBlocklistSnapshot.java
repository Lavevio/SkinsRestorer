/*
 * SkinsRestorer
 * Copyright (C) 2026  SkinsRestorer Team
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
package net.skinsrestorer.shared.skinsafety;

import java.util.Set;
import java.util.UUID;

public record SkinSafetyBlocklistSnapshot(
        Set<String> blockedNames,
        Set<String> blockedTextureHashes,
        Set<UUID> blockedPlayerUuids,
        Set<String> blockedPlayerNames,
        Set<String> blockedUrlPrefixes,
        Set<String> blockedDomains,
        Set<String> blockedPngSha256,
        Set<String> blockedPerceptualHashes,
        Set<String> allowTextureHashes,
        Set<UUID> allowPlayerUuids
) {
    public SkinSafetyBlocklistSnapshot {
        blockedNames = Set.copyOf(blockedNames);
        blockedTextureHashes = Set.copyOf(blockedTextureHashes);
        blockedPlayerUuids = Set.copyOf(blockedPlayerUuids);
        blockedPlayerNames = Set.copyOf(blockedPlayerNames);
        blockedUrlPrefixes = Set.copyOf(blockedUrlPrefixes);
        blockedDomains = Set.copyOf(blockedDomains);
        blockedPngSha256 = Set.copyOf(blockedPngSha256);
        blockedPerceptualHashes = Set.copyOf(blockedPerceptualHashes);
        allowTextureHashes = Set.copyOf(allowTextureHashes);
        allowPlayerUuids = Set.copyOf(allowPlayerUuids);
    }

    public static SkinSafetyBlocklistSnapshot empty() {
        return new SkinSafetyBlocklistSnapshot(
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of()
        );
    }

    public boolean hasImageHashRules() {
        return !blockedPngSha256.isEmpty() || !blockedPerceptualHashes.isEmpty();
    }
}
