import { Body, Controller, Post } from '@nestjs/common';
import { LoginRequestDto, LoginResponseDto, RegisterRequestDto } from 'src/backend/user/dto';
import { UserService } from 'src/backend/user/user.service';

@Controller('api/auth')
export class AuthController {

  constructor(
    private userService: UserService,
  ) {}

  @Post("login")
  async login(@Body() dto: LoginRequestDto): Promise<LoginResponseDto> {
    return await this.userService.login(dto);
  }

  @Post("register")
  async register(@Body() dto: RegisterRequestDto): Promise<void> {
    return await this.userService.register(dto);
  }
}
