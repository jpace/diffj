package org.incava.diffj.util;

import org.incava.ijdk.text.Message;

public class Messages {
    private final Message addedMsg;
    private final Message changedMsg;
    private final Message deletedMsg;

    public Messages(Message addedMsg, Message changedMsg, Message deletedMsg) {
        this.addedMsg = addedMsg;
        this.changedMsg = changedMsg;
        this.deletedMsg = deletedMsg;
    }

    public Message getAdded() {
        return addedMsg;
    }

    public Message getChanged() {
        return changedMsg;
    }

    public Message getDeleted() {
        return deletedMsg;
    }
}
