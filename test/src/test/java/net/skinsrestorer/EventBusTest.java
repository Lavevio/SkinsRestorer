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

import ch.jalu.injector.Injector;
import net.skinsrestorer.api.event.SkinApplyEvent;
import net.skinsrestorer.shared.api.event.EventBusImpl;
import net.skinsrestorer.shared.api.event.SkinApplyEventImpl;
import net.skinsrestorer.shared.plugin.SRPlatformAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith({MockitoExtension.class, SRExtension.class})
public class EventBusTest {
    @Mock
    private SRPlatformAdapter srPlatformAdapter;
    @Mock
    private Object plugin;

    @Test
    public void testServices(Injector injector) {
        doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            long delay = invocation.getArgument(1);
            TimeUnit unit = invocation.getArgument(2);

            unit.sleep(delay);
            runnable.run();

            return null; // must return null because method returns void
        }).when(srPlatformAdapter).runAsyncDelayed(any(), any(), any());

        injector.register(SRPlatformAdapter.class, srPlatformAdapter);

        EventBusImpl eventBus = injector.getSingleton(EventBusImpl.class);

        eventBus.subscribe(plugin, SkinApplyEvent.class, new TestListener());

        SkinApplyEvent event = new SkinApplyEventImpl(null, null);

        eventBus.callEvent(event);

        assertTrue(event.isCancelled(), "Event was not cancelled");
    }

    private static class TestListener implements Consumer<SkinApplyEvent> {
        @Override
        public void accept(SkinApplyEvent event) {
            event.setCancelled(true);
        }
    }
}
