package com.kaizen.ai.assistant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

object CallHelper {

    data class ContactMatch(val name: String, val phoneNumber: String)

    private fun hasPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * Searches the device contacts for a name matching [query] and returns the best match, if any.
     */
    fun findContact(context: Context, query: String): ContactMatch? {
        if (!hasPermission(context, Manifest.permission.READ_CONTACTS)) return null
        val target = query.trim().lowercase()
        if (target.isEmpty()) return null

        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
        ) ?: return null

        var best: ContactMatch? = null
        cursor.use {
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIdx) ?: continue
                val number = it.getString(numberIdx) ?: continue
                if (name.lowercase() == target) {
                    return ContactMatch(name, number) // exact match wins immediately
                }
                if (best == null && name.lowercase().contains(target)) {
                    best = ContactMatch(name, number)
                }
            }
        }
        return best
    }

    /**
     * Places a direct call. Requires CALL_PHONE permission to be already granted;
     * falls back to the dialer (ACTION_DIAL, no permission needed) otherwise.
     */
    fun callContact(context: Context, phoneNumber: String) {
        val intent = if (hasPermission(context, Manifest.permission.CALL_PHONE)) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
