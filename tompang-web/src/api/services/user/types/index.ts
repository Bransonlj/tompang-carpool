export type UserProfile = {
  id: string;
  name: string;
  profileImgUrl: string;
}

export type uploadProfilePictureDto = {
  file: File;
  userId: string;
  token: string;
}