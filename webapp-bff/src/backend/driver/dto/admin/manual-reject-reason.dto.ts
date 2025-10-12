import { IsString } from "class-validator";

export class ManualRejectRequestDto {
  @IsString()
  rejectReason: string;
}