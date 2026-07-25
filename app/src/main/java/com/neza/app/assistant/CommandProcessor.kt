package com.neza.app.assistant

import android.content.Context

sealed class NezaCommand {
    data class OpenApp(val appQuery: String) : NezaCommand()
    data class CallContact(val contactQuery: String) : NezaCommand()
    data class Unrecognized(val rawText: String) : NezaCommand()
}

/**
 * Very lightweight rule-based parser for voice commands like:
 * "open whatsapp", "whatsapp kholo", "call Rahul", "Rahul ko call karo"
 * Extend this with more phrases/languages as needed.
 */
object CommandProcessor {

    private val openPatterns = listOf("open ", "launch ", "kholo", "khol do", "start ")
    private val callPatterns = listOf("call ", "phone karo ", "ko call karo")

    fun parse(rawText: String): NezaCommand {
        val text = rawText.trim().lowercase()

        // "call X" / "X ko call karo" / "phone karo X"
        if (text.startsWith("call ")) {
            return NezaCommand.CallContact(text.removePrefix("call ").trim())
        }
        if (text.endsWith("ko call karo") || text.endsWith("ko phone karo")) {
            val name = text.substringBefore(" ko ")
            return NezaCommand.CallContact(name.trim())
        }
        if (text.startsWith("phone karo ")) {
            return NezaCommand.CallContact(text.removePrefix("phone karo ").trim())
        }

        // "open X" / "X kholo" / "X khol do"
        if (text.startsWith("open ") || text.startsWith("launch ") || text.startsWith("start ")) {
            val name = openPatterns.fold(text) { acc, p -> acc.removePrefix(p) }
            return NezaCommand.OpenApp(name.trim())
        }
        if (text.endsWith("kholo") || text.endsWith("khol do")) {
            val name = text.substringBeforeLast("kholo").substringBeforeLast("khol do")
            return NezaCommand.OpenApp(name.trim())
        }

        return NezaCommand.Unrecognized(rawText)
    }

    /**
     * Executes a parsed command. Returns a short human-readable result message.
     */
    fun execute(context: Context, command: NezaCommand): String = when (command) {
        is NezaCommand.OpenApp -> {
            val opened = AppLauncher.openAppByName(context, command.appQuery)
            if (opened) "Opening ${command.appQuery}" else "I couldn't find an app called '${command.appQuery}'"
        }
        is NezaCommand.CallContact -> {
            val contact = CallHelper.findContact(context, command.contactQuery)
            if (contact != null) {
                CallHelper.callContact(context, contact.phoneNumber)
                "Calling ${contact.name}"
            } else {
                "I couldn't find a contact matching '${command.contactQuery}'"
            }
        }
        is NezaCommand.Unrecognized -> "" // caller should route this to the AI chat instead
    }
}
