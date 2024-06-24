package com.neolife.devfine.core.network

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.regex.Pattern

object Utils {
    fun parseMarkdown(markdown: String): String {
        // Extract the first paragraph
        val paragraphs = markdown.lines()
        var firstParagraph = paragraphs.first()
        val imagePattern = Pattern.compile("(!\\[.*?\\]\\((.*?)\\))")

        firstParagraph = imagePattern.matcher(firstParagraph).replaceAll("")
        val spacedWords = firstParagraph.split(" ")
        val first30Words = spacedWords.take(30)
        firstParagraph = if (first30Words.size < spacedWords.size) {
            first30Words.dropLastWhile { it.endsWith(".") || it.endsWith(",") }.joinToString(" ").take(260) + "..."
        } else {
            first30Words.joinToString(" ").take(250)
        }
        val imageMatcher = imagePattern.matcher(markdown)
        if (imageMatcher.find()) {
            val firstImage = imageMatcher.group(1)
            return firstParagraph.trim() + "\n\n" + firstImage
        } else {
            return firstParagraph
        }
    }

    fun checkPassword(password: String): Boolean {
        val minLength = 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { it.isLetterOrDigit().not() }
        return password.length >= minLength && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar

    }

    fun TimeOrDate(timestamp: String): String {
        val currentDate = LocalDate.now()
        val parsedTimestamp = Instant.parse(timestamp)
        val timestampDate = parsedTimestamp.atZone(ZoneId.systemDefault()).toLocalDate()

        if (currentDate == timestampDate) {
            // Display the time if it's the same day
            val timeString = parsedTimestamp.atZone(ZoneId.systemDefault())
                .toLocalTime()
                .toString()
                .substring(0, 5)
            return "сегодня в " + timeString
        } else {
            // Display the date if it's not the same day
            val dateString = timestampDate.toString().replace("-",".")
            return dateString
        }
    }
}