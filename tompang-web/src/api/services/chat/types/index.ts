export type SendMessageDto = {
  groupId: string;
  senderId: string;
  message: string;
}

export type SenderTitle = "RIDER" | "DRIVER";

export type ChatMessage = {
  messageId: string;
  createdAt: string;
  senderId: string;
  message: string;
}

export type GroupChatDataResponseDto = {
  groupId: string;
  members: {
    [id: string]: {
      senderName: string;
      senderTitle: SenderTitle;
      senderProfilePicture: string | undefined;
    }
  };
  messages: ChatMessage[];
}