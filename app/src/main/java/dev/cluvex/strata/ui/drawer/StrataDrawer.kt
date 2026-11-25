package dev.cluvex.strata.ui.drawer

import android.os.StatFs
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.EnhancedEncryption
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import dev.cluvex.strata.R
import dev.cluvex.strata.core.bookmarks.Bookmark
import dev.cluvex.strata.core.remote.RemoteConnection
import dev.cluvex.strata.core.vault.VaultStore
import dev.cluvex.strata.core.remote.RemoteProtocol
import dev.cluvex.strata.core.util.Formatters
import dev.cluvex.strata.core.vault.Vault
import kotlin.math.roundToInt

class DrawerActions(
    val onShortcut: (String) -> Unit,
    val onBookmark: (Bookmark) -> Unit,
    val onRecycleBin: () -> Unit,
    val onApps: () -> Unit,
    val onNetwork: () -> Unit,
    val onStorage: (RemoteConnection) -> Unit,
    val onLanguages: () -> Unit,
    val onSettings: () -> Unit,
    val onShare: () -> Unit,
    val onColorPicker: () -> Unit,
    val onSigningKeys: () -> Unit,
    val onPlugins: () -> Unit,
    val onVault: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StrataDrawerContent(
    root: String,
    rootMode: Boolean,
    bookmarks: List<Bookmark>,
    storages: List<RemoteConnection>,
    open: Boolean,
    closed: Boolean,
    actions: DrawerActions,
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp, bottom = 16.dp)
            .testTag("strata-drawer"),
    ) {
        Brand(actions.onSettings, Modifier)

        // --- STORAGES SECTION ---
        SectionTitle(stringResource(R.string.drawer_system), Modifier)
        DenseStorageCard(
            title = stringResource(R.string.model_start_home),
            path = root,
            icon = Icons.Rounded.Smartphone,
            modifier = Modifier,
            onClick = { actions.onShortcut(root) },
        )

        val rootLabel = stringResource(R.string.drawer_root)
        val dataLabel = stringResource(R.string.drawer_data)
        val systemLabel = stringResource(R.string.drawer_system_folder)
        val systemMounts = remember(rootMode, rootLabel, dataLabel, systemLabel) {
            buildList {
                add(DenseMount(rootLabel, "/", Icons.Rounded.Folder) { actions.onShortcut("/") })
                if (rootMode) {
                    add(DenseMount(dataLabel, "/data", Icons.Rounded.Storage) { actions.onShortcut("/data") })
                    add(DenseMount(systemLabel, "/system", Icons.Rounded.Memory) { actions.onShortcut("/system") })
                }
            }
        }
        if (systemMounts.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            DenseItemList(
                items = systemMounts.map { mount ->
                    DenseItemSpec(
                        title = mount.name,
                        subtitle = mount.path,
                        icon = mount.icon,
                        onClick = mount.onClick,
                    )
                },
                shape = MaterialShapes.Cookie6Sided,
                modifier = Modifier,
            )
        }

        // --- BOOKMARKS SECTION ---
        if (bookmarks.isNotEmpty()) {
            SectionTitle(stringResource(R.string.common_bookmarks), Modifier)
            DenseItemList(
                items = bookmarks.map { b ->
                    DenseItemSpec(
                        title = b.name,
                        subtitle = b.path,
                        icon = Icons.Rounded.Bookmark,
                        iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = { actions.onBookmark(b) },
                    )
                },
                shape = MaterialShapes.Cookie4Sided,
                modifier = Modifier,
            )
        }

        // --- NETWORK SECTION ---
        SectionTitle(stringResource(R.string.remote_title), Modifier)
        val manageLabel = stringResource(R.string.drawer_manage_storages)
        val sharingLabel = stringResource(R.string.drawer_wifi_sharing)
        val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
        val networkItems = remember(storages, manageLabel, sharingLabel, secondaryContainerColor) {
            buildList {
                add(
                    DenseItemSpec(
                        title = manageLabel,
                        subtitle = "Configure remote connections",
                        icon = Icons.Rounded.Cloud,
                        onClick = actions.onNetwork,
                    ),
                )
                storages.forEach { c ->
                    add(
                        DenseItemSpec(
                            title = c.name.ifBlank { c.host },
                            subtitle = c.displayAddress,
                            icon = getProtocolIcon(c.protocol),
                            iconContainerColor = secondaryContainerColor,
                            onClick = { actions.onStorage(c) },
                        ),
                    )
                }
                add(
                    DenseItemSpec(
                        title = sharingLabel,
                        subtitle = "FTP / Browser sharing",
                        icon = Icons.Rounded.Wifi,
                        onClick = actions.onShare,
                    ),
                )
            }
        }
        DenseItemList(
            items = networkItems,
            shape = MaterialShapes.Clover4Leaf,
            modifier = Modifier,
        )

        // --- TOOLS SECTION ---
        SectionTitle(stringResource(R.string.common_tools), Modifier)
        DenseItemList(
            items = listOf(
                DenseItemSpec(stringResource(R.string.drawer_languages), "Termux CLI & Runners", Icons.Rounded.Terminal, onClick = actions.onLanguages),
                DenseItemSpec(stringResource(R.string.apps_title), "Installed application manager", Icons.Rounded.Apps, onClick = actions.onApps),
                DenseItemSpec(stringResource(R.string.color_picker_title), "Color tools & palettes", Icons.Rounded.Palette, onClick = actions.onColorPicker),
                DenseItemSpec(stringResource(R.string.keys_title), "APK signing keystores", Icons.Rounded.Key, onClick = actions.onSigningKeys),
                DenseItemSpec(stringResource(R.string.plugin_title), "Extensions & plugins", Icons.Rounded.Extension, onClick = actions.onPlugins),
            ),
            shape = MaterialShapes.Cookie9Sided,
            modifier = Modifier,
        )

        Spacer(Modifier.height(16.dp))
        VaultCard(actions.onVault, Modifier)

        Spacer(Modifier.height(8.dp))
        DenseItemList(
            items = listOf(
                DenseItemSpec(
                    title = stringResource(R.string.common_recycle_bin),
                    subtitle = "Deleted files & recovery",
                    icon = Icons.Rounded.DeleteSweep,
                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                    iconContentColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = actions.onRecycleBin,
                ),
            ),
            shape = MaterialShapes.Cookie4Sided,
            modifier = Modifier,
        )
    }
}

