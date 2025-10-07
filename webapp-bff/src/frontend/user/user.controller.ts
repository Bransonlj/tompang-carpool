import { Body, Controller, Get, Param, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { UserProfile } from './dto';
import { FileInterceptor } from '@nestjs/platform-express';

@Controller('api/user')
export class UserController {

  @Get(":id")
  async getUserById(@Param("id") userId: string): Promise<UserProfile> {
    return {
      id: "u23b",
      name: "test user",
      profileImgUrl: "buhhuhasdbse"
    }
  }

  @Post('profile/pic')
  @UseInterceptors(FileInterceptor('file'))
  async uploadProfilePicture(
    @UploadedFile() file: Express.Multer.File,
    @Body('id') userId: string
  ) {
    console.log(`uploaded: ${file.filename} ${file.size} ${userId}`);
  }
}
