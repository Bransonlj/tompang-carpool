export type UserProfile = {
  id: string;
  name: string;
  profileImgUrl: string | undefined;
}

export type uploadProfilePictureDto = {
  file: File;
  userId: string;
  token: string;
}