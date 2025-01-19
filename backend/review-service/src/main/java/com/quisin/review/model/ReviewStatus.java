package com.quisin.review.model;

public enum ReviewStatus {
    PENDING,      // Awaiting moderation
    APPROVED,     // Approved and visible
    REJECTED,     // Rejected by moderator
    FLAGGED,      // Flagged for review
    REMOVED,      // Removed after being approved
    UPDATED       // Modified after initial approval
} 