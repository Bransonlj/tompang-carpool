import { Body, Controller, Get, Headers, Param, Post } from '@nestjs/common';
import { RideRequestDetail, RideRequestSummary } from './dto';
import { CarpoolService } from 'src/backend/carpool/carpool.service';
import { UserService } from 'src/backend/user/user.service';
import { RideRequestService } from 'src/backend/carpool/ride-request.service';
import { CreateRideRequestDto } from './dto/create-ride-request.dto';

@Controller('api/ride-request')
export class RideRequestController {
  constructor(
    private rideRequestService: RideRequestService,
    private userService: UserService,
  ) {}

  @Get("user/:id")
  async getRideRequestsByUser(@Param('id') userId: string, @Headers("Authorization") authHeader: string): Promise<RideRequestSummary[]> {
    const rideRequest = await this.rideRequestService.getRideRequestsByRiderId(userId, authHeader);
    return rideRequest.map(ride => ({
      id: ride.id,
      passengers: ride.passengers,
      originAddress: ride.route.origin.address,
      destinationAddress: ride.route.destination.address,
      startTime: ride.startTime,
      endTime: ride.endTime,
      status: ride.status,
    }));
  }

  @Get(":id")
  async getRideRequestById(@Param('id') requestId: string, @Headers("Authorization") authHeader: string): Promise<RideRequestDetail> {
    const rideRequest = await this.rideRequestService.getRideRequestById(requestId, authHeader);
    const driverIds = new Set([
      ...rideRequest.matchedCarpools.map(carpool => carpool.driverId), 
      ...(rideRequest.assignedCarpool?.driverId ? [rideRequest.assignedCarpool.driverId] : []),
    ]);
    const userProfileIdMap = await this.userService.getUserProfilesFromIdsByBatch(driverIds, authHeader);
    const assignedCarpool: RideRequestDetail["assignedCarpool"] = !!rideRequest.assignedCarpool
      ? {
        id: rideRequest.assignedCarpool.id,
        seatsAssigned: rideRequest.assignedCarpool.seatsAssigned,
        totalSeats: rideRequest.assignedCarpool.totalSeats,
        arrivalTime: rideRequest.assignedCarpool.arrivalTime,
        originAddress: rideRequest.assignedCarpool.route.origin.address,
        destinationAddress: rideRequest.assignedCarpool.route.destination.address,
        driver: {
          id: rideRequest.assignedCarpool.driverId,
          name: Boolean(userProfileIdMap[rideRequest.assignedCarpool.driverId]) 
            ? userProfileIdMap[rideRequest.assignedCarpool.driverId].fullName
            : "[Unknown]"
        },
      } : null;

    const pendingCarpools: RideRequestDetail["pendingCarpools"] = rideRequest.matchedCarpools.map(carpool => ({
        id: carpool.id,
        seatsAssigned: carpool.seatsAssigned,
        totalSeats: carpool.totalSeats,
        arrivalTime: carpool.arrivalTime,
        originAddress: carpool.route.origin.address,
        destinationAddress: carpool.route.destination.address,
        driver: {
          id: carpool.driverId,
          name: Boolean(userProfileIdMap[carpool.driverId]) 
            ? userProfileIdMap[carpool.driverId].fullName
            : "[Unknown]"
        },
    }));

    return {
      id: rideRequest.id,
      passengers: rideRequest.passengers,
      originAddress: rideRequest.route.origin.address,
      originLatLng: {
        lat: rideRequest.route.origin.latitude,
        lng: rideRequest.route.origin.longitude,
      },
      destinationAddress: rideRequest.route.destination.address,
      destinationLatLng: {
        lat: rideRequest.route.destination.latitude,
        lng: rideRequest.route.destination.longitude,
      },
      startTime: rideRequest.startTime,
      endTime: rideRequest.endTime,
      status: rideRequest.status,
      pendingCarpools,
      assignedCarpool,
    }
  }

  @Post()
  async createRideRequest(
    @Body() dto: CreateRideRequestDto, 
    @Headers("Authorization") authHeader: string
  ): Promise<void> {
    console.log("buh")
    return await this.rideRequestService.createRideRequest(dto, authHeader);
  }
}
