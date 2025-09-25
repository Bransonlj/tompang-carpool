import { Body, Controller, Post } from '@nestjs/common';
import { LoginRequestDto, LoginResponseDto, RegisterRequestDto } from './dto';

@Controller('api/auth')
export class AuthController {

  @Post("login")
  async login(@Body() dto: LoginRequestDto): Promise<LoginResponseDto> {
    return {
      userId: "buh",
      token: "huh",
      roles: ["USER", "DRIVER"],
    }
  }

  @Post("register")
  async register(@Body() dto: RegisterRequestDto): Promise<void> {
    
  }
}
