package com.adeem.stockflow.service;

import io.sentry.Breadcrumb;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Service for interacting with Sentry error tracking system.
 * Provides methods for exception tracking, message logging, and context management.
 */
@Service
public class SentryService {

    /**
     * Captures an exception and sends it to Sentry.
     *
     * @param exception the exception to be captured
     */
    public void captureException(Exception exception) {
        Sentry.captureException(exception);
    }

    /**
     * Captures an exception with additional context information and sends it to Sentry.
     *
     * @param exception the exception to be captured
     * @param extra     a map of additional information to be included with the exception
     */
    public void captureException(Exception exception, Map<String, Object> extra) {
        Sentry.withScope(scope -> {
            extra.forEach((key, value) -> scope.setExtra(key, (String) value));
            Sentry.captureException(exception);
        });
    }

    /**
     * Captures a message with specified severity level and sends it to Sentry.
     *
     * @param message the message to be captured
     * @param level   the severity level of the message
     */
    public void captureMessage(String message, SentryLevel level) {
        Sentry.captureMessage(message, level);
    }

    /**
     * Adds a breadcrumb to track user actions or application events.
     * Breadcrumbs are stored with INFO level by default.
     *
     * @param message  the breadcrumb message
     * @param category the category of the breadcrumb
     */
    public void addBreadcrumb(String message, String category) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setMessage(message);
        breadcrumb.setCategory(category);
        breadcrumb.setLevel(SentryLevel.INFO);
        Sentry.addBreadcrumb(breadcrumb);
    }

    /**
     * Sets user information for the current scope in Sentry.
     *
     * @param userId   the unique identifier of the user
     * @param email    the email address of the user
     * @param username the username of the user
     */
    public void setUser(String userId, String email, String username) {
        Sentry.configureScope(scope -> {
            User user = new User();
            user.setId(userId);
            user.setEmail(email);
            user.setUsername(username);
            scope.setUser(user);
        });
    }

    /**
     * Sets a tag for the current scope in Sentry.
     * Tags are key-value pairs that are searchable in Sentry.
     *
     * @param key   the tag key
     * @param value the tag value
     */
    public void setTag(String key, String value) {
        Sentry.configureScope(scope -> scope.setTag(key, value));
    }

    /**
     * Sets additional context information for the current scope in Sentry.
     *
     * @param key     the context identifier
     * @param context a map of context information
     */
    public void setContext(String key, Map<String, Object> context) {
        Sentry.configureScope(scope -> scope.setContexts(key, context));
    }
}
