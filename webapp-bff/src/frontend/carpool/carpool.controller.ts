import { Controller, Get, Param } from '@nestjs/common';
import { CarpoolService } from 'src/backend/carpool/carpool.service';
import { CarpoolDetail, CarpoolSummary } from './dto';

@Controller('api/carpool')
export class CarpoolController {
  constructor(
    private carpoolService: CarpoolService
  ) {}

  @Get("user/:id")
  async getCarpoolsByUser(@Param('id') userId: string): Promise<CarpoolSummary[]> {
    return [
      {
        id: "123",
        originAddress: "green tower block 44 upper low street",
        destinationAddress: "white harbour road post office building B",
        arrivalTime: new Date().toISOString(),
        totalSeats: 5,
        seatsAssigned: 2,
      },
      {
        id: "456",
        originAddress: "22 blue street",
        destinationAddress: "red robin",
        arrivalTime: new Date().toISOString(),
        totalSeats: 5,
        seatsAssigned: 1,
      }
    ];
  }

  @Get(":id")
  async getCarpoolById(@Param('id') carpoolId: string): Promise<CarpoolDetail> {
    return {
      id: "123",
      originAddress: "green tower block 44 upper low street",
      destinationAddress: "white harbour road post office building B",
      arrivalTime: new Date().toISOString(),
      totalSeats: 5,
      seatsAssigned: 2,
      pendingRequests: [
        {
          id: "456",
          originAddress: "green tower block 44 upper low street",
          destinationAddress: "white harbour road post office building B",
          startTime: new Date().toISOString(),
          endTime: new Date().toISOString(),
          passengers: 2,
          status: 'PENDING',
          rider: {
            id: "rider-222",
            name: "bobby",
          }
        },
        {
          id: "123",
          originAddress: "green tower block 44",
          destinationAddress: "white harbour road",
          startTime: new Date().toISOString(),
          endTime: new Date().toISOString(),
          passengers: 2,
          status: 'PENDING',
          rider: {
            id: "rider-213",
            name: "tommy",
          }
        },
      ],
      confirmedRides: [
        {
          id: "456",
          originAddress: "green tower block 44 upper low street",
          destinationAddress: "white harbour road post office building B",
          startTime: new Date().toISOString(),
          endTime: new Date().toISOString(),
          passengers: 2,
          status: 'PENDING',
          rider: {
            id: "rider-222",
            name: "bobby",
          }
        },
        {
          id: "123",
          originAddress: "green tower block 44",
          destinationAddress: "white harbour road",
          startTime: new Date().toISOString(),
          endTime: new Date().toISOString(),
          passengers: 2,
          status: 'PENDING',
          rider: {
            id: "rider-213",
            name: "tommy",
          }
        },
      ],
    }
  }
}
