package com.skillbox.auth.dto.events;


public class ResetPasswordEvent {
    private ResetPassword resetPassword;

    public ResetPasswordEvent() {
    }

    public ResetPasswordEvent(ResetPassword resetPassword) {
        this.resetPassword = resetPassword;
    }

    public ResetPassword getResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(ResetPassword resetPassword) {
        this.resetPassword = resetPassword;
    }
}
