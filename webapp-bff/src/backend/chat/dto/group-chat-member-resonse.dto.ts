import { IsString } from "class-validator";

export type UserTitle = "RIDER" | "DRIVER";


export class GroupChatMemberResponseDto {
  @IsString()
  userId: string;

  @IsString()
  userTitle: UserTitle;
}