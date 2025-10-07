import api, { authHeader } from "../../client/http";
import type { UserNotificationDto } from "./types";

async function getUserNotifications(userId: string, token: string): Promise<UserNotificationDto[]> {
  return api.get(`/api/notification/user/${userId}`, authHeader(token));
}

const NotificationService = {
  getUserNotifications,
};

export default NotificationService;
