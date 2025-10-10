import { Body, Controller, Get, Headers, Param, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { UserProfile } from './dto';
import { FileInterceptor } from '@nestjs/platform-express';
import { UserService } from 'src/backend/user/user.service';

@Controller('api/user')
export class UserController {

  constructor(
    private userService: UserService,
  ) {}

  @Get(":id")
  async getUserById(@Param("id") userId: string, @Headers("Authorization") authHeader: string): Promise<UserProfile> {
    const profile = await this.userService.getUserProfileById(userId, authHeader);
    return {
      id: profile.id,
      name: profile.fullName,
      profileImgUrl: profile.profilePictureUrl ?? undefined,
    }
  }

  @Post('profile/pic')
  @UseInterceptors(FileInterceptor('file'))
  async uploadProfilePicture(
    @UploadedFile() file: Express.Multer.File,
    @Body('id') userId: string,
    @Headers("Authorization") authHeader: string,
  ) {
    return await this.userService.uploadUserProfilePicture(file, userId, authHeader);
  }
}
