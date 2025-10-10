import { CommandRouteDto } from "src/backend/carpool/dto";

export class CreateCarpoolDto {
  driverId: string;
  seats: number;
  arrivalTime: Date;
  route: CommandRouteDto;
}