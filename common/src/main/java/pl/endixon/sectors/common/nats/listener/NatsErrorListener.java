/*
 *
 * EndSectors – Non-Commercial License
 * (c) 2025 Endixon
 *
 * Permission is granted to use, copy, and
 * modify this software **only** for personal
 * or educational purposes.
 *
 * Commercial use, redistribution, claiming
 * this work as your own, or copying code
 * without explicit permission is strictly
 * prohibited.
 *
 * Visit https://github.com/Endixon/EndSectors
 * for more info.
 *
 */

package pl.endixon.sectors.common.nats.listener;

import io.nats.client.Connection;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import io.nats.client.JetStreamSubscription;
import pl.endixon.sectors.common.util.LoggerUtil;


public final class NatsErrorListener implements ErrorListener {

    @Override
    public void errorOccurred(Connection connection, String error) {
        LoggerUtil.error("NATS generic error occurred: " + error);
    }

    @Override
    public void exceptionOccurred(Connection connection, Exception exception) {
        LoggerUtil.error("NATS internal exception: " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void slowConsumerDetected(Connection connection, Consumer consumer) {
        LoggerUtil.warn("NATS Slow Consumer detected! Check your message processing speed.");

        if (consumer instanceof JetStreamSubscription) {
            LoggerUtil.warn("Slow consumer on JetStream subject: " + ((JetStreamSubscription) consumer).getSubject());
        }
    }

    @Override
    public void messageDiscarded(Connection connection, io.nats.client.Message message) {
        LoggerUtil.warn("NATS message discarded on subject: " + message.getSubject());
    }

    @Override
    public void heartbeatAlarm(Connection connection, JetStreamSubscription subscription, long lastStreamSequence, long lastConsumerSequence) {
        LoggerUtil.error("NATS Heartbeat alarm for subscription on: " + subscription.getSubject());
    }

    @Override
    public void unhandledStatus(Connection connection, io.nats.client.JetStreamSubscription subscription, io.nats.client.support.Status status) {
        LoggerUtil.warn("NATS Unhandled status: " + status.getMessage());
    }
}