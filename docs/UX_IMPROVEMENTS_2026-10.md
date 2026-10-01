# UX improvements — October 2026

This iteration adds a compact **Recently opened** strip to the root of the local media browser. It retains up to eight local document URIs and display names in app-private preferences, reusing the existing persisted Android folder grant; it does not copy media, SMB credentials, or passwords. Recent items reopen individually, while selecting an item from the current folder keeps the existing folder playlist behavior.

The in-headset video control strip now labels its live state as **PLAYING**, **PAUSED**, **BUFFERING**, or **ENDED**. The in-headset settings cog also carries a visible **SETTINGS** label. The existing seek, previous/next, image navigation, local playback, and SMB playback paths are unchanged.

## Deferred

- **Subtitles:** the standalone video path feeds Media3 directly into the OpenXR renderer's decoder surface. There is no subtitle-selection UI or subtitle compositing path in the current XR output, so exposing a subtitle toggle now would be misleading. Subtitle support needs a deliberate overlay/composition design before it can be discoverable or persistent.
- **Browser handoff and streaming services:** playback currently accepts local document URIs and the app's SMB source. Browser pages, HLS, YouTube, and Plex do not have a supported source/decoder path here; this change does not claim or invent one.
- **Favorites and SMB recents:** local recent items are safe to reopen using the app's existing persistent folder permission. Cross-folder favorites and durable SMB recents need explicit provider/session-aware identity and lifecycle handling. The existing SMB “Open last image” behavior remains untouched.
- **Viewing presets:** current screen/depth controls store individual values and expose no named preset model. No arbitrary comfort or quality presets were added.
