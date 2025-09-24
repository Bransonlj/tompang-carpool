import { Type } from "class-transformer";
import { IsNumber, IsString, ValidateNested } from "class-validator";

class LocationDto {
  @IsNumber()
  latitude: number;
  @IsNumber()
  longitude: number;
  @IsString()
  address: string;
}

export class RouteDto {
  @ValidateNested()
  @Type(() => LocationDto)
  origin: LocationDto;
  
  @ValidateNested()
  @Type(() => LocationDto)
  destination: LocationDto;
}