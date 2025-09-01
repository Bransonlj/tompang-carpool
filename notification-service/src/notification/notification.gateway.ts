import { OnGatewayConnection, OnGatewayDisconnect, OnGatewayInit, WebSocketGateway, WebSocketServer } from "@nestjs/websockets";
import { Server, Socket } from "socket.io";

interface AuthenticatedSocket extends Socket {
  userId: string;
}

@WebSocketGateway() // cors in gateway
export class NotificationGateway implements OnGatewayInit, OnGatewayConnection, OnGatewayDisconnect {

  @WebSocketServer()
  server: Server;

  afterInit(server: Server) {
    // add middleware to verify headers
    server.use((socket: AuthenticatedSocket, next) => {
      const userIdHeader = socket.handshake.headers["x-user-id"];
      let userId: string;
      if (userIdHeader === undefined) {
        return next(new Error("invalid x-user-id header"));
      }

      if (Array.isArray(userIdHeader)) {
        if (userIdHeader.length === 0) {
          return next(new Error("invalid x-user-id header"));
        }

        userId = userIdHeader[0];
      } else {
        userId = userIdHeader;
      }

      // attach to client socket
      socket.userId = userId;
      next();
    })
  }

  handleConnection(client: AuthenticatedSocket, ...args: any[]) {
    const userId = client.userId
    console.log("user connected", userId);
    // join the right room
    client.join(userId);
  }

  handleDisconnect(client: AuthenticatedSocket) {
    console.log("user disconnected", client.userId)
  }

  sendNotification(message: { title: string, message: string }, userId: string) {
    this.server.to(userId).emit("notification", message);
  }

}