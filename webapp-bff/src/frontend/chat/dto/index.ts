import { UserTitle } from "src/backend/chat/dto";

export class ChatMessage {
  messageId: string;
  createdAt: Date;
  senderId: string;
  message: string;
}

export type MembersMap = {
  [id: string]: {
    senderName: string;
    senderTitle: UserTitle;
    senderProfilePicture: string | undefined;
  };
};

export class GroupChatDataResponseDto {
  groupId: string;
  members: MembersMap;
  messages: ChatMessage[];
}
