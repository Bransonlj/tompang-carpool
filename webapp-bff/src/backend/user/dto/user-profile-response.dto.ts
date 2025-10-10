import { IsString } from "class-validator";

export class UserProfileResponseDto {

    @IsString()
    id: string;

    @IsString()
    firstName: string;

    @IsString()
    lastName: string;

    @IsString()
    fullName: string;

    @IsString()
    email: string;

    // null if user not a driver
    driverId: string | null;
    profilePictureUrl: string | null;
}