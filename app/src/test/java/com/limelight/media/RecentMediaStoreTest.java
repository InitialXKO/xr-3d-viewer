package com.limelight.media;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecentMediaStoreTest {
    @Test
    public void remembersNewestFirstAndMovesDuplicatesToTheFront() {
        List<RecentMediaStore.RecentMedia> recent = new ArrayList<>();
        for (int i = 0; i < RecentMediaStore.MAX_ITEMS; i++) {
            recent = RecentMediaStore.remember(recent, "content://tree/item" + i,
                    "item" + i, i % 2 == 0);
        }
        recent = RecentMediaStore.remember(recent, "content://tree/item3", "renamed.mp4", true);

        assertEquals(RecentMediaStore.MAX_ITEMS, recent.size());
        assertEquals("content://tree/item3", recent.get(0).uri);
        assertEquals("renamed.mp4", recent.get(0).name);
        assertTrue(recent.get(0).video);
        assertEquals("content://tree/item7", recent.get(1).uri);
        assertFalse(recent.get(RecentMediaStore.MAX_ITEMS - 1).uri.endsWith("item3"));
    }

    @Test
    public void serializesSpecialCharactersAndMediaKind() {
        List<RecentMediaStore.RecentMedia> original = new ArrayList<>();
        original.add(new RecentMediaStore.RecentMedia(
                "content://provider/tree/猫%2Bfolder/item\t1.jpg", "猫 + summer\nphoto.jpg", false));
        original.add(new RecentMediaStore.RecentMedia(
                "content://provider/tree/movie.mkv", "movie + live.mkv", true));

        List<RecentMediaStore.RecentMedia> restored = RecentMediaStore.parse(
                RecentMediaStore.serialize(original));

        assertEquals(2, restored.size());
        assertEquals(original.get(0).uri, restored.get(0).uri);
        assertEquals(original.get(0).name, restored.get(0).name);
        assertFalse(restored.get(0).video);
        assertEquals(original.get(1).uri, restored.get(1).uri);
        assertEquals(original.get(1).name, restored.get(1).name);
        assertTrue(restored.get(1).video);
    }

    @Test
    public void ignoresMalformedAndDuplicateSavedRecords() {
        String valid = RecentMediaStore.serialize(java.util.Collections.singletonList(
                new RecentMediaStore.RecentMedia("content://one", "one.jpg", false)));
        List<RecentMediaStore.RecentMedia> parsed = RecentMediaStore.parse(
                "broken\n" + valid + "\n" + valid);

        assertEquals(1, parsed.size());
        assertEquals("content://one", parsed.get(0).uri);
    }
}
