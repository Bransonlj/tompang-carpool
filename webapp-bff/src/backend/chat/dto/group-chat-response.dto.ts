import { IsString, ValidateNested } from "class-validator";
import { Type } from "class-transformer";
import { ChatMessageResponseDto } from "./chat-message-response.dto";
import { GroupChatMemberResponseDto } from "./group-chat-member-resonse.dto";

export class GroupChatResponseDto {
  @IsString()
  groupId: string;

  @ValidateNested({ each: true })   // list of nested DTOs
  @Type(() => ChatMessageResponseDto)
  messages: ChatMessageResponseDto[];

  @ValidateNested({ each: true })   // list of nested DTOs
  @Type(() => GroupChatMemberResponseDto)
  members: GroupChatMemberResponseDto[];
}