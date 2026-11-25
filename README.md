# UpperStrata

**UpperStrata** is a fork of the open-source [Strata](https://github.com/CluvexStudio/Strata) Android file manager, tuned with a **MiXplorer** design philosophy and power-user optimizations. It combines Strata's rich feature set—archives, APK/DEX tools, code/hex editors, remote storage providers, and encrypted vault—with a fast, high-density, tabbed workspace.

---

## What's Different from Original Strata

UpperStrata focuses on speed, screen real-estate efficiency, and MiXplorer-style usability enhancements:

- 📑 **MiXplorer-Style Top Tab Bar:** Replaced rigid side-by-side split screens with full-width active pane view navigated by a top tab bar. Supports creating new tabs, closing tabs, and opening any folder into a new pane directly from the long-press menu.
- 🗂️ **Compact & Dense Navigation Sidebar:** Redesigned navigation drawer with a high-density vertical layout (~300dp width), clear path subtitles, protocol URIs (`smb://...`, `sftp://...`), dedicated **Bookmarks** and **Network** sections, and direct access to the **Recycle Bin**.
- 🚀 **Zero-Jank & High-Performance Scrolling:**
  - Removed item touch press scaling to eliminate icon shift on touch down.
  - Disabled lazy layout placement animations (`animateItem`) for ultra-smooth 60/120fps fast scrolling.
  - Replaced sub-composition checkbox animations with direct composable states.
  - Cached date formatters and memoized file category lookups to eliminate GC allocation churn.
- ⚡ **Instant Folder Navigation:** Removed unnecessary slide and fade transitions when entering folders for instant MiXplorer-fast directory switching.
- 🛠️ **Enhanced Contextual Actions & Usability:**
  - **Contextual Clipboard:** Instant tap clipboard action menu (*Paste here* / *Clear clipboard*).
  - **Breadcrumb Navigation:** Tap any parent path segment in the header to jump directly.
  - **Copy Full Path:** One-tap action to copy absolute file path to the system clipboard.
  - **Open With Chooser:** Added *Open with* option to the single-file selection action overflow menu.

---

## Core Features

- 📁 **File Management:** Full-width tabbed panes, drag and drop, Root (`su`) and Shizuku IPC access, Recycle Bin, batch rename, permissions, symlinks, split & merge, and file encryption.
- 📦 **Archive Tools:** Open and extract ZIP, 7z, RAR, TAR, GZ, XZ, ZST; create password-protected ZIP, 7z, and TAR archives.
- 🔧 **APK & DEX Tools:** App details, split APK installation, v1–v3 APK signing, package cloning, DEX/smali editor, Java decompiler, manifest and resource ARSC editors.
- 📝 **Built-in Editors & Viewers:** TextMate syntax-highlighted code editor, hex editor, PDF reader, image viewer, media player, font viewer, Markdown preview, 9-patch editor, color picker.
- 🌐 **Network Storage & Remote:** SMB, SFTP, FTP, WebDAV, plus local Wi-Fi sharing via HTTP/FTP server.
- 💻 **Termux Integration:** Execute and build code in multiple languages via the Termux bridge.
- 🔒 **Privacy & Vault:** AES-256 encrypted vault with biometric authentication and local device security policies.

---

## Building

### Requirements
- Android SDK with NDK
- Rust with Android targets and `cargo-ndk`
- JDK 17 (configured via Gradle)

```bash
rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android
cargo install cargo-ndk
native/fetch-pdfium.sh
native/build.sh
./gradlew :app:assembleDebug
```

---

## License

UpperStrata is licensed under the [GNU AGPL v3.0 or later](LICENSE), matching upstream Strata.
