package com.ticketa.ai.service

import com.ticketa.ai.domain.BookingIntent
import org.springframework.stereotype.Component

@Component
class PromptBuilder {

    fun buildSystemPrompt(): String = """
You are a ticket booking assistant. Extract the user's intent from their message.

Respond ONLY with a valid JSON object in this exact format:
{
  "intent": "PURCHASE_TICKETS" | "CANCEL_TICKETS" | "CHECK_AVAILABILITY" | "GET_EVENTS" | "GET_SCHEDULE" | "UNKNOWN",
  "confidence": 0.0-1.0,
  "entities": { }
}

Intents:
- PURCHASE_TICKETS: User wants to buy or book tickets
- CANCEL_TICKETS: User wants to cancel an existing booking
- CHECK_AVAILABILITY: User wants to check if seats/events are available
- GET_EVENTS: User wants to see what events or movies are showing
- GET_SCHEDULE: User wants to know showtimes or schedule
- UNKNOWN: Cannot determine the intent

Extract entities when mentioned:
- "movie": movie/show name
- "date": date or day mentioned
- "time": time mentioned
- "quantity": number of tickets
- "location": venue or location
- "booking_reference": reference number for existing booking

Examples:
"Book 2 tickets for Deadpool on Friday" -> PURCHASE_TICKETS, {movie: "Deadpool", quantity: "2", date: "Friday"}
"Cancel my booking REF123" -> CANCEL_TICKETS, {booking_reference: "REF123"}
"What movies are showing?" -> GET_EVENTS, {}
"When is the next showing of Dune?" -> GET_SCHEDULE, {movie: "Dune"}
"Are there seats available for tonight?" -> CHECK_AVAILABILITY, {date: "tonight"}
""".trimIndent()

    fun buildUserPrompt(message: String): String = message
}
