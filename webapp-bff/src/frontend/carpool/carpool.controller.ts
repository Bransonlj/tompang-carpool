import { Body, Controller, Get, Headers, Param, Post } from '@nestjs/common';
import { CarpoolService } from 'src/backend/carpool/carpool.service';
import { AcceptCarpoolDto, CarpoolDetail, CarpoolSummary, CreateCarpoolDto, DeclineCarpoolDto } from './dto';
import { UserService } from 'src/backend/user/user.service';

@Controller('api/carpool')
export class CarpoolController {
  constructor(
    private carpoolService: CarpoolService,
    private userService: UserService,
  ) {}

  @Get("user/:id")
  async getCarpoolsByUser(@Param('id') userId: string, @Headers("Authorization") authHeader: string): Promise<CarpoolSummary[]> {
    const carpools = await this.carpoolService.getCarpoolsByUserId(userId, authHeader);
    return carpools.map(carpool => ({
      id: carpool.id,
      seatsAssigned: carpool.seatsAssigned,
      totalSeats: carpool.totalSeats,
      arrivalTime: carpool.arrivalTime,
      originAddress: carpool.route.origin.address,
      destinationAddress: carpool.route.destination.address,
      originImageUrl: carpool.originImageUrl || undefined,
      destinationImageUrl: carpool.destinationImageUrl || undefined,
    }))
  }

  @Get(":id")
  async getCarpoolById(@Param('id') carpoolId: string, @Headers("Authorization") authHeader: string): Promise<CarpoolDetail> {
    const carpool = await this.carpoolService.getCarpoolById(carpoolId, authHeader);
    const passengerIds = new Set([
      ...carpool.pendingRequests.map(ride => ride.riderId), 
      ...carpool.confirmedRequests.map(ride => ride.riderId)
    ]);
    const userProfileIdMap = await this.userService.getUserProfilesFromIdsByBatch({ ids: Array.from(passengerIds), includePhoto: false }, authHeader);
    const confirmedRides: CarpoolDetail["confirmedRides"] = carpool.confirmedRequests.map(ride => ({
      id: ride.id,
      passengers: ride.passengers,
      originAddress: ride.route.origin.address,
      destinationAddress: ride.route.destination.address,
      startTime: ride.startTime,
      endTime: ride.endTime,
      status: ride.status,
      originImageUrl: ride.originImageUrl || undefined,
      destinationImageUrl: ride.destinationImageUrl || undefined,
      rider: {
        id: ride.riderId,
        name: Boolean(userProfileIdMap[ride.riderId]) 
          ? userProfileIdMap[ride.riderId].fullName
          : "[Unknown]"
      },
    }));

    const pendingRequests: CarpoolDetail["pendingRequests"] = carpool.pendingRequests.map(ride => ({
      id: ride.id,
      passengers: ride.passengers,
      originAddress: ride.route.origin.address,
      destinationAddress: ride.route.destination.address,
      startTime: ride.startTime,
      endTime: ride.endTime,
      status: ride.status,
      originImageUrl: ride.originImageUrl || undefined,
      destinationImageUrl: ride.destinationImageUrl || undefined,
      rider: {
        id: ride.riderId,
        name: Boolean(userProfileIdMap[ride.riderId]) 
          ? userProfileIdMap[ride.riderId].fullName
          : "[Unknown]"
      },
    }));

    return {
      id: carpool.id,
      seatsAssigned: carpool.seatsAssigned,
      totalSeats: carpool.totalSeats,
      arrivalTime: carpool.arrivalTime,
      originAddress: carpool.route.origin.address,
      originLatLng: {
        lat: carpool.route.origin.latitude,
        lng: carpool.route.origin.longitude,
      },
      destinationAddress: carpool.route.destination.address,
      destinationLatLng: {
        lat: carpool.route.destination.latitude,
        lng: carpool.route.destination.longitude,
      },
      originImageUrl: carpool.originImageUrl || undefined,
      destinationImageUrl: carpool.destinationImageUrl || undefined,
      confirmedRides,
      pendingRequests,
    };
  }

  @Post()
  async createCarpool(
    @Body() dto: CreateCarpoolDto, 
    @Headers("Authorization") authHeader: string
  ): Promise<void> {
    return await this.carpoolService.createCarpool(dto, authHeader);
  }

  @Post("request/accept")
  async acceptCarpoolRequest(
    @Body() dto: AcceptCarpoolDto,
    @Headers("Authorization") authHeader: string
  ): Promise<void> {
    return await this.carpoolService.acceptRequest(dto, authHeader);
  }

  @Post("request/decline")
  async declineCarpoolRequest(
    @Body() dto: DeclineCarpoolDto,
    @Headers("Authorization") authHeader: string
  ): Promise<void> {
    return await this.carpoolService.declineRequest(dto, authHeader);
  }
}
