package org.container.platform.web.ui.common;

import lombok.Data;

@Data
public class ActiveStatus {

    boolean isActive = false;
    String message = Constants.EMPTY_VALUE;

    public ActiveStatus() {
    }

    public ActiveStatus(boolean isActive, String message) {
        this.isActive = isActive;
        this.message = message;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
