import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { MessageResponseDto, SendMessageDto } from './dto';

@Controller('api/chat')
export class ChatController {

  @Get("messages/:gid")
  async getGroupMessages(@Param("gid") groupId: string): Promise<MessageResponseDto[]> {
    return [
      {
        groupId: "carpool-123",
        messageId: "1",
        senderId: "user-123",
        senderName: "Bob Tan",
        senderTitle: "DRIVER",
        createdAt: new Date(),
        message: "welcome to my carpool",
      },
      {
        groupId: "carpool-123",
        messageId: "2",
        senderId: "test-user-id",
        senderName: "Testy Tester",
        senderTitle: "RIDER",
        createdAt: new Date(),
        message: "thanks for accepting me",
      },
      {
        groupId: "carpool-123",
        messageId: "3",
        senderId: "user-345",
        senderName: "User Bob",
        senderTitle: "RIDER",
        createdAt: new Date(),
        message: "hello",
      },
            {
        groupId: "carpool-123",
        messageId: "3",
        senderId: "user-345",
        senderName: "User Bob",
        senderTitle: "RIDER",
        createdAt: new Date(),
        message: "hello",
      },
            {
        groupId: "carpool-123",
        messageId: "3",
        senderId: "user-345",
        senderName: "User Bob",
        senderTitle: "RIDER",
        createdAt: new Date(),
        message: "hello",
      },

    ]
  }

  @Post("send")
  async sendChatMessage(@Body() dto: SendMessageDto): Promise<void> {
    
  }

}
