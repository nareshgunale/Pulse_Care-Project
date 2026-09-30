package com.ai_service.agent;

import org.springframework.stereotype.Component;

/**
 * UserContext stores the authenticated user's ID for the current request.
 * - A utility class that keeps the logged-in user's ID accessible throughout the request.
 * <p>
 * Why ?
 * - Without UserContext, the user ID would need to be passed through every controller,
 * service, and tool method, making the code repetitive and harder to maintain.
 * - It provides a single place to access the current user's ID.
 * <p>
 * How work?
 * - After authentication, the user's ID is stored in ThreadLocal.
 * - Any class handling the same request can retrieve the ID using getUserId().
 * - When the request finishes, the ID is removed to prevent memory leaks.
 * <p>
 * What is ThreadLocal?
 * - ThreadLocal is a Java class that stores data separately for each thread (Separate context for every thread).
 * - Each request in a Spring Boot application is usually processed by its own
 * thread, so every user gets their own independent value.
 * - One user's data cannot be accessed by another user's request.
 */

@Component
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long id) {
        USER_ID.set(id);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}