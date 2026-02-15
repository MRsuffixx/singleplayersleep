# SinglePlayerSleep v1.2.2 - Hotfix 🩹

This release fixes a critical crash related to BossBar configuration.

## 🐛 Bug Fixes
- **[Critical]** Fixed `IllegalArgumentException` when loading BossBar with style "SOLID".
  - The plugin now correctly maps generic names like `SOLID`, `SEGMENTED_6` to their internal Adventure API counterparts (`PROGRESS`, `NOTCHED_6`).
  - Added safety checks: if an invalid color or style is configured, it will fallback to defaults instead of crashing the server.

---

**Full Changelog**: https://github.com/MRsuffixx/SinglePlayerSleep/compare/v1.2.1...v1.2.2
