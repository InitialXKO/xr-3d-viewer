package com.limelight.media;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/** Small app-private history for media opened through a persisted local folder grant. */
public final class RecentMediaStore {
    public static final int MAX_ITEMS = 8;
    private static final String UTF_8 = "UTF-8";

    private RecentMediaStore() { }

    public static final class RecentMedia {
        public final String uri;
        public final String name;
        public final boolean video;

        public RecentMedia(String uri, String name, boolean video) {
            this.uri = uri == null ? "" : uri;
            this.name = name == null || name.isEmpty() ? this.uri : name;
            this.video = video;
        }
    }

    /** Put an item first, remove an older copy, and retain only the newest eight items. */
    public static List<RecentMedia> remember(List<RecentMedia> current, String uri,
                                             String name, boolean video) {
        ArrayList<RecentMedia> updated = new ArrayList<>();
        if (uri == null || uri.isEmpty()) return current == null
                ? updated : new ArrayList<>(current);
        updated.add(new RecentMedia(uri, name, video));
        if (current != null) {
            for (RecentMedia item : current) {
                if (item != null && !uri.equals(item.uri) && updated.size() < MAX_ITEMS) {
                    updated.add(item);
                }
            }
        }
        return updated;
    }

    /** Parse the private tab-delimited format; malformed records are safely ignored. */
    public static List<RecentMedia> parse(String encoded) {
        ArrayList<RecentMedia> items = new ArrayList<>();
        if (encoded == null || encoded.isEmpty()) return items;
        for (String line : encoded.split("\\n")) {
            if (items.size() >= MAX_ITEMS) break;
            String[] fields = line.split("\\t", -1);
            if (fields.length != 3 || !("v".equals(fields[0]) || "i".equals(fields[0]))) {
                continue;
            }
            try {
                String uri = decode(fields[1]);
                String name = decode(fields[2]);
                if (!uri.isEmpty()) {
                    boolean video = "v".equals(fields[0]);
                    boolean duplicate = false;
                    for (RecentMedia item : items) {
                        if (uri.equals(item.uri)) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) items.add(new RecentMedia(uri, name, video));
                }
            } catch (IllegalArgumentException ignored) {
                // A damaged preference should not prevent the media browser from opening.
            }
        }
        return items;
    }

    /** Encode a bounded recent list for one SharedPreferences string. */
    public static String serialize(List<RecentMedia> items) {
        if (items == null || items.isEmpty()) return "";
        StringBuilder saved = new StringBuilder();
        int count = 0;
        for (RecentMedia item : items) {
            if (item == null || item.uri.isEmpty() || count >= MAX_ITEMS) continue;
            if (saved.length() > 0) saved.append('\n');
            saved.append(item.video ? 'v' : 'i').append('\t')
                    .append(encode(item.uri)).append('\t')
                    .append(encode(item.name));
            count++;
        }
        return saved.toString();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, UTF_8);
        } catch (UnsupportedEncodingException impossible) {
            throw new AssertionError(impossible);
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, UTF_8);
        } catch (UnsupportedEncodingException impossible) {
            throw new AssertionError(impossible);
        }
    }
}
