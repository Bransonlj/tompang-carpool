import { Body, Controller, Post } from '@nestjs/common';
import { LoginRequestDto, LoginResponseDto, RegisterRequestDto } from './dto';

@Controller('api/auth')
export class AuthController {

  @Post("login")
  async login(@Body() dto: LoginRequestDto): Promise<LoginResponseDto> {
    return {
      userId: "test-user-id",
      token: "huh",
      roles: ["USER", "DRIVER", "ADMIN"],
    }
  }

  @Post("register")
  async register(@Body() dto: RegisterRequestDto): Promise<void> {
    
  }
}
