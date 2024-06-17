package com.sparta.newspeed.entity;

public enum UserStatusEnum {
    VERIFIED(Authority.VERIFIED),
    NOTVERIFIED(Authority.NOTVERIFIED),
    WITHDREW(Authority.WITHDREW);

    private String status;

    UserStatusEnum(String status) {
        this.status = status;

    }

    public String getStatus() {
        return this.status;
    }

    public static class Authority {
        private static final String VERIFIED = "STATUS_인증 전";
        private static final String NOTVERIFIED = "STATUS_인증 후";
        private static final String WITHDREW = "STATUS_WITHDREW";

    }
}

