import { IsString } from "class-validator";

export class RegisterRequestDto {

  @IsString()
  email: string;

  @IsString()
  password: string;

  @IsString()
  firstName: string;

  @IsString()
  lastName: string;
}