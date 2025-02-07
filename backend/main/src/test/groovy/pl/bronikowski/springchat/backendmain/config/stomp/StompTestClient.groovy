package pl.bronikowski.springchat.backendmain.config.stomp

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.stereotype.Component
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto
import pl.bronikowski.springchat.backendmain.websocket.api.StompResponse
import pl.bronikowski.springchat.backendmain.websocket.internal.StompConstants

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Component
class StompTestClient {
    @Autowired
    ServletContext servletContext
    @Autowired
    ObjectMapper objectMapper
    @Autowired
    ServletWebServerApplicationContext server

    def connect(AbstractTestStompSessionHandler handler, int timeout = 5) {
        var contextPath = servletContext.getContextPath() ?: ""
        var url = "ws://localhost:${server.getWebServer().getPort()}${contextPath}/ws"
        var transports = [
                new WebSocketTransport(new StandardWebSocketClient()),
        ]
        var sockJsClient = new SockJsClient(transports)
        var stompClient = new WebSocketStompClient(sockJsClient)
        stompClient.messageConverter = new MappingJackson2MessageConverter(objectMapper)
        stompClient.connectAsync(url, handler).get(timeout, TimeUnit.SECONDS)
    }

    <T> T sendWithExpectedResponse(String sendDestination, String subscribeDestination, Object payload,
                                   Class<T> responseType, int timeout = 10) {
        var transactionId = UUID.randomUUID().toString()
        var latch = new CountDownLatch(1)
        var failure = new AtomicReference<Throwable>()
        var response = new AtomicReference<ChannelMessageBasicsDto>()
        var session = connect(new AbstractTestStompSessionHandler(failure) {})
        var handler = new SimpleStompFrameHandler(StompResponse.class, { headers, StompResponse responsePayload ->
            if (transactionId == responsePayload.transactionId()) {
                response.set(objectMapper.convertValue(responsePayload.data(), responseType))
                latch.countDown()
            }
        })
        session.subscribe(subscribeDestination, handler)
        var headers = new StompHeaders()
        headers.setDestination(sendDestination)
        headers.set(StompConstants.APP_TRANSACTION_HEADER, transactionId)
        session.send(headers, payload)
        latch.await(timeout, TimeUnit.SECONDS)
        session.disconnect()
        if (failure.get() !== null) {
            throw failure.get()
        }
        response.get()
    }
}
