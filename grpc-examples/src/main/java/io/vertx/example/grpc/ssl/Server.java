package io.vertx.example.grpc.ssl;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.VertxGreeterGrpc;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.example.util.Runner;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServiceBridge;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Server extends AbstractVerticle {

  public static void main(String[] args) {
    Runner.runExample(Server.class);
  }

  @Override
  public void start() {
    // Create the server
    GrpcServer rpcServer = GrpcServer.server(vertx);

    // The rpc service
    rpcServer.callHandler(GreeterGrpc.getSayHelloMethod(), request -> {
      request
        .last()
        .onSuccess(msg -> {
          System.out.println("Hello " + msg.getName());
          request.response().end(HelloReply.newBuilder().setMessage(msg.getName()).build());
        });
    });

    // start the server
    HttpServerOptions options = new HttpServerOptions()
      .setSsl(true)
      .setUseAlpn(true)
      .setKeyStoreOptions(new JksOptions()
        .setPath("tls/server-keystore.jks")
        .setPassword("wibble"));
    vertx.createHttpServer(options).requestHandler(rpcServer).listen(8080)
      .onFailure(cause -> {
        cause.printStackTrace();
      });
  }
}
