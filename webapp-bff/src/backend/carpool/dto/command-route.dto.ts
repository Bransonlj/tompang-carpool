/**
 * Route dto for commands which contain only *latitude* & *longitude* for *origin* & *destination*.
 */
export type CommandRouteDto = {
  origin: {
    latitude: number;
    longitude: number;
  };

  destination: {
    latitude: number;
    longitude: number;
  };
};