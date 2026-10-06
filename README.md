<a id="top"></a>

<div align="center">

# DarkModifier

**Make GTNH easier, protect your liver.**

一个用于 **GTNH (GregTech: New Horizons)** 的 Minecraft 1.7.10 Forge 模组，
用 Mixin 重写蜂产品与作物的生产节奏，把重复等待从游戏时间里拿掉。

[![Minecraft](https://img.shields.io/badge/Minecraft-1.7.10-52A447?style=for-the-badge)](https://minecraft.net)
[![Forge](https://img.shields.io/badge/Forge-10.13.4.1614-2D6CB5?style=for-the-badge)](https://files.minecraftforge.net/)
[![UniMixins](https://img.shields.io/badge/Requires-UniMixins-D5453B?style=for-the-badge)](https://github.com/LegacyModdingMC/UniMixins)
[![License](https://img.shields.io/badge/License-MIT-4C8E50?style=for-the-badge)](LICENSE)
[![JDK](https://img.shields.io/badge/Build%20with-JDK%2021-E76F00?style=for-the-badge)](https://adoptium.net/)

<br/>

[![简体中文](https://img.shields.io/badge/-%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87-0052D9?style=for-the-badge)](#zh-cn)
[![English](https://img.shields.io/badge/-English-006B3F?style=for-the-badge)](#en-us)

[![Repository](https://img.shields.io/badge/GitHub-DarkAhri%2FDarkModifier-181717?style=for-the-badge&logo=github)](https://github.com/DarkAhri/DarkModifier)

</div>

---

<a id="zh-cn"></a>

<div align="center">

# 简体中文

</div>

## 核心特性

<table>
<tr>
<td width="50%" align="center">

**MixinQueenWorkTick**

<sub>Forestry · `BeekeepingLogic`</sub>

把每 tick 的节流自增从原版的 `1` 直接替换为配置值
产出间隔由固定 550 tick 缩短为约 `ceil(550 / 配置值)` tick

</td>
<td width="50%" align="center">

**MixinMTEIndustrialApiary**

<sub>GregTech · 工业蜂箱</sub>

将周期基准常量 `550.0f` 按配置值缩短
并把内部 `minTime = 周期 / 100` 改为 `/ 1`，
移除 100 tick 硬地板，同时消除短周期下的整数除零崩溃

</td>
</tr>
<tr>
<td width="50%" align="center">

**MixinTileEntityCropSticks**

<sub>CropsNH · 作物架</sub>

用配置值替换原版 `TICK_RATE = 256`
生长结算按相同比例变得更频繁

</td>
<td width="50%" align="center">

**MixinMTEIndustrialFarm**

<sub>CropsNH · 工业农场</sub>

按同一个 256 tick 基准等比缩放每周期生长进度
与作物架保持完全一致的倍率

</td>
</tr>
</table>

所有 mixin 均**按 modid 条件加载**：未安装对应模组时不会被织入，也不会影响启动。

## 运行环境

| 项目 | 要求 | 说明 |
|:---|:---|:---|
| Minecraft | `1.7.10` | 目标版本 |
| Forge | `10.13.4.1614` | 目标版本 |
| UniMixins | **必需** | 本模组依赖 Mixin |
| Java | `JDK 21` | 仅构建时需要，已验证可用 |
| Forestry | 可选 | 缺失则停用 `MixinQueenWorkTick` |
| CropsNH | 可选 | 缺失则停用两个作物 mixin |
| GregTech | 可选 | 缺失则停用 `MixinMTEIndustrialApiary` |

## 配置项

配置文件位于 `config/darkmodifier.cfg`，全部条目集中在 `general` 分类下。

| 键 | 默认值 | 范围 | 说明 |
|:---|:---|:---|:---|
| `queenWorkCycleThrottleIncrement` | `55` | `1` – `550` | 蜂后节流增量。**同时作用于 Forestry 蜂箱与 GT 工业蜂箱**（两者共用此值）。设为 `1` 等同原版，越大越快 |
| `cropMixinTickRate` | `16` | `1` – `256` | 作物基准周期（tick）。设为 `256` 等同原版，越小越快，默认 `16` 约为原版 16 倍速 |

两种修改方式：

- **游戏内 GUI**：主菜单 → `Mods` → 选中 `DarkModifier` → 左下角 `Mod Options`（即时生效）
- **手工编辑**：修改 cfg 后最多约 5 秒自动生效，无需重启

## 按键绑定

主菜单 → `选项` → `按键控制`，`DarkModifier` 分类下可重新绑定。

| 功能 | 默认按键 | 说明 |
|:---|:---|:---|
| 切换加速开关 | `'`（单引号） | 关闭后蜂箱与作物恢复原版速度，再次按下恢复加速。仅运行期有效，重启游戏后默认开启；切换时聊天栏提示当前状态 |

<details>
<summary><b>安装</b></summary>

1. 确保已安装 **UniMixins**
2. 将构建出的 jar 放入 `mods/` 目录
3. 启动一次游戏，配置文件会自动生成于 `config/darkmodifier.cfg`

</details>

<details>
<summary><b>构建</b></summary>

```bash
./gradlew build                 # 构建 jar
./gradlew runClient             # 启动客户端调试
./gradlew spotlessApply check   # 格式化 + Checkstyle + Spotless 校验
```

构建需要 `JAVA_HOME` 指向 JDK 21。

Forestry / CropsNH / GregTech 三个依赖是 `devOnlyNonPublishable`，仅用于编译，
不会被发布为运行时依赖。

</details>

<details>
<summary><b>注意事项</b></summary>

- 工业蜂箱的加速会连带影响 GT 的功率结算逻辑，调整大数值后建议观察耗电是否符合预期。
- `MixinTileEntityCropSticks` 依赖 CropsNH `updateEntity` 中 `256` 这一常量的唯一性。若后续版本在该方法中引入第二个 `256`，需要重新确认。
- `MixinMTEIndustrialApiary` 依赖 GT `checkRecipe` 中常量 `100` 的出现顺序（`ordinal = 0`），升级 GT 版本时需重新核对。
- `MixinPlugin` 通过 modid 精确匹配（`Forestry` / `cropsnh` / `gregtech`）决定是否加载对应 mixin，注意大小写。

</details>

## 许可证

[MIT](LICENSE)

---

<a id="en-us"></a>

<div align="center">

# English

</div>

## Features

<table>
<tr>
<td width="50%" align="center">

**MixinQueenWorkTick**

<sub>Forestry · `BeekeepingLogic`</sub>

Replaces the vanilla per-tick throttle increment of `1` with the configured value
Production triggers roughly every `ceil(550 / value)` ticks instead of a fixed 550 ticks

</td>
<td width="50%" align="center">

**MixinMTEIndustrialApiary**

<sub>GregTech · Industrial Apiary</sub>

Shrinks the `550.0f` cycle base constant by the configured value
and turns the internal `minTime = cycle / 100` into `/ 1`,
removing the 100-tick floor and eliminating an integer division-by-zero crash on short cycles

</td>
</tr>
<tr>
<td width="50%" align="center">

**MixinTileEntityCropSticks**

<sub>CropsNH · Crop Sticks</sub>

Substitutes the configured tick rate for the vanilla `TICK_RATE = 256`
Growth ticks proportionally more often

</td>
<td width="50%" align="center">

**MixinMTEIndustrialFarm**

<sub>CropsNH · Industrial Farm</sub>

Scales per-cycle growth progress by the same 256-tick base
Keeping the multiplier exactly consistent with the crop sticks

</td>
</tr>
</table>

Every mixin is **loaded conditionally by modid**: when the target mod is absent, that mixin is never applied and
startup stays unaffected.

## Requirements

| Item | Requirement | Note |
|:---|:---|:---|
| Minecraft | `1.7.10` | Target version |
| Forge | `10.13.4.1614` | Target version |
| UniMixins | **Required** | The mod relies on Mixins |
| Java | `JDK 21` | Build only, verified working |
| Forestry | Optional | Missing it disables `MixinQueenWorkTick` |
| CropsNH | Optional | Missing it disables both crop mixins |
| GregTech | Optional | Missing it disables `MixinMTEIndustrialApiary` |

## Configuration

The config file lives at `config/darkmodifier.cfg`, with all entries under the `general` category.

| Key | Default | Range | Description |
|:---|:---|:---|:---|
| `queenWorkCycleThrottleIncrement` | `55` | `1` – `550` | Queen throttle increment. **Applies to both Forestry bee housings and the GT industrial apiary** (shared value). `1` matches vanilla; higher is faster |
| `cropMixinTickRate` | `16` | `1` – `256` | Crop base period in ticks. `256` matches vanilla; lower is faster, and the default `16` is roughly 16x vanilla |

Two ways to edit:

- **In-game GUI**: Main menu → `Mods` → select `DarkModifier` → `Mod Options` at the bottom left (applies instantly)
- **Manual editing**: changes take effect within ~5 seconds after saving the cfg, no restart needed

## Keybinding

Main menu → `Options` → `Controls`, rebindable under the `DarkModifier` category.

| Action | Default key | Notes |
|:---|:---|:---|
| Toggle speedups | `'` (apostrophe) | Disabling restores vanilla production speeds for bees and crops; press again to re-enable. Runtime-only — the mod starts enabled after a restart. The current state is announced in chat on each toggle |

<details>
<summary><b>Installation</b></summary>

1. Make sure **UniMixins** is installed
2. Drop the built jar into your `mods/` folder
3. Launch once; the config file appears at `config/darkmodifier.cfg`

</details>

<details>
<summary><b>Building</b></summary>

```bash
./gradlew build                 # Build the jar
./gradlew runClient             # Launch a client for testing
./gradlew spotlessApply check   # Formatting + Checkstyle + Spotless verification
```

A `JAVA_HOME` pointing to JDK 21 is required.

Forestry / CropsNH / GregTech are declared as `devOnlyNonPublishable`, meaning they are compile-only and are not
published as runtime dependencies.

</details>

<details>
<summary><b>Caveats</b></summary>

- Speeding up the industrial apiary feeds back into GregTech's power calculation. If you push the value high, check
  whether the EU draw still matches expectations.
- `MixinTileEntityCropSticks` relies on `256` being a unique constant inside CropsNH's `updateEntity`. If a future
  CropsNH version introduces a second `256` there, it needs to be re-checked.
- `MixinMTEIndustrialApiary` relies on the position of the `100` constant (`ordinal = 0`) inside GT's `checkRecipe`.
  Re-verify it after upgrading GregTech.
- `MixinPlugin` decides whether to load each mixin by exact modid match (`Forestry` / `cropsnh` / `gregtech`); the
  casing matters.

</details>

## License

[MIT](LICENSE)

---

<div align="center">

[![Back to top](https://img.shields.io/badge/Back%20to%20top-DarkModifier-6C4AB6?style=for-the-badge)](#top)

</div>
