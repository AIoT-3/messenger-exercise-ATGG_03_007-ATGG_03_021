/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2024. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.message.observer.impl;

import com.message.action.MessageAction;
import com.message.observer.Observer;
import com.message.subject.EventType;

public class MessageRecvObserver implements Observer {
    private final MessageAction messageAction;

    public MessageRecvObserver(MessageAction messageAction) {
        this.messageAction = messageAction;
    }

    @Override
    public EventType getEventType() {
        return EventType.RECV;
    }

    @Override
    public void updateMessage(Object message) {
        messageAction.execute(message);
    }

}
