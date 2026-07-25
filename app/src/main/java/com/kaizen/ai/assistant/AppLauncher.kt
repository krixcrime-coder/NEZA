package com.kaizen.ai.assistant

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

data class InstalledApp(
    val label: String,
    val packageName: String
)

/**
 * Lists installed apps and launches any of them by (fuzzy) name.
 * Requires QUERY_ALL_PACKAGES (declared in the manifest) to see the full app list on API 30+.
 */
object AppLauncher {

    fun listInstalledApps(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        val apps: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return apps
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map { InstalledApp(label = pm.getApplicationLabel(it).toString(), packageName = it.packageName) }
            .sortedBy { it.label.lowercase() }
    }

    /**
     * Tries to find an installed app whose label contains [query] (case-insensitive),
     * and launches it. Returns true if an app was found and launched.
     */
    fun openAppByName(context: Context, query: String): Boolean {
        val target = query.trim().lowercase()
        if (target.isEmpty()) return false

        val apps = listInstalledApps(context)
        val match = apps.firstOrNull { it.label.lowercase() == target }
            ?: apps.firstOrNull { it.label.lowercase().contains(target) }
            ?: apps.firstOrNull { target.contains(it.label.lowercase()) }
            ?: return false

        val launchIntent = context.packageManager.getLaunchIntentForPackage(match.packageName) ?: return false
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        return true
    }
}
