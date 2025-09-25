import { Controller, Get, Param } from '@nestjs/common';
import { RideRequestDetail, RideRequestSummary } from './dto';

@Controller('api/ride-request')
export class RideRequestController {

  @Get("user/:id")
  async getRideRequestsByUser(@Param('id') userId: string): Promise<RideRequestSummary[]> {
    return [
      {
        id: "123",
        originAddress: "green tower block 44",
        destinationAddress: "white harbour road",
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        passengers: 5,
        status: 'ASSIGNED',
      },
      {
        id: "456",
        originAddress: "green tower block 44 upper low street",
        destinationAddress: "white harbour road post office building B",
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        passengers: 5,
        status: 'ASSIGNED',
      }
    ]
  }

  @Get(":id")
  async  getRideRequestById(@Param('id') requestId: string): Promise<RideRequestDetail> {
    if (requestId === "456") {
      return {
        id: "456",
        originAddress: "green tower block 44 upper low street",
        destinationAddress: "white harbour road post office building B",
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        passengers: 3,
        status: 'ASSIGNED',
        assignedCarpool: {
          id: "123",
          originAddress: "22 blue street",
          destinationAddress: "red robin",
          arrivalTime: new Date().toISOString(),
          totalSeats: 5,
          seatsAssigned: 4,
          driver: {
            id: "driver-222",
            name: "drivy-billy",
          }
        },
        pendingCarpools: []
      }
    } else {
      return {
        id: "123",
        originAddress: "green tower block 44",
        destinationAddress: "white harbour road",
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        passengers: 5,
        status: 'PENDING',
        pendingCarpools: [
          {
            id: "456",
            originAddress: "22 blue street",
            destinationAddress: "red robin",
            arrivalTime: new Date().toISOString(),
            totalSeats: 5,
            seatsAssigned: 4,
            driver: {
              id: "driver-222",
              name: "drivy-billy",
            }
          }, {
            id: "123",
            originAddress: "green tower block 44 upper low street",
            destinationAddress: "white harbour road post office building B",
            arrivalTime: new Date().toISOString(),
            totalSeats: 5,
            seatsAssigned: 2,
            driver: {
              id: "driver-333",
              name: "radabaga",
            }
          },
        ]
      }
    }
  }
}
