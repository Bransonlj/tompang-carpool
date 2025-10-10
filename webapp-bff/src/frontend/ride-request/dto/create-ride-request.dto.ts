import { CommandRouteDto } from "src/backend/carpool/dto";

export class CreateRideRequestDto {
  riderId: string;
  passengers: number;
  startTime: Date;
  endTime: Date;
  route: CommandRouteDto;
}