private class DenseMount(val name: String, val path: String, val icon: ImageVector, val onClick: () -> Unit)

private class DenseItemSpec(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconContainerColor: Color? = null,
    val iconContentColor: Color? = null,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Brand(onSettings: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().padding(start = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        StrataMark(Modifier.size(36.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text("Strata", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(stringResource(R.string.drawer_tagline), style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.common_settings), tint = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun StrataMark(modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val layers = listOf(colors.secondary, colors.tertiary, colors.primary)
    Canvas(modifier) {
        val h = size.height / 4.2f
        val gap = (size.height - 3 * h) / 2f
        layers.forEachIndexed { i, c ->
            val inset = size.width * (0.16f - i * 0.05f)
            drawRoundRect(
                color = c,
                topLeft = Offset(inset, i * (h + gap)),
                size = Size(size.width - inset * 2f, h),
                cornerRadius = CornerRadius(h / 2f),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DenseStorageCard(
    title: String,
    path: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val usage = remember(path) { runCatching { StatFs(path).let { it.availableBytes to it.totalBytes } }.getOrNull() }
    val usedFraction = usage?.let { (free, total) -> if (total > 0) (1f - free.toFloat() / total).coerceIn(0f, 1f) else 0f } ?: 0f

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = colors.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth().testTag("drawer-storage"),
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape())
                        .background(colors.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (usage != null && usage.second > 0) {
                        Text(
                            stringResource(R.string.drawer_free_of, Formatters.size(usage.first), Formatters.size(usage.second)),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else {
                        Text(path, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.ui_percent, (usedFraction * 100).roundToInt()),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { usedFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = colors.primary,
                trackColor = colors.surfaceContainerHighest,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DenseItemList(
    items: List<DenseItemSpec>,
    shape: RoundedPolygon,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
        items.forEachIndexed { i, spec ->
            SegmentedListItem(
                onClick = spec.onClick,
                shapes = ListItemDefaults.segmentedShapes(i, items.size),
                colors = ListItemDefaults.segmentedColors(containerColor = colors.surfaceBright),
                leadingContent = {
                    val containerColor = spec.iconContainerColor ?: colors.secondaryContainer
                    val contentColor = spec.iconContentColor ?: colors.onSecondaryContainer
                    Box(
                        Modifier.size(32.dp).clip(shape.toShape()).background(containerColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(spec.icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
                    }
                },
                supportingContent = {
                    Text(
                        spec.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            ) {
                Text(
                    spec.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun getProtocolIcon(protocol: RemoteProtocol): ImageVector = when (protocol) {
    RemoteProtocol.FTP, RemoteProtocol.FTPS -> Icons.Rounded.CloudQueue
    RemoteProtocol.SFTP -> Icons.Rounded.Security
    RemoteProtocol.SMB -> Icons.Rounded.Router
    RemoteProtocol.WEBDAV, RemoteProtocol.WEBDAVS -> Icons.Rounded.Cloud
    RemoteProtocol.SAF -> Icons.Rounded.FolderSpecial
    RemoteProtocol.VAULT -> Icons.Rounded.EnhancedEncryption
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SectionTitle(text: String, modifier: Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 8.dp, top = 14.dp, bottom = 6.dp).semantics { heading() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun VaultCard(onClick: () -> Unit, modifier: Modifier) {
    val state by remember { if (Vault.ready) Vault.store.state else kotlinx.coroutines.flow.MutableStateFlow(VaultStore.State.NONE) }.collectAsState()
    val open = state == VaultStore.State.UNLOCKED
    val shape = remember { RoundedCornerShape(20.dp) }
    Surface(
        onClick = onClick,
        shape = shape,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier.fillMaxWidth().testTag("drawer-vault"),
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(MaterialShapes.Cookie7Sided.toShape())
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (open) Icons.Rounded.LockOpen else Icons.Rounded.EnhancedEncryption,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.vault_drawer), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
        }
    }
}
