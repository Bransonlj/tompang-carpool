import { Type } from "class-transformer";
import { IsDate, IsNumber, IsString, ValidateNested } from "class-validator";
import { RouteDto } from "./route.dto";
import { RideRequestSummaryDto } from "./ride-reqeust-response.dto";

export class CarpoolSummaryDto {
  @IsString()
  id: string;

  @IsNumber()
  totalSeats: number;

  @IsNumber()
  seatsAssigned: number;

  @IsString()
  driverId: string;

  @IsDate()
  @Type(() => Date) // ensures string -> Date
  arrivalTime: Date;

  @ValidateNested()
  @Type(() => RouteDto)
  route: RouteDto;
}

export class CarpoolDetailedDto extends CarpoolSummaryDto {
  @ValidateNested({ each: true })   // list of nested DTOs
  @Type(() => RideRequestSummaryDto)
  pendingRequests: RideRequestSummaryDto[];

  @ValidateNested({ each: true })
  @Type(() => RideRequestSummaryDto)
  confirmedRequests: RideRequestSummaryDto[];
}