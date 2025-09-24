import type { UserProfile } from "./types";

export async function getUserById(userId: string): Promise<UserProfile> {
  return {
    id: "u23b",
    name: "johny fairplay",
    profileImgUrl: "buhhuhasdbse"
  }
}