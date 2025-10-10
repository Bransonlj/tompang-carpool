import { Type } from "class-transformer";
import { IsDate, IsNumber, IsString, ValidateNested } from "class-validator";
import { RouteDto } from "./route.dto";
import { CarpoolSummaryDto } from "./carpool-response.dto";

export type RideRequestStatus = "PENDING" | "ASSIGNED" | "FAILED";

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
  status: RideRequestStatus;
}

export class RideRequestDetailedDto extends RideRequestSummaryDto {
  @ValidateNested({ each: true })   // list of nested DTOs
  @Type(() => CarpoolSummaryDto)
  matchedCarpools: CarpoolSummaryDto[];

  @ValidateNested()
  @Type(() => RideRequestSummaryDto)
  assignedCarpool: CarpoolSummaryDto | null;
}
