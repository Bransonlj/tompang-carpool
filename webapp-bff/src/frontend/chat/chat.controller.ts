import { Body, Controller, Get, Headers, Param, Post } from '@nestjs/common';
import { GroupChatDataResponseDto, MembersMap } from './dto';
import { ChatService } from 'src/backend/chat/chat.service';
import { UserService } from 'src/backend/user/user.service';
import { SendMessageDto } from 'src/backend/chat/dto';

@Controller('api/chat')
export class ChatController {

  constructor(
    private chatService: ChatService,
    private userService: UserService,
  ) {}

  @Get("group/:gid")
  async getGroupMessages(@Param("gid") groupId: string, @Headers("Authorization") authHeader: string): Promise<GroupChatDataResponseDto> {
    const groupChatData = await this.chatService.getGroupChatData(groupId, authHeader);
    const userIds = new Set(groupChatData.members.map(member => member.userId));
    const userProfiles = await this.userService.getUserProfilesFromIdsByBatch({ ids: Array.from(userIds), includePhoto: true }, authHeader);

    const members: MembersMap = groupChatData.members.reduce((acc, member) => {
      acc[member.userId] = {
        senderName: userProfiles[member.userId]?.fullName ?? "Unknown",
        senderTitle: member.userTitle,
        senderProfilePicture: userProfiles[member.userId]?.profilePictureUrl ?? undefined,
      };
      return acc;
    }, {} as MembersMap);
    return {
      groupId,
      members,
      messages: groupChatData.messages,
    };
  }

  @Post("send")
  async sendChatMessage(@Body() dto: SendMessageDto, @Headers("Authorization") authHeader: string): Promise<void> {
    return await this.chatService.sendMessage(dto, authHeader);
  }

}
