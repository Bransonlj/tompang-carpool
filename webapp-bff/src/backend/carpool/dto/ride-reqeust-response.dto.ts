import { Type } from "class-transformer";
import { IsDate, IsNumber, IsString, ValidateNested } from "class-validator";
import { RouteDto } from "./route.dto";

export class RideRequestSummaryDto {
  @IsString()
  id: string;

  @IsNumber()
  passengers: number;

  @IsString()
  riderId: string;

  @IsDate()
  @Type(() => Date)
  startTime: Date;

  @IsDate()
  @Type(() => Date)
  endTime: Date;

  @ValidateNested()
  @Type(() => RouteDto)
  route: RouteDto;

  @IsString()
  status: string;
}