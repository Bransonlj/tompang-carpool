import { CommandRouteDto } from "../command-route.dto";

export type CreateCarpoolCommand = {
  driverId: string;
  seats: number;
  arrivalTime: Date;
  route: CommandRouteDto;
}