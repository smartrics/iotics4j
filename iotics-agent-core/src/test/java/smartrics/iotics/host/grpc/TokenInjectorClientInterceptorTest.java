package smartrics.iotics.host.grpc;
import io.grpc.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import smartrics.iotics.host.grpc.token.TokenScheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TokenInjectorClientInterceptorTest {
    private TokenInjectorClientInterceptor interceptor;

    private static String TEST_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        TokenScheduler scheduler = new TokenScheduler() {
            @Override
            public String validToken() {
                return TEST_TOKEN;
            }

            @Override
            public void schedule() {
                // Implement scheduling logic if necessary for testing
            }

            @Override
            public void cancel() {
                // Implement cancel logic if necessary for testing
            }
        };
        interceptor = new TokenInjectorClientInterceptor(scheduler);
    }

    @Test
    void interceptCallAddsAuthorizationHeader() {
        MethodDescriptor<String, String> methodDescriptor = MethodDescriptor.<String, String>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName("service", "method"))
                .setRequestMarshaller(newStringRequestMarshaller())
                .setResponseMarshaller(newStringResponseMarshaller())
                .build();

        CallOptions callOptions = CallOptions.DEFAULT;
        ClientCall<String, String> clientCall = newClientCall(methodDescriptor, callOptions);
        ClientCall.Listener<String> noOpListener = new ClientCall.Listener<>() {};
        Metadata headers = new Metadata();
        clientCall.start(noOpListener, headers);

        Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
        String headerValue = headers.get(AUTHORIZATION_KEY);

        assertEquals("bearer " + TEST_TOKEN, headerValue);
    }

    @NotNull
    private ClientCall<String, String> newClientCall(MethodDescriptor<String, String> methodDescriptor, CallOptions callOptions) {
        Channel channel = new Channel() {

            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
                return new ClientCall<>() {
                    Metadata capturedHeaders;

                    @Override
                    public void start(Listener<ResponseT> listener, Metadata metadata) {
                        this.capturedHeaders = metadata;
                    }

                    @Override
                    public void request(int i) {

                    }

                    @Override
                    public void cancel(@Nullable String s, @Nullable Throwable throwable) {

                    }

                    @Override
                    public void halfClose() {

                    }

                    @Override
                    public void sendMessage(RequestT requestT) {

                    }
                };
            }

            @Override
            public String authority() {
                return "localhost";
            }
        };
        return interceptor.interceptCall(methodDescriptor, callOptions, channel);
    }

    private MethodDescriptor.Marshaller<String> newStringResponseMarshaller() {
        return new io.grpc.MethodDescriptor.Marshaller<>() {
            @Override
            public InputStream stream(String value) {
                return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String parse(InputStream stream) {
                try {
                    return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private MethodDescriptor.Marshaller<String> newStringRequestMarshaller() {
        return new io.grpc.MethodDescriptor.Marshaller<>() {
            @Override
            public InputStream stream(String value) {
                return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String parse(InputStream stream) {
                try {
                    return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
