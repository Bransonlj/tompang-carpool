import api, { authHeader } from "../../client/http";
import type { GroupChatDataResponseDto, SendMessageDto } from "./types";

async function sendChatMessage(dto: SendMessageDto, token: string): Promise<void> {
  return await api.post("api/chat/send", dto, authHeader(token));
}

async function getGroupChatData(groupId: string, token: string): Promise<GroupChatDataResponseDto> {
  return await api.get(`api/chat/group/${groupId}`, authHeader(token));
}

const ChatService = {
  sendChatMessage,
  getGroupChatData,
};

export default ChatService;