import { CommandRouteDto } from "../command-route.dto";

export type CreateRideRequestCommand = {
  riderId: string;
  passengers: number;
  startTime: Date;
  endTime: Date;
  route: CommandRouteDto;
}