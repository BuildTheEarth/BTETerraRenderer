package com.mndk.bteterrarenderer.core.projection;

import com.mndk.bteterrarenderer.core.network.ServerWelcomeMessage;
import com.mndk.bteterrarenderer.dep.terraplusplus.projection.GeographicProjection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ServerWelcomeMessageTest {

    @Test
    public void givenWelcomeMessage_testParsable() throws IOException {
        ByteBuf buf = Unpooled.buffer();
        ServerWelcomeMessage ongoingMessage = new ServerWelcomeMessage(Projections.BTE);
        ongoingMessage.toBytes(buf);

        ServerWelcomeMessage incomingMessage = new ServerWelcomeMessage();
        incomingMessage.fromBytes(buf);
        GeographicProjection projection = incomingMessage.getProjection();

        GeographicProjectionTest.validateBTEProjection(projection);
    }

}
