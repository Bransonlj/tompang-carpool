import type { CarpoolDetail, CarpoolSummary } from "./types";

export async function getCarpoolsByUser(userId: string): Promise<CarpoolSummary[]> {
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

export async function getCarpoolById(carpoolId: string): Promise<CarpoolDetail> {
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