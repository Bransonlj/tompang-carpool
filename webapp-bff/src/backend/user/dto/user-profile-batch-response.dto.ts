import { UserProfileResponseDto } from "./user-profile-response.dto"

export class UserProfileBatchResponseDto {
  [id: string]: UserProfileResponseDto;
}