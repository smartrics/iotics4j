package smartrics.iotics.host.wrappers;

import com.iotics.api.*;
import io.grpc.stub.StreamObserver;

/**
 * Internalised interface of IOTICS Inputs API gRPC Service
 */
public interface InputAPIFuture {

    void receiveInputMessages(ReceiveInputMessageRequest request, StreamObserver<ReceiveInputMessageResponse> responseObserver);

    void describeInput(DescribeInputRequest request, StreamObserver<DescribeInputResponse> observer);

    void createInput(CreateInputRequest request, StreamObserver<CreateInputResponse> observer);

    void deleteInput(DeleteInputRequest request, StreamObserver<DeleteInputResponse> observer);
}
