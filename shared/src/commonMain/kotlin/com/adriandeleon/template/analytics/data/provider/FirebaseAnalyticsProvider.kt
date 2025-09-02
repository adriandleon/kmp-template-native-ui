package com.adriandeleon.template.analytics.data.provider

import com.adriandeleon.template.analytics.domain.AnalyticsEvent
import com.adriandeleon.template.analytics.domain.AnalyticsProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics

/**
 * Firebase Analytics provider implementation for tracking analytics events.
 *
 * This provider integrates with Firebase Analytics to track user events and app behavior. It uses
 * the GitLive Firebase Kotlin SDK for cross-platform compatibility, allowing the same analytics
 * code to work on both Android and iOS platforms.
 *
 * **Features:**
 * - Cross-platform Firebase Analytics integration
 * - Automatic event parameter conversion
 * - Error handling and graceful failure
 * - Real-time event tracking
 *
 * **Prerequisites:**
 * - Firebase project configured with Analytics enabled
 * - `google-services.json` (Android) and `GoogleService-Info.plist` (iOS) files
 * - Firebase Analytics initialized in the app
 *
 * @see AnalyticsProvider for the base interface
 * @see AnalyticsEvent for event definitions
 * @example
 *
 * ```kotlin
 * // The provider is automatically configured in AnalyticsModule
 * val analytics: Analytics = koinInject()
 *
 * // Track events - they will be sent to Firebase Analytics
 * analytics.track(
 *     CommonAnalyticsEvent.ScreenView(
 *         screenName = "HomeScreen",
 *         screenClass = "HomeComponent"
 *     )
 * )
 *
 * // Custom events are also supported
 * analytics.track(
 *     CustomAnalyticsEvent(
 *         name = "user_preference_changed",
 *         parameters = mapOf(
 *             "preference_key" to "theme",
 *             "new_value" to "dark"
 *         )
 *     )
 * )
 * ```
 */
internal class FirebaseAnalyticsProvider : AnalyticsProvider {

    /**
     * Firebase Analytics instance for cross-platform event tracking.
     *
     * This instance is automatically initialized using the GitLive Firebase SDK, which provides a
     * unified API for both Android and iOS platforms.
     */
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    /**
     * Tracks an analytics event through Firebase Analytics.
     *
     * This method converts the generic [AnalyticsEvent] to Firebase Analytics format and sends it
     * to Firebase. The event parameters are automatically converted to the appropriate Firebase
     * Analytics parameter types.
     *
     * **Parameter Conversion:**
     * - String values are sent as-is
     * - Numeric values (Int, Long, Double, Float) are converted to appropriate types
     * - Boolean values are converted to strings
     * - Other types are converted to strings using toString()
     *
     * **Error Handling:** This method handles errors gracefully and does not propagate exceptions
     * to avoid breaking the main application flow.
     *
     * @param event The analytics event to track. Must not be null.
     * @example
     *
     * ```kotlin
     * val event = CommonAnalyticsEvent.ButtonClick("LoginButton")
     * firebaseProvider.track(event)
     * // Event will appear in Firebase Analytics as "button_click" with parameter "button_name"
     * ```
     */
    override fun track(event: AnalyticsEvent) {
        try {
            // Convert parameters to Firebase Analytics format
            val firebaseParameters =
                event.parameters.mapValues { (_, value) ->
                    when (value) {
                        is String -> value
                        is Int -> value.toLong()
                        is Long -> value
                        is Double -> value
                        is Float -> value.toDouble()
                        is Boolean -> value.toString()
                        else -> value.toString()
                    }
                }

            firebaseAnalytics.logEvent(event.name, firebaseParameters)
        } catch (e: Exception) {
            // Log error but don't propagate to avoid breaking the app
            // In a production app, you might want to use a proper logging framework
            println("Firebase Analytics failed to track event: ${event.name}, error: ${e.message}")
        }
    }
}
