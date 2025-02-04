package io.vertx.example.grpc.conversation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.SocketAddress;
import io.vertx.example.grpc.ConversationalServiceGrpc;
import io.vertx.example.grpc.Messages;
import io.vertx.example.util.Runner;
import io.vertx.grpc.client.GrpcClient;

/*
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class Client extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(Client.class);
  }

  @Override
  public void start() {

    // Create the client
    GrpcClient client = GrpcClient.client(vertx);

    // Call the remote service
    client.request(SocketAddress.inetSocketAddress(8080, "localhost"), ConversationalServiceGrpc.getFullDuplexCallMethod())
      .compose(
        request -> {
          // start the conversation
          request.write(Messages.StreamingOutputCallRequest.newBuilder().build());
          vertx.setTimer(500L, t -> {
            request.write(Messages.StreamingOutputCallRequest.newBuilder().build());
          });
          return request.response();
        }
      ).compose(resp -> {
        System.out.println("Client: received response");
        return resp.end();
      });
  }
}
