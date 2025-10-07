import api, { authHeader } from "../../client/http";
import type { MessageResponseDto, SendMessageDto } from "./types";

async function sendChatMessage(dto: SendMessageDto, token: string): Promise<void> {
  return await api.post("api/chat/send", dto, authHeader(token));
}

async function getGroupMessages(groupId: string, token: string): Promise<MessageResponseDto[]> {
  return await api.get(`api/chat/messages/${groupId}`, authHeader(token));
}

const ChatService = {
  sendChatMessage,
  getGroupMessages,
};

export default ChatService